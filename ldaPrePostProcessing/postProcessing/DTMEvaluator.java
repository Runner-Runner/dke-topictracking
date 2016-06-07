package postProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import tools.IOUtils;

public class DTMEvaluator 
{
	CorpusTopicDataObject data;

	public DTMEvaluator(CorpusTopicDataObject data) 
	{
		this.data = data;
		
		System.out.println("[DTMEvaluator] initialization done.");
	}

	public void writeTopicsWithDocsPerTime(final int numTimeSteps, 
			boolean scoreInsteadOfDocNumbers,
			final String filename)
	{
		String content = "";
		
		for (Integer index = 0; index < data.getLDADocumentsPerTopic().size(); index++)
		{
			//ArrayList<Integer> docs = liLDATopicsToDocs.get(index);
			HashMap<Integer, Double> docs = data.getLDADocumentsPerTopic().get(index);
			
			if (!scoreInsteadOfDocNumbers)
			{
				content += docs.size();
			}
			
			HashMap<Integer, ArrayList<Integer>> mDocsPerDate = new HashMap<Integer, ArrayList<Integer>>();
			
			for (Entry<Integer, Double> entry : docs.entrySet()) 
			{
				int doc = entry.getKey();
				int date = data.getDocIndexToDate().get(doc);
				
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
}
