package data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Class holding the LDA topic distributions for each document for further processing.
 * This is usually read from the LDA gamma file output.
 */
public class TopicDistributions {

	/**
	 * list of all documents of map containing topic id and topic score for each document
	 */
	private ArrayList<HashMap<Integer, Float> > liTopicsPerDocument;

	/**
	 * map of all topics containing topic id and a map containing document id and this topic's score for each document
	 */
	private HashMap<Integer, HashMap<Integer, Float> > mDocumentsPerTopic;

	private Integer indexCounter = 0;

	/**
	 * 
	 * @param ldaTopicsFilename	e.g. gamma.txt
	 */
	public TopicDistributions(final String ldaTopicsFilename)
	{
		liTopicsPerDocument = new ArrayList<HashMap<Integer, Float> >();
		
		mDocumentsPerTopic = new HashMap<Integer, HashMap<Integer, Float> >();

		loadLDATopics(ldaTopicsFilename);

		System.out.println("[CorpusTopicDataObject] initialization done.");
		
	}
	
	public TopicDistributions()
	{
		liTopicsPerDocument = new ArrayList<HashMap<Integer, Float> >();
		
		mDocumentsPerTopic = new HashMap<Integer, HashMap<Integer, Float> >();

		System.out.println("[CorpusTopicDataObject] initialization done.");
		
	}
	
	public HashMap<Integer, Float> getTopicsAndWeightsForDocument(int index)
	{
		return liTopicsPerDocument.get(index);
	}

	public HashMap<Integer, HashMap<Integer, Float> > getDocumentsPerTopics()
	{
		return mDocumentsPerTopic;
	}
	
	public HashMap<Integer, Float> getDocumentsAndWeightsForTopic(int index)
	{
		return mDocumentsPerTopic.get(index);
	}
	
	/**
	 * load gamma file from LDA output.
	 * 
	 * @param filename
	 */
	private void loadLDATopics(final String filename)
	{
		if (Files.exists(Paths.get(filename))) 
		{
			try (Stream<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset()))
			{
				System.out.println("[TopicDistributions::loadLDATopics] Loading data from " + filename);
				
				synchronized(indexCounter)
				{
					indexCounter = 0;
				}
				
				lines.forEachOrdered(line -> readLDATopicLine(line));
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
	
	private void readLDATopicLine(final String line)
	{
		String[] parts = line.split(" ");
		HashMap<Integer, Float> topics = new HashMap<Integer, Float>();
		
		synchronized(indexCounter)
		{
			for (int i = 0; i < parts.length; i++)
			{
				String[] topicAndWeight = parts[i].split(":");
				if (topicAndWeight.length == 2 && topicAndWeight[0] != "" && topicAndWeight[1] != "")
				{
					int topic = Integer.parseInt(topicAndWeight[0]);
					float weight = Float.parseFloat(topicAndWeight[1]);
					topics.put(topic, weight);
//				if(liLDATopicsToDocs.containsKey(topic))
//				{
//					liLDATopicsToDocs.get(topic).add(indexCounter);
//				}
//				else
//				{
//					ArrayList<Integer> docs = new ArrayList<Integer>();
//					docs.add(indexCounter);
//					liLDATopicsToDocs.put(topic, docs);
//				}
				
					if(mDocumentsPerTopic.containsKey(topic))
					{
						mDocumentsPerTopic.get(topic).put(indexCounter, weight);
					}
					else
					{
						HashMap<Integer, Float> docs = new HashMap<Integer, Float>();
						docs.put(indexCounter, weight);
						mDocumentsPerTopic.put(topic, docs);
					}
				}
			}
			
			liTopicsPerDocument.add(topics);
		
			++indexCounter;
		}
	}
}
