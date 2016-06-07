package postProcessing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import tools.IOUtils;

public class CorpusTopicDataObject {

	private HashMap<Integer, String> mIndexToDoc;
	
	private HashMap<Integer, Integer> mDocIndexToDate;
	
	// topics per document
	private HashMap<Integer, ArrayList<Integer> > mLDATopicsPerDocument;
	private HashMap<Integer, ArrayList<String> > mAnnotatedTopicsPerDocument;
	
	// documents per topic
	private HashMap<Integer, HashMap<Integer, Double> > mLDADocumentsPerTopic;
	private HashMap<String, ArrayList<Integer> > mAnnotatedTopicsDocumentsPerTopic;

	private Integer indexCounter = 0;

	public CorpusTopicDataObject(final String metaDataFilename,
			final String ldaTopicsFilename)
	{
		mIndexToDoc = new HashMap<Integer, String>();
		
		mDocIndexToDate = new HashMap<Integer, Integer>();
		
		mLDATopicsPerDocument = new HashMap<Integer, ArrayList<Integer> >();
		
		mAnnotatedTopicsPerDocument = new HashMap<Integer, ArrayList<String> >();
		
		//liLDATopicsToDocs = new HashMap<Integer, ArrayList<Integer> >();
		mLDADocumentsPerTopic = new HashMap<Integer, HashMap<Integer, Double> >();
		
		mAnnotatedTopicsDocumentsPerTopic = new HashMap<String, ArrayList<Integer> >();
		
		loadMetaData(metaDataFilename);
		
		loadLDATopics(ldaTopicsFilename);

		System.out.println("[CorpusTopicDataObject] initialization done.");
		
	}
	
	public HashMap<Integer, String> getIndexToDoc() 
	{
		return mIndexToDoc;
	}

	public HashMap<Integer, Integer> getDocIndexToDate()
	{
		return mDocIndexToDate;
	}

	public HashMap<Integer, ArrayList<Integer>> getLDATopicsPerDocument()
	{
		return mLDATopicsPerDocument;
	}

	public HashMap<Integer, ArrayList<String>> getAnnotatedTopicsPerDocument()
	{
		return mAnnotatedTopicsPerDocument;
	}

	public HashMap<Integer, HashMap<Integer, Double>> getLDADocumentsPerTopic()
	{
		return mLDADocumentsPerTopic;
	}

	public HashMap<String, ArrayList<Integer>> getAnnotatedTopicsDocumentsPerTopic()
	{
		return mAnnotatedTopicsDocumentsPerTopic;
	}
	
	private void loadMetaData(final String metaDataFilename)
	{
		if (Files.exists(Paths.get(metaDataFilename))) 
		{
			try (Stream<String> lines = Files.lines(Paths.get(metaDataFilename), Charset.defaultCharset()))
			{
				System.out.println("[CorpusTopicDataObject::loadMetaData] Loading data from " + metaDataFilename);
				
				synchronized(indexCounter)
				{
					indexCounter = 0;
				}
				
				lines.forEachOrdered(line -> readMetaDataLine(line));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("ERROR: file does not exist: " + metaDataFilename);
		}
	}
	
	private void readMetaDataLine(final String line)
	{
		String[] parts = line.split(" ");
		String doc = parts[0];
		String date = parts[1];
		ArrayList<String> topics = new ArrayList<String>();
		
		synchronized(indexCounter)
		{
			mIndexToDoc.put(indexCounter, doc);
			
			date = date.replace("-", "");
			
			int iDate = Integer.parseInt(date);
			mDocIndexToDate.put(indexCounter, iDate);
	
			for (int i = 2; i < parts.length; i++)
			{
				String topic = parts[i];
				topics.add(topic);
				
				if(mAnnotatedTopicsDocumentsPerTopic.containsKey(topic))
				{
					mAnnotatedTopicsDocumentsPerTopic.get(topic).add(indexCounter);
				}
				else
				{
					ArrayList<Integer> docs = new ArrayList<Integer>();
					docs.add(indexCounter);
					mAnnotatedTopicsDocumentsPerTopic.put(topic, docs);
				}
			}
			
			mAnnotatedTopicsPerDocument.put(indexCounter, topics);
		
			++indexCounter;
		}
	}
	
	private void loadLDATopics(final String filename)
	{
		if (Files.exists(Paths.get(filename))) 
		{
			try (Stream<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset()))
			{
				System.out.println("[CorpusTopicDataObject::loadLDATopics] Loading data from " + filename);
				
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
		ArrayList<Integer> topics = new ArrayList<Integer>();
		
		synchronized(indexCounter)
		{
			for (int i = 0; i < parts.length; i++)
			{
				String[] topicAndWeight = parts[i].split(":");
				if (topicAndWeight.length == 2 && topicAndWeight[0] != "" && topicAndWeight[1] != "")
				{
					int topic = Integer.parseInt(topicAndWeight[0]);
					double weight = Double.parseDouble(topicAndWeight[1]);
					topics.add(topic);
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
				
					if(mLDADocumentsPerTopic.containsKey(topic))
					{
						mLDADocumentsPerTopic.get(topic).put(indexCounter, weight);
					}
					else
					{
						HashMap<Integer, Double> docs = new HashMap<Integer, Double>();
						docs.put(indexCounter, weight);
						mLDADocumentsPerTopic.put(topic, docs);
					}
				}
			}
			
			mLDATopicsPerDocument.put(indexCounter, topics);
		
			++indexCounter;
		}
	}
	
	public void writeTopicsWithDocWeight(final String filename)
	{
		String content = "";
		
		for (Integer index = 0; index < getLDADocumentsPerTopic().size(); index++)
		{
			//ArrayList<Integer> docs = liLDATopicsToDocs.get(index);
			HashMap<Integer, Double> docs = getLDADocumentsPerTopic().get(index);
			docs = sortByValue(docs);
			
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
		
		System.out.println("[CorpusTopicDataObject::writeTopicsWithDocWeight] Saving topics with number of docs to " + filename);
		IOUtils.saveContentToFile(content, filename);
	}
	
	private static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> 
    sortByValue( Map<K, V> map )
	{
		LinkedHashMap<K, V> result = new LinkedHashMap<>();
	    Stream<Map.Entry<K, V>> st = map.entrySet().stream();
	
	    st.sorted( Map.Entry.comparingByValue(Comparator.reverseOrder()) )
	        .forEachOrdered( e -> result.put(e.getKey(), e.getValue()) );
	
	    return result;
	}
}
