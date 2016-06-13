package postProcessing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.stream.Stream;

import container.Vocabulary;
import tools.Utils;

public class ReutersMetaData {

	private ArrayList<String> liDocNames;
	
	private ArrayList<Integer> liDocDates;
	
	private ArrayList<ArrayList<String> > liTopicsPerDocument;
	
	private HashMap<String, ArrayList<Integer> > mDocumentsPerTopic;

	HashMap<Integer, Integer> mDocsPerDate;
	
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
		
		mDocsPerDate = new HashMap<Integer, Integer>();

		computeDocsPerDate();

		System.out.println("[CorpusTopicDataObject] initialization done.");
	}
	
	public String getDocName(int index)
	{
		return liDocNames.get(index);
	}

	public String[] getTopicNamesAsArray()
	{
		return mDocumentsPerTopic.keySet().toArray(new String[0]);
	}
	
	public ArrayList<Integer> getDocDates()
	{
		return liDocDates;
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
	
	public int getNumDocsPerDate(int date)
	{
		return mDocsPerDate.get(date);
	}
	
	public HashMap<Integer, Integer> getDocsPerDate()
	{
		return mDocsPerDate;
	}
	
	private void computeDocsPerDate()
	{
		for(Integer date : liDocDates)
		{
			if(mDocsPerDate.containsKey(date))
			{
				int numDocs = mDocsPerDate.get(date);
				++numDocs;
				mDocsPerDate.put(date, numDocs);
			}
			else
			{
				mDocsPerDate.put(date, 1);
			}
		}
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
	
	public float [][] computeAnnotatedTopicsWithDocsPerTime(int numTimeSteps)
	{
		int numTopics = getDocumentsPerTopics().size();
		
		float [][] weightsTopicsPerTimesteps = new float [numTopics][numTimeSteps];
		
		int index = 0;
		for (Entry<String,ArrayList<Integer> > entry : getDocumentsPerTopics().entrySet())
		//for (Integer index = 0; index < numTopics; index++)
		{
			ArrayList<Integer> docs = entry.getValue();// dataLDA.getDocumentsAndWeightsForTopic(index);
			
			HashMap<Integer, ArrayList<Integer>> mDocsPerDate = new HashMap<Integer, ArrayList<Integer>>();
			
			for (Integer docId : docs) 
			{
				int date = getDocDate(docId);
				
				if (!mDocsPerDate.containsKey(date))
				{
					ArrayList<Integer> liDocs = new ArrayList<Integer>();
					mDocsPerDate.put(date, liDocs);
				}
				
				mDocsPerDate.get(date).add(docId);
			}
			
			// Fill missing dates in case there were no docs
			for (Integer date : getDocDates())
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
			int numDocsPerDateOverall = 0;
			for (int iDateIndex : liKeys)
			{
				numDocsPerDateOverall += getNumDocsPerDate(iDateIndex);
				iSum += mDocsPerDate.get(iDateIndex).size();
								
				if (iMod % timeStepLength == 0)
				{
					int timeStep = (iMod / timeStepLength) - 1;
					weightsTopicsPerTimesteps[index][timeStep] = (float)iSum / (float)numDocsPerDateOverall;
					// Reset for next date according to timesteplength (weekly e.g.)
					iSum = 0;
					numDocsPerDateOverall = 0;
				}
				
				++iMod;				
			}
		
			++index;
		}
		
		return weightsTopicsPerTimesteps;
	}
}
