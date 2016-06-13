package postProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.sound.midi.Synthesizer;

import tools.IOUtils;
import tools.Utils;

public class DTMEvaluator 
{
	final ReutersMetaData dataReuters;
	
	final TopicDistributions dataLDA;
	
	final int numTimeSteps;

	public DTMEvaluator(final ReutersMetaData dataReuters,
			final TopicDistributions dataLDA,
			final int numTimeSteps) 
	{
		this.dataReuters = dataReuters;
		
		this.dataLDA = dataLDA;
		
		this.numTimeSteps = numTimeSteps;
		
		System.out.println("[DTMEvaluator] initialization done.");
	}
	
	public float[][] findNewTopics(float[][] timestepTopics,
			WordDistributions wds,
			float threshold)
	{
		ArrayList<ArrayList<Float>> allNewTopics = new ArrayList<ArrayList<Float>>();
		
		double[][] topicSimilarities = wds.computeIntraTopicSimilarities();

		if (timestepTopics.length != topicSimilarities.length)
		{
			System.err.println("[findNewTopics] ERROR: number of topics do not match.");
			return null;
		}
		
		if (timestepTopics.length < 1)
		{
			System.err.println("[findNewTopics] ERROR: timestepTopics does not contain topics.");
			return null;
		}
		
		if (timestepTopics[0].length != numTimeSteps)
		{
			System.err.println("[findNewTopics] ERROR: timestepTopics timesteps do not match.");
			return null;
		}

		// iterate through topics
		for (int i = 0; i < topicSimilarities.length; i++) 
		{
			// no word distributions for this topic !? Should not happen.
			if (topicSimilarities[i].length < 1)
			{
				System.err.println("[findNewTopics] ERROR: topicSimilarities have no timesteps");
				continue;
			}
			
			// first timestep is first reference
			double firstTimestep = topicSimilarities[i][0];
			
			// find number of topic changes in time
			double sumDivs = 0.0d;
			ArrayList<Integer> pointsOfChange = new ArrayList<Integer>();
			pointsOfChange.add(0);
			for (int j = 1; j < topicSimilarities[i].length; ++j)
			{
				double nextTimestep = topicSimilarities[i][j];
				
				// get div from last chage to current
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
			
			// list of existing topic and possible spinoffs
			ArrayList<ArrayList<Float> > newTopics = new ArrayList<ArrayList<Float> >();
			
			// newTopics.size() = pointsOfchange.size()
			for(Integer temp : pointsOfChange)
			{
				ArrayList<Float> newTopic = new ArrayList<Float>();
				newTopics.add(newTopic);
			}
			
			int activeTopic = 0;
			for (int j = 0; j < timestepTopics[i].length; ++j)
			{
				if (j != 0 && pointsOfChange.contains(j))
				{
					++activeTopic;
				}
				
				for(int k = 0; k < newTopics.size(); ++k)
				{
					if (k == activeTopic)
					{
						newTopics.get(k).add(timestepTopics[i][j]);
					}
					else
					{
						newTopics.get(k).add(0.0f);
					}
				}
			}
			
			for (ArrayList<Float> newTopic : newTopics)
			{
				allNewTopics.add(newTopic);
			}
		}
		
		float[][] moreTopics = new float[allNewTopics.size()][numTimeSteps];
		for (int i = 0; i < moreTopics.length; i++) 
		{
			for (int j = 0; j < moreTopics[i].length; j++) 
			{
				moreTopics[i][j] = allNewTopics.get(i).get(j);	
			}
		}
		return moreTopics;
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
		int numTopics = dataLDA.getDocumentsPerTopics().size();
		
		float [][] weightsTopicsPerTimesteps = new float [numTopics][numTimeSteps];
		
		for (Integer index = 0; index < numTopics; index++)
		{
			HashMap<Integer, Float> docs = dataLDA.getDocumentsAndWeightsForTopic(index);
			
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
			
			System.out.println("mDocsPerDate.size() " + mDocsPerDate.size() + " / numTimeSteps " + numTimeSteps + " = timeStepLength " + timeStepLength);
			
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
	
	public void writeTopicsWithDocsPerTime(boolean scoreInsteadOfDocNumbers,
			final String filename)
	{
		String content = "";
		
		for (Integer index = 0; index < dataLDA.getDocumentsPerTopics().size(); index++)
		{
			//ArrayList<Integer> docs = liLDATopicsToDocs.get(index);
			HashMap<Integer, Float> docs = dataLDA.getDocumentsAndWeightsForTopic(index);
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
		IOUtils.saveContentToFile(content, filename);
	}
	
		public void writeTopicsWithDocWeight(final String filename)
	{
		String content = "";
		
		for (Integer index = 0; index < dataLDA.getDocumentsPerTopics().size(); index++)
		{
			//ArrayList<Integer> docs = liLDATopicsToDocs.get(index);
			HashMap<Integer, Float> docs = dataLDA.getDocumentsAndWeightsForTopic(index);
			docs = Utils.sortByValue(docs);
			
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
		IOUtils.saveContentToFile(content, filename);
	}
}
