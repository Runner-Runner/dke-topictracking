package postProcessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.sound.midi.Synthesizer;

import org.omg.Messaging.SyncScopeHelper;

import app.Configuration;
import data.MetaDataInterface;
import data.TopicDistributions;
import data.WordDistributions;
import tools.IOTools;
import tools.Tools;
import wordContainer.Vocabulary;

public class DTMEvaluator 
{
	final MetaDataInterface dataReuters;
	
	final TopicDistributions topicDistributions;

	final WordDistributions wordDistributions;
	
	final int numTimeSteps;

	/**
	 * Postprocessing of LDA output data.
	 * 
	 * @param dataReuters
	 * @param td
	 * @param wd
	 * @param numTimeSteps
	 */
	public DTMEvaluator(final MetaDataInterface dataReuters,
			final TopicDistributions td,
			final WordDistributions wd,
			final int numTimeSteps) 
	{
		this.dataReuters = dataReuters;
		
		this.topicDistributions = td;
		
		this.wordDistributions = wd;
		
		this.numTimeSteps = numTimeSteps;
		
		System.out.println("[DTMEvaluator] initialization done.");
	}
	
	/**
	 * Computes topic weights.
	 * Weight is either normalized score for each topic over all documents
	 *  or sum of documents, where the topic value is above threshold.
	 *  
	 * @param scoreInsteadOfDocNumbers	normalized score ?
	 * @param threshold
	 * @return topicWeightsPerTimetep[numTopics][numTimeSteps]
	 */
	public float [][] computeTopicsWithDocsPerTime(final boolean scoreInsteadOfDocNumbers,
			final float threshold)
	{
		int numTopics = topicDistributions.getDocumentsPerTopics().size();
		
		float [][] weightsTopicsPerTimesteps = new float [numTopics][numTimeSteps];
		
		for (Integer index = 0; index < numTopics; index++)
		{
			HashMap<Integer, Float> docs = topicDistributions.getDocumentsAndWeightsForTopic(index);
			
			HashMap<Integer, ArrayList<Integer>> mDocsPerDate = new HashMap<Integer, ArrayList<Integer>>();
			
			for (Entry<Integer, Float> entry : docs.entrySet()) 
			{
				if (!scoreInsteadOfDocNumbers)
				{
					float weight = entry.getValue();
					if(weight < threshold)
						continue;
				}
				
				int doc = entry.getKey();
				int date = dataReuters.getDocDate(doc);
				
				if (!mDocsPerDate.containsKey(date))
				{
					ArrayList<Integer> liDocs = new ArrayList<Integer>();
					mDocsPerDate.put(date, liDocs);
				}
				
				mDocsPerDate.get(date).add(doc);
			}
			
			// Fill missing dates in case there were no docs
			for (Integer date : dataReuters.getDocDates())
			{
				if (!mDocsPerDate.containsKey(date))
				{
					ArrayList<Integer> liDocs = new ArrayList<Integer>();
					mDocsPerDate.put(date, liDocs);
				}
			}
			
			int timeStepLength = mDocsPerDate.size() / numTimeSteps;
			
			ArrayList<Integer> liKeys = new ArrayList<Integer>();
			liKeys.addAll(mDocsPerDate.keySet());
			Collections.sort(liKeys);
			
			int iMod = 1;
			int iSum = 0;
			float fScoreSum = 0f;
			for (int iDateIndex : liKeys)
			{
				if (scoreInsteadOfDocNumbers)
				{
					ArrayList<Integer> liCurrentDocs = mDocsPerDate.get(iDateIndex);
					for (int iDocId : liCurrentDocs)
					{
						fScoreSum += docs.get(iDocId);
					}
				}

				iSum += mDocsPerDate.get(iDateIndex).size();
				
				
				if (iMod % timeStepLength == 0)
				{
					int timeStep = (iMod / timeStepLength) - 1;
					if (scoreInsteadOfDocNumbers)
					{
						weightsTopicsPerTimesteps[index][timeStep] = fScoreSum / iSum;
						fScoreSum = 0f;
						iSum = 0;
					}
					else
					{
						weightsTopicsPerTimesteps[index][timeStep] = iSum;
						iSum = 0;
					}
				}
				
				++iMod;				
			}
			
		}
		
		return weightsTopicsPerTimesteps;
	}
	
	/**
	 * Add new topics to timestepTopics according to changes in word vectors exceeding threshold
	 * 
	 * @param topicSimilarities
	 * @param threshold
	 * @param numTopDocs
	 * @param vocabulary
	 * @param numTopWords
	 * @param timestepTopics
	 * @param docsFilename
	 * @param wordsFilename
	 * @param topicsFilename
	 */
	public void addTopics(final double[][] topicSimilarities,
			final float threshold,
			int numTopDocs,
			Vocabulary vocabulary, 
			int numTopWords,
			final float[][] timestepTopics,
			final String docsFilename,
			final String wordsFilename,
			final String topicsFilename,
			final String visFilename)
	{
		LinkedList<String> vocabularyList = vocabulary.getAsList();
		
		if (numTopWords < 1)
			numTopWords = vocabularyList.size();
		
		ArrayList<List<String>> allNewWordLists = new ArrayList<List<String>>();
	
		ArrayList<List<String>> allNewDocLists = new ArrayList<List<String>>();

		ArrayList<List<Float>> allNewTopics = new ArrayList<List<Float>>();
		
		if (timestepTopics.length != topicSimilarities.length)
		{
			System.err.println("[addTopics] ERROR: number of topics do not match.");
			return;
		}
		
		if (timestepTopics.length < 1)
		{
			System.err.println("[addTopics] ERROR: timestepTopics does not contain topics.");
			return;
		}
		
		if (timestepTopics[0].length != numTimeSteps)
		{
			System.err.println("[addTopics] ERROR: timestepTopics timesteps do not match.");
			return;
		}

		// iterate through topics
		for (int topicId = 0; topicId < topicSimilarities.length; topicId++) 
		{
			// no word distributions for this topic !? Should not happen.
			if (topicSimilarities[topicId].length < 1)
			{
				System.err.println("[addTopics] ERROR: topicSimilarities have no timesteps");
				continue;
			}
			
			ArrayList<Integer> pointsOfChange = findPointsOfChange(topicSimilarities[topicId], threshold);
			
			if (pointsOfChange.size() < 1)
			{
				System.err.println("[addTopics] ERROR: pointsOfChange have no timesteps");
				continue;
			}
			
			allNewWordLists = getWords(allNewWordLists,
					pointsOfChange,
					vocabulary, 
					numTopWords,
					topicId);
			
			allNewDocLists = getDocs(allNewDocLists,
					pointsOfChange,
					numTopDocs,
					topicId);
			
			allNewTopics = computeTopics(allNewTopics,
					pointsOfChange,
					timestepTopics,
					topicId);
		}
		
		System.out.println("[findDocs] num topics: " + allNewDocLists.size());
		
		tools.IOTools.writeListMatrix(docsFilename, allNewDocLists);
		
		System.out.println("[findWords] num topics: " + allNewWordLists.size());
		
		tools.IOTools.writeListMatrix(wordsFilename, allNewWordLists);
				
		System.out.println("[findTopics] num topics: " + allNewTopics.size());
		
		tools.IOTools.writeListMatrix(topicsFilename, allNewTopics);
		
		ArrayList<List<String>> visList = writeVisOutput(allNewDocLists, 
				allNewTopics,
				numTimeSteps,
				"20.8.1996");
		
		tools.IOTools.writeListMatrixWithoutSpace(visFilename, visList);
	}
	
	/**
	 * Identify the timesteps, where the similarity exceeds the threshold for this topic.
	 * Similarities are summed up along the timeline until threshold is exceeded, then reset.
	 * 
	 * @param topicSimilarities
	 * @param threshold
	 * @return List of timesteps exceeding similarity threshold.
	 */
	public ArrayList<Integer> findPointsOfChange(final double[] topicSimilarities,
			final double threshold)
	{
		double firstTimestep = topicSimilarities[0];
		
		// find number of topic changes in time
		double sumDivs = 0.0d;
		ArrayList<Integer> pointsOfChange = new ArrayList<Integer>();
		pointsOfChange.add(0);
		for (int j = 1; j < topicSimilarities.length; ++j)
		{
			double nextTimestep = topicSimilarities[j];
			
			// get div from last change to current
			double div = Math.abs(firstTimestep - nextTimestep);
			
			// sum divs as long as no change happens as changes are computed between neighboring timesteps.
			sumDivs += div;
			
			// change happens ?
			if (sumDivs > threshold)
			{
				pointsOfChange.add(j);
				firstTimestep = nextTimestep;
				sumDivs = 0.0d;
			}
		}	
		return pointsOfChange;
	}
	
	/**
	 * Get the top words for the topics, including new topics according to similarity and threshold.
	 * 
	 * @param allNewWordLists
	 * @param pointsOfChange
	 * @param vocabulary
	 * @param numTopWords
	 * @param topicId
	 * @return allNewWordLists for topics with evtl. new topics' word lists
	 */
	private ArrayList<List<String>> getWords(ArrayList<List<String>> allNewWordLists,
			final ArrayList<Integer> pointsOfChange,
			final Vocabulary vocabulary, 
			final int numTopWords,
			final int topicId)
	{
		// list of existing topic and possible spinoffs
		ArrayList<List<String> > newWordLists = new ArrayList<List<String> >();

		// newWordLists.size() = pointsOfchange.size()
		for(Integer temp : pointsOfChange)
		{
			String[] wordLists = wordDistributions.getWordListForTopic(vocabulary, numTopWords, temp, topicId);

			List<String> newWordList = Arrays.asList(wordLists);

			newWordLists.add(newWordList);
		}

		for (List<String> wordList : newWordLists)
		{
			allNewWordLists.add(wordList);
		}
		
		return allNewWordLists;
	}
	
	/**
	 * Get the top documents for the topics, including new topics according to similarity and threshold.
	 * 
	 * @param allNewDocLists
	 * @param pointsOfChange
	 * @param numTopDocs
	 * @param topicId
	 * @return allNewDocLists for topics with evtl. new topics' document lists
	 */
	private ArrayList<List<String>> getDocs(ArrayList<List<String>> allNewDocLists,
			final ArrayList<Integer> pointsOfChange,
			int numTopDocs,
			final int topicId)
	{
		// list of existing topic and possible spinoffs
		ArrayList<List<String> > newDocLists = new ArrayList<List<String> >();

		// get documents and sort them by score
		HashMap<Integer, Float> docs = topicDistributions.getDocumentsAndWeightsForTopic(topicId);
		docs = Tools.sortByValue(docs);

		if (numTopDocs < 1)
			numTopDocs = docs.size();
		
		// get document dates
		ArrayList<Integer> docDates = dataReuters.getDocDates();
		int timeStepLength = docDates.size() / numTimeSteps;

		if (pointsOfChange.size() == 1)
		{
			List<String> newDocList = new ArrayList<String>();

			int topDocs = numTopDocs;
			Iterator<Integer> keys = docs.keySet().iterator();
			while (topDocs > 0 && keys.hasNext())
			{
				newDocList.add(dataReuters.getDocFilename(keys.next()));
				--topDocs;
			}
			//						for (Integer docId : docs.keySet())
			//						{
			//							newDocList.add(dataReuters.getDocName(docId));
			//						}

			newDocLists.add(newDocList);
		}
		else
		{
			ArrayList<Integer> pointsOfChangeTemp = (ArrayList<Integer>) pointsOfChange.clone();
			
			pointsOfChangeTemp.add(numTimeSteps + 1);
			int prevPoint = 0;
			for(int point = 1; point < pointsOfChangeTemp.size(); ++point)
			{
				int currentPoint = pointsOfChangeTemp.get(point);
				int diff = currentPoint - prevPoint;
				int firstDate = prevPoint * timeStepLength;
				int numDates = diff * timeStepLength;

				// ids are needed !!
				//docDates.subList(firstDate, numDates);

				List<String> newDocList = new ArrayList<String>();

				int topDocs = numTopDocs;
				Iterator<Integer> keys = docs.keySet().iterator();
				while (topDocs > 0 && keys.hasNext())
				{
					int docId = keys.next();
					if (docId >= firstDate && docId < (firstDate + numDates))
					{
						newDocList.add(dataReuters.getDocFilename(docId));
						--topDocs;
					}
				}

				newDocLists.add(newDocList);

				prevPoint = currentPoint;
			}
		}

		for (List<String> wordList : newDocLists)
		{
			allNewDocLists.add(wordList);
		}
					
		return allNewDocLists;
	}
	
	/**
	 * Add new topics to timestepTopics according to changes in word vectors exceeding threshold
	 * 
	 * @param allNewTopics
	 * @param pointsOfChange
	 * @param timestepTopics
	 * @param topicId
	 * @return allNewTopics containing timestepTopics' values for this topic and evtl. new ones
	 */
	private ArrayList<List<Float>> computeTopics(ArrayList<List<Float>> allNewTopics,
			final ArrayList<Integer> pointsOfChange,
			final float[][] timestepTopics,
			final int topicId)
	{
		// list of existing topic and possible spinoffs
		ArrayList<List<Float> > newTopics = new ArrayList<List<Float> >();

		// newTopics.size() = pointsOfchange.size()
		for(Integer temp : pointsOfChange)
		{
			ArrayList<Float> newTopic = new ArrayList<Float>();
			newTopics.add(newTopic);
		}

		int activeTopic = 0;
		for (int j = 0; j < timestepTopics[topicId].length; ++j)
		{
			if (j != 0 && pointsOfChange.contains(j))
			{
				++activeTopic;
			}

			for(int k = 0; k < newTopics.size(); ++k)
			{
				if (k == activeTopic)
				{
					newTopics.get(k).add(timestepTopics[topicId][j]);
				}
				else
				{
					newTopics.get(k).add(0.0f);
				}
			}
		}

		for (List<Float> newTopic : newTopics)
		{
			allNewTopics.add(newTopic);
		}
		
		return allNewTopics;
	}
	
	/**
	 * 
	 */
	public ArrayList<List<String>> writeVisOutput(final ArrayList<List<String>> allNewDocLists, 
			final ArrayList<List<Float>> allNewTopics,
			final int numTimesteps,
			final String startDate)
	{
		ArrayList<List<String>> outList = new ArrayList<List<String>>();

		if (allNewDocLists.size() != allNewTopics.size())
		{
			System.err.println("Number of topics do not match.");
			return outList;
		}
		
		List<String> date = new ArrayList<String>();
		date.add(startDate);
		outList.add(date);
		
		for (int timestep = 0; timestep < numTimesteps; ++timestep)
		{
			List<String> row = new ArrayList<String>();
			for (int topicId = 0; topicId < allNewTopics.size(); ++topicId)
			{
				row.add(allNewTopics.get(topicId).get(timestep).toString());
				row.add(":");
				
				// name is changed to filename by now, no longer retrievable.
				// get document dates
//				ArrayList<Integer> docDates = dataReuters.getDocDates();
//				int timeStepLength = docDates.size() / numTimeSteps;
//				
//				int firstDocFromDate = timestep * timeStepLength;
//				int tastDocFromDate = firstDocFromDate + timeStepLength;
				
				List<String> docs = allNewDocLists.get(topicId);
				for (int docPos = 0; docPos < docs.size(); ++docPos)
				{
					String doc = docs.get(docPos);
//					int docId = dataReuters.getDocIdForName(doc);
//					if (docId >= firstDocFromDate && docId < lastDocFromDate)
//					{
//					}
					row.add(doc);
					
					if (docPos != (docs.size() - 1))
						row.add(",");
				}
				row.add(";");
			}
			outList.add(row);
		}
		
		return outList;
	}
	
	/**
	 * 
	 * 
	 * @param scoreInsteadOfDocNumbers
	 * @param filename
	 */
	public void writeTopicsWithDocsPerTime(boolean scoreInsteadOfDocNumbers,
			final String filename)
	{
		String content = "";
		
		for (Integer index = 0; index < topicDistributions.getDocumentsPerTopics().size(); index++)
		{
			//ArrayList<Integer> docs = liLDATopicsToDocs.get(index);
			HashMap<Integer, Float> docs = topicDistributions.getDocumentsAndWeightsForTopic(index);
			if (!scoreInsteadOfDocNumbers)
			{
				content += docs.size();
			}
			
			HashMap<Integer, ArrayList<Integer>> mDocsPerDate = new HashMap<Integer, ArrayList<Integer>>();
			
			for (Entry<Integer, Float> entry : docs.entrySet()) 
			{
				int doc = entry.getKey();
				int date = dataReuters.getDocDate(doc);
				
				if (!mDocsPerDate.containsKey(date))
				{
					ArrayList<Integer> liDocs = new ArrayList<Integer>();
					mDocsPerDate.put(date, liDocs);
				}
				
				mDocsPerDate.get(date).add(doc);
			}
			
			int timeStepLength = mDocsPerDate.size() / numTimeSteps;
			
			ArrayList<Integer> liKeys = new ArrayList<Integer>();
			liKeys.addAll(mDocsPerDate.keySet());
			Collections.sort(liKeys);
			
			int iMod = 1;
			int iSum = 0;
			float fScoreSum = 0f;
			for (int iDateIndex : liKeys)
			{
				if (scoreInsteadOfDocNumbers)
				{
					ArrayList<Integer> liCurrentDocs = mDocsPerDate.get(iDateIndex);
					for (int iDocId : liCurrentDocs)
					{
						fScoreSum += docs.get(iDocId);
					}
				}

				iSum += mDocsPerDate.get(iDateIndex).size();
				
				
				if (iMod % timeStepLength == 0)
				{
					if (scoreInsteadOfDocNumbers)
					{
						content += " " + fScoreSum / iSum;
						fScoreSum = 0f;
						iSum = 0;
					}
					else
					{
						content += " " + iSum;
						iSum = 0;
					}
				}
				
				++iMod;				
			}
			
			content += "\n";
//			for (Entry<Integer, ArrayList<Integer>> entry : mDocsPerDate.entrySet()) 
//			{
//				content += " " + entry.getValue().size();	
//			}
//			
//			content += "\n";
		}
		
		System.out.println("[DTMEvaluator::writeTopicsWithDocsPerTime] Saving topics with number of docs to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	/**
	 * 
	 * 
	 * @param filename
	 */
	public void writeTopicsWithDocWeight(final String filename)
	{
		String content = "";
		
		for (Integer index = 0; index < topicDistributions.getDocumentsPerTopics().size(); index++)
		{
			//ArrayList<Integer> docs = liLDATopicsToDocs.get(index);
			HashMap<Integer, Float> docs = topicDistributions.getDocumentsAndWeightsForTopic(index);
			docs = Tools.sortByValue(docs);
			
			content += docs.size();
			
//			for (Entry<Integer, Double> entry : docs.entrySet()) 
//			{
//				String docName = getIndexToDoc().get(entry.getKey());
//				content += " " + docName + ":" + entry.getValue();
//			}
			
//			docs.forEach((id, weight) -> 
//			{
//				String docName = getIndexToDoc().get(id);
//				content += " " + docName + ":" + weight;
//			});
			content += "\n";
		}
		
		System.out.println("[DTMEvaluator::writeTopicsWithDocWeight] Saving topics with number of docs to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
}
