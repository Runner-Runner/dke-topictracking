package postProcessing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Stream;

import container.Vocabulary;
import tools.Utils;

public class WordDistributions {

	final int numTimeSteps;

	// topic list of timestep lists of word distributions
	private ArrayList<ArrayList<ArrayList<Float>>> topicTimestepWordDistributions;
	
	public int getNumTimeSteps()
	{
		return numTimeSteps;
	}
	
	public ArrayList<ArrayList<ArrayList<Float>>> getTopicTimestepWordDistributions() 
	{
		return topicTimestepWordDistributions;
	}
	
	public ArrayList<Float> getWordDistribution(final int topicId, 
												final int timestep)
	{
		return topicTimestepWordDistributions.get(topicId).get(timestep);
	}

	private Integer lineIndex = 0;
	
	public WordDistributions(final int numTimesteps, 
							final String betaFilename)
	{
		assert(numTimesteps > 0);

		this.numTimeSteps = numTimesteps;
		
		topicTimestepWordDistributions = new ArrayList<>();
		
		loadTopicWordDistributions(betaFilename);
		
		System.out.println("[DTMWordDistributions] initialization done.");
	}
	
	public WordDistributions(final int numTimesteps)
	{
		assert(numTimesteps > 0);

		this.numTimeSteps = numTimesteps;

		topicTimestepWordDistributions = new ArrayList<>();

		System.out.println("[DTMWordDistributions] initialization done.");
	}
	
	
	public String[][] getTopicsAsWordsForTimeStep(Vocabulary vocabulary, 
			int numTopWords,
			int timestep)
	{
		LinkedList<String> vocabularyList = vocabulary.getAsList();
		int numTopics = topicTimestepWordDistributions.size();
		
		if (numTopWords < 1)
			numTopWords = vocabularyList.size();
		
		String[][] wordLists = new String[numTopics][numTopWords];
		
		for (int topicId = 0; topicId < numTopics; ++topicId)
		{
//			ArrayList<Float> topicWords = getWordDistribution(topicId, timestep);
//			
//			// create map with explicit id to get implicit id after sorting
//			LinkedHashMap<Integer, Float> wordIdsAndWeigths = new LinkedHashMap<Integer, Float>();
//			for (int wordId = 0; wordId < topicWords.size(); ++wordId)
//			{
//				wordIdsAndWeigths.put(wordId, topicWords.get(wordId));
//			}
//			
//			wordIdsAndWeigths = Utils.sortByValue(wordIdsAndWeigths);
//			
//			int topWord = 0;
//			for (Integer wordId : wordIdsAndWeigths.keySet()) 
//			{
//				if (topWord < numTopWords)
//				{
//					String word = vocabularyList.get(wordId);
//					wordLists[topicId][topWord] = word;
//				}
//				else
//				{
//					break;
//				}
//				++topWord;
//			}
			
			wordLists[topicId] = getWordListForTopic(vocabulary, numTopWords, timestep, topicId);
		}
		
		return wordLists;
		
	}
	
	public String[] getWordListForTopic(Vocabulary vocabulary, 
			int numTopWords,
			int timestep,
			int topicId)
	{
		String[] wordList = new String[numTopWords];
		
		ArrayList<Float> topicWords = getWordDistribution(topicId, timestep);

		LinkedList<String> vocabularyList = vocabulary.getAsList();

		// create map with explicit id to get implicit id after sorting
		LinkedHashMap<Integer, Float> wordIdsAndWeigths = new LinkedHashMap<Integer, Float>();
		for (int wordId = 0; wordId < topicWords.size(); ++wordId)
		{
			wordIdsAndWeigths.put(wordId, topicWords.get(wordId));
		}
		
		wordIdsAndWeigths = Utils.sortByValue(wordIdsAndWeigths);
		
		int topWord = 0;
		for (Integer wordId : wordIdsAndWeigths.keySet()) 
		{
			if (topWord < numTopWords)
			{
				String word = vocabularyList.get(wordId);
				wordList[topWord] = word;
			}
			else
			{
				break;
			}
			++topWord;
		}
		
		return wordList;
	}
	
	private void loadTopicWordDistributions(final String filename)
	{
		if (Files.exists(Paths.get(filename))) 
		{
			synchronized (lineIndex) {
				lineIndex = 0;
			}

			try (Stream<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset()))
			{
				System.out.println("[DTMWordDistributions::loadTopicWordDistributions] Loading data from " + filename);
				
				lines.forEachOrdered(line -> readLine(line));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("ERROR: file does not exist: " + filename);
		}
	}
	
	private void readLine(final String line)
	{
		String[] parts = line.split(" ");
		ArrayList<Float> liWordDistribution = new ArrayList<Float>();
		
		for (int i = 0; i < parts.length; i++)
		{
			float wordProb = Float.parseFloat(parts[i]);
			liWordDistribution.add(wordProb);
		}

		synchronized (topicTimestepWordDistributions)
		{
			synchronized (lineIndex)
			{
			
				int currentTopic = lineIndex / numTimeSteps;
				if (topicTimestepWordDistributions.size() <= currentTopic)
				{
					ArrayList<ArrayList<Float>> liTimesteps = new ArrayList<>(numTimeSteps); 
					
					topicTimestepWordDistributions.add(liTimesteps);
				}
				
				int currentTimestep = lineIndex % numTimeSteps;
				assert(topicTimestepWordDistributions.get(currentTopic).size() == currentTimestep);
				
				topicTimestepWordDistributions.get(currentTopic).add(liWordDistribution);

				++lineIndex;
			}
		}
	}
	
	/**
	 * compute cosine similarities of topic timesteps
	 * 
	 * @return cosine similarity matrix[numTopics][numTimeSteps -1]
	 */
	public double[][] computeIntraTopicSimilarities()
	{
		int sizeLDATopics = getTopicTimestepWordDistributions().size();
		
		double[][] intraTopipcSimiliarity = new double[sizeLDATopics][numTimeSteps -1];
		
		for (int topicId = 0; topicId < sizeLDATopics; ++topicId) 
		{
			for (int timeStep = 0; timeStep < numTimeSteps - 1; ++timeStep) 
			{
				double[] currentWD = Utils.convertToArray(getWordDistribution(topicId, timeStep));
				double[] nextWD = Utils.convertToArray(getWordDistribution(topicId, timeStep + 1));
				
				double sim = Similarities.cosineSimilarity(currentWD, nextWD);
				
				intraTopipcSimiliarity[topicId][timeStep] = sim;
			}
		}
		
		return intraTopipcSimiliarity;
	}
	
	
	/**
	 * compute vector distance of topic timesteps
	 * 
	 * @return distance matrix[numTopics][numTimeSteps -1]
	 */
	public int[][] computeIntraTopicDistances(int numWords)
	{
		int sizeLDATopics = getTopicTimestepWordDistributions().size();
		
		int[][] intraTopipcSimiliarity = new int[sizeLDATopics][numTimeSteps -1];
		
		for (int topicId = 0; topicId < sizeLDATopics; ++topicId) 
		{
			for (int timeStep = 0; timeStep < numTimeSteps - 1; ++timeStep) 
			{
				ArrayList<Float> currentWordList = getWordDistribution(topicId, timeStep);
				ArrayList<Float> nextWordList = getWordDistribution(topicId, timeStep + 1);
				
				assert(currentWordList.size() == nextWordList.size());
				
				if (numWords < 1)
					numWords = currentWordList.size();
				
				LinkedHashMap<Integer, Float> wordListA = Utils.convertListToMap(currentWordList);
				LinkedHashMap<Integer, Float> wordListB = Utils.convertListToMap(nextWordList);

				int distance = Similarities.indexDistance(wordListA, wordListB, numWords);
				
				intraTopipcSimiliarity[topicId][timeStep] = distance;
			}
		}
		
		return intraTopipcSimiliarity;
	}
	
	/**
	 * compute cosine similarities of all topics for a certain timestep
	 * 
	 * @return cosine similiarity matrix[numTopics][numTopics]
	 */
	public double[][] computeInterTopicSimilarities(final int timeStep)
	{
		int sizeLDATopics = getTopicTimestepWordDistributions().size();
		
		double[][] interTopipcSimiliarity = new double[sizeLDATopics][sizeLDATopics];
		
		for (int currentTopicId = 0; currentTopicId < sizeLDATopics; ++currentTopicId) 
		{
			double[] currentWD = Utils.convertToArray(getWordDistribution(currentTopicId, timeStep));
			
			for (int otherTopicId = 0; otherTopicId < sizeLDATopics; ++otherTopicId) 
			{
				if (currentTopicId != otherTopicId)
				{
					double[] otherWD = Utils.convertToArray(getWordDistribution(otherTopicId, timeStep));
					
					double sim = Similarities.cosineSimilarity(currentWD, otherWD);
					
					interTopipcSimiliarity[currentTopicId][otherTopicId] = sim;
				}
				else
				{
					interTopipcSimiliarity[currentTopicId][otherTopicId] = 1.0;
				}
			}
		}
		
		return interTopipcSimiliarity;
	}
	
	/**
	 * compute vector distance of all topics for a certain timestep
	 * 
	 * @return distance matrix[numTopics][numTopics]
	 */
	public int[][] computeInterTopicDistances(final int timeStep,
			int numWords)
	{
		int sizeLDATopics = getTopicTimestepWordDistributions().size();
		
		int[][] interTopipcSimiliarity = new int[sizeLDATopics][sizeLDATopics];
		
		for (int currentTopicId = 0; currentTopicId < sizeLDATopics; ++currentTopicId) 
		{
			ArrayList<Float> currentWordList = getWordDistribution(currentTopicId, timeStep);
			LinkedHashMap<Integer, Float> wordListA = Utils.convertListToMap(currentWordList);
			
			for (int otherTopicId = 0; otherTopicId < sizeLDATopics; ++otherTopicId) 
			{
				if (currentTopicId != otherTopicId)
				{
					ArrayList<Float> nextWordList = getWordDistribution(otherTopicId, timeStep);
					LinkedHashMap<Integer, Float> wordListB = Utils.convertListToMap(nextWordList);
					
					assert(currentWordList.size() == nextWordList.size());
					
					if (numWords < 1)
						numWords = currentWordList.size();
					
					int distance = Similarities.indexDistance(wordListA, wordListB, numWords);
					
					interTopipcSimiliarity[currentTopicId][otherTopicId] = distance;
				}
				else
				{
					interTopipcSimiliarity[currentTopicId][otherTopicId] = 0;
				}
			}
		}
		
		return interTopipcSimiliarity;
	}
	
	/**
	 * collect similar topics according to given threshold cosine similarity 
	 * 
	 * @param threshold	threshold for cosine similarity
	 * @param interTopipcSimiliarity @see computeInterTopicSimilarities
	 * @return list of map containing id and similarity value of similar topics for each topic 
	 */
	public ArrayList<HashMap<Integer, Double> > findSimilarTopics(double threshold, 
			double[][] interTopipcSimiliarity)
	{
		ArrayList<HashMap<Integer, Double> > similarTopics = new ArrayList<HashMap<Integer, Double> >();
		
		for (int currentTopicId = 0; currentTopicId < interTopipcSimiliarity.length; ++currentTopicId) 
		{
			HashMap<Integer, Double> topics = new HashMap<Integer, Double>();
			similarTopics.add(topics);
			
			for (int otherTopicId = 0; otherTopicId < interTopipcSimiliarity[currentTopicId].length; ++otherTopicId) 
			{
				double sim = interTopipcSimiliarity[currentTopicId][otherTopicId];
				if (sim > threshold)
				{
					similarTopics.get(currentTopicId).put(otherTopicId, sim);
				}
			}
		}
		
		return similarTopics;
	}
}
