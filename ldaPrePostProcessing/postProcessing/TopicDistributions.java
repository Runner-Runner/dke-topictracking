package postProcessing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import tools.IOUtils;

public class TopicDistributions {

	private ArrayList<HashMap<Integer, Double> > liTopicsPerDocument;

	private HashMap<Integer, HashMap<Integer, Double> > mDocumentsPerTopic;

	private Integer indexCounter = 0;

	public TopicDistributions(final String ldaTopicsFilename)
	{
		liTopicsPerDocument = new ArrayList<HashMap<Integer, Double> >();
		
		mDocumentsPerTopic = new HashMap<Integer, HashMap<Integer, Double> >();

		loadLDATopics(ldaTopicsFilename);

		System.out.println("[CorpusTopicDataObject] initialization done.");
		
	}
	
	public HashMap<Integer, Double> getTopicsAndWeightsForDocument(int index)
	{
		return liTopicsPerDocument.get(index);
	}

	public HashMap<Integer, HashMap<Integer, Double> > getDocumentsPerTopics()
	{
		return mDocumentsPerTopic;
	}
	
	public HashMap<Integer, Double> getDocumentsAndWeightsForTopic(int index)
	{
		return mDocumentsPerTopic.get(index);
	}
	
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
		HashMap<Integer, Double> topics = new HashMap<Integer, Double>();
		
		synchronized(indexCounter)
		{
			for (int i = 0; i < parts.length; i++)
			{
				String[] topicAndWeight = parts[i].split(":");
				if (topicAndWeight.length == 2 && topicAndWeight[0] != "" && topicAndWeight[1] != "")
				{
					int topic = Integer.parseInt(topicAndWeight[0]);
					double weight = Double.parseDouble(topicAndWeight[1]);
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
						HashMap<Integer, Double> docs = new HashMap<Integer, Double>();
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
