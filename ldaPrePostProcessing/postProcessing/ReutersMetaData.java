package postProcessing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class ReutersMetaData {

	private ArrayList<String> liDocNames;
	
	private ArrayList<Integer> liDocDates;
	
	private ArrayList<ArrayList<String> > liTopicsPerDocument;
	
	private HashMap<String, ArrayList<Integer> > mDocumentsPerTopic;

	private Integer indexCounter = 0;

	/**
	 * Read metadata for Reuters corpus from file with data for each document,
	 * i.e. each line in file is one document from corpus directory ordered by article id.
	 * Line consists of (space separated):
	 * <article id> <Date-String like yyyy-mm-dd> <topic 0> ... <topic n>
	 * 
	 * @param metaDataFilename
	 */
	public ReutersMetaData(final String metaDataFilename)
	{
		liDocNames = new ArrayList<String>();
		
		liDocDates = new ArrayList<Integer>();
		
		liTopicsPerDocument = new ArrayList<ArrayList<String> >();
		
		mDocumentsPerTopic = new HashMap<String, ArrayList<Integer> >();
		
		loadMetaData(metaDataFilename);

		System.out.println("[CorpusTopicDataObject] initialization done.");
		
	}
	
	public String getDocName(int index)
	{
		return liDocNames.get(index);
	}

	public Integer getDocDate(int index)
	{
		return liDocDates.get(index);
	}

	public ArrayList<String> getTopicsForDocument(int index)
	{
		return liTopicsPerDocument.get(index);
	}

	public HashMap<String, ArrayList<Integer>> getDocumentsPerTopics()
	{
		return mDocumentsPerTopic;
	}
	
	public ArrayList<Integer> getDocumentIdsForTopic(int index)
	{
		return mDocumentsPerTopic.get(index);
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
			liDocNames.add(doc);
			
			date = date.replace("-", "");
			
			int iDate = Integer.parseInt(date);
			liDocDates.add(iDate);
	
			for (int i = 2; i < parts.length; i++)
			{
				String topic = parts[i];
				topics.add(topic);
				
				if(mDocumentsPerTopic.containsKey(topic))
				{
					mDocumentsPerTopic.get(topic).add(indexCounter);
				}
				else
				{
					ArrayList<Integer> docs = new ArrayList<Integer>();
					docs.add(indexCounter);
					mDocumentsPerTopic.put(topic, docs);
				}
			}
			
			liTopicsPerDocument.add(topics);
		
			++indexCounter;
		}
	}
}
