package postProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


public class Evaluator {

	ReutersMetaData dataReuters;
	
	TopicDistributions dataLDA;
	
	public Evaluator(ReutersMetaData reutersData,
			TopicDistributions ldaData)
	{
		this.dataReuters = reutersData;
		
		this.dataLDA = ldaData;
		
		System.out.println("[Evaluator] initialization done.");
	}
	
	public void evaluteTopics()
	{
		HashMap<String, Integer> mAnnotatedTopicsTopicToIndex = new HashMap<String, Integer>();
		HashMap<Integer, String> mAnnotatedTopicsIndexToTopic = new HashMap<Integer, String>();

		int index = 0;
		for(Entry<String, ArrayList<Integer>> entry: dataReuters.getDocumentsPerTopics().entrySet())
		{
			String topic = entry.getKey();
			mAnnotatedTopicsTopicToIndex.put(topic, index);
			mAnnotatedTopicsIndexToTopic.put(index, topic);
			++index;
		}
		
		int sizeLDATopics = dataLDA.getDocumentsPerTopics().size();
		int sizeOrigTopcis = dataReuters.getDocumentsPerTopics().size();
		
		int[][] confusionMatrix = new int[sizeLDATopics][sizeOrigTopcis];
		
//		for(Entry<Integer, ArrayList<Integer>> entry1 : data.getLDADocumentsPerTopic().entrySet())
//		{
//			int topicLDA = entry1.getKey();
//			ArrayList<Integer> docsLDA = entry1.getValue();
//			
//			for (int doc : docsLDA)
//			{
//				ArrayList<String> topicsOrig = data.getAnnotatedTopicsPerDocument().get(doc);
//				for (String topicOrig : topicsOrig)
//				{
//					int indexTopicOrig = mAnnotatedTopicsTopicToIndex.get(topicOrig);
//					
//					++confusionMatrix[topicLDA][indexTopicOrig];
//				}
//			}
//		}
		
		for(Entry<Integer, HashMap<Integer, Double>> entry1 : dataLDA.getDocumentsPerTopics().entrySet())
		{
			int topicLDA = entry1.getKey();
			HashMap<Integer, Double> docsLDA = entry1.getValue();
			
			for (Entry<Integer, Double> doc : docsLDA.entrySet())
			{
				ArrayList<String> topicsOrig = dataReuters.getTopicsForDocument(doc.getKey());
				for (String topicOrig : topicsOrig)
				{
					int indexTopicOrig = mAnnotatedTopicsTopicToIndex.get(topicOrig);
					
					++confusionMatrix[topicLDA][indexTopicOrig];
				}
			}
		}
		
		double[][] precisionMatrix = new double[sizeLDATopics][sizeOrigTopcis];
		
		// Sum of all elements, v1
		double sumCM1 = 0;
		double[] sumsLDA = new double[sizeLDATopics];
		
		// compute all precisions
		for(int i = 0; i < sizeLDATopics; ++i)
		{
			double sumI = 0;
			for(int j = 0; j < sizeOrigTopcis; ++j)
			{
				sumI += confusionMatrix[i][j];
			}
			for(int j = 0; j < sizeOrigTopcis; ++j)
			{
				precisionMatrix[i][j] = confusionMatrix[i][j] / sumI;
			}
			
			sumsLDA[i] = sumI;
			sumCM1 += sumI;
		}
		
		double[][] recallMatrix = new double[sizeLDATopics][sizeOrigTopcis];
		
		// Sum of all elements, v1
		double sumCM2 = 0;
		double[] sumsOrig = new double[sizeOrigTopcis];
				
		// compute all recalls
		for(int j = 0; j < sizeOrigTopcis; ++j)
		{
			double sumJ = 0;
			for(int i = 0; i < sizeLDATopics; ++i)
			{
				sumJ += confusionMatrix[i][j];
			}
			for(int i = 0; i < sizeLDATopics; ++i)
			{
				recallMatrix[i][j] = confusionMatrix[i][j] / sumJ;
			}
			
			sumsOrig[j] = sumJ;			
			sumCM2 += sumJ;
		}
		
		if (sumCM1 != sumCM2 || sumCM1 == 0)
		{
			System.out.println("ERROR: Sums make no sense: " + sumCM1 + " : " + sumCM2);
			sumCM1 = 1.0;
		}
		
		
		double[][] fScoreMatrix = new double[sizeLDATopics][sizeOrigTopcis];
		
		// compute all fScores
		for(int i = 0; i < sizeLDATopics; ++i)
		{
			for(int j = 0; j < sizeOrigTopcis; ++j)
			{
				double div = precisionMatrix[i][j] + recallMatrix[i][j];
				if(div != 0)
				{
					fScoreMatrix[i][j] = 2 * ( precisionMatrix[i][j] * recallMatrix[i][j] ) / div;
				}
				else
				{
					fScoreMatrix[i][j] = 0.0;
				}
			}
		}
		
		LinkedHashMap<Integer, String> mTopicsLDAToOrig = new LinkedHashMap<Integer, String>();
		HashMap<Integer, Double> mTopicPrecision = new HashMap<Integer, Double>();
		HashMap<Integer, Double> mTopicRecall = new HashMap<Integer, Double>();
		HashMap<Integer, Double> mTopicFScore = new HashMap<Integer, Double>();
		
		HashSet<Integer> skipIndicesOrig = new HashSet<Integer>();
		int errorIndex = mAnnotatedTopicsTopicToIndex.get("ERROR_TOPIC");
		skipIndicesOrig.add(errorIndex);
		
		HashSet<Integer> skipIndicesLDA = new HashSet<Integer>();
		
		// at least once for all topics
		for(int k = 0; k < sizeLDATopics; ++k)
		{
			// find max fscore and save indices
			boolean bDone = false;
			int indexLDA = 0;
			int indexOrig = 0;
			for(int i = 0; i < sizeLDATopics; ++i)
			{
				// skip that one, if we already connected it
				if(!skipIndicesLDA.contains(i))
				{
					for(int j = 0; j < sizeOrigTopcis; ++j)
					{
						// skip that one, if we already connected it
						if(!skipIndicesOrig.contains(j))
						{
							if(fScoreMatrix[i][j] > fScoreMatrix[indexLDA][indexOrig])
							{
								indexLDA = i;
								indexOrig = j;
								bDone = true;
							}
							else if(fScoreMatrix[i][j] == fScoreMatrix[indexLDA][indexOrig])// weight in case of equality
							{
								if((fScoreMatrix[i][j] * (sumsLDA[i] / sumCM1) * (sumsOrig[j] / sumCM1)) > fScoreMatrix[indexLDA][indexOrig] * (sumsLDA[indexLDA] / sumCM1) * (sumsOrig[indexOrig] / sumCM1))
								{
									indexLDA = i;
									indexOrig = j;
									bDone = true;
								}
							}
						}
						else if(j == 0)// if 0 is connected
						{
							++j;
						}
					}
				}
				else if(i == 0)// if 0 is connected
				{
					++i;
				}
			}

			// found -> connect
			if(bDone)
			{
				skipIndicesLDA.add(indexLDA);
				skipIndicesOrig.add(indexOrig);

				mTopicsLDAToOrig.put(indexLDA, mAnnotatedTopicsIndexToTopic.get(indexOrig));
				mTopicPrecision.put(indexLDA, precisionMatrix[indexLDA][indexOrig]);
				mTopicRecall.put(indexLDA, recallMatrix[indexLDA][indexOrig]);
				mTopicFScore.put(indexLDA, fScoreMatrix[indexLDA][indexOrig]);
			}
		}
		
		// once more for all unconnected topics -> ERROR_TOPIC
		for(int i = 0; i < sizeLDATopics; ++i)
		{
			if(!skipIndicesLDA.contains(i))
			{
				mTopicsLDAToOrig.put(i, mAnnotatedTopicsIndexToTopic.get(errorIndex));
				mTopicPrecision.put(i, precisionMatrix[i][errorIndex]);
				mTopicRecall.put(i, recallMatrix[i][errorIndex]);
				mTopicFScore.put(i, fScoreMatrix[i][errorIndex]);
			}
		}

		System.out.println("\\textbf{\\parbox[t]{1cm}{LDA\\\\Topic}}&\\textbf{\\parbox[t]{1cm}{Annotated\\\\Topic}&\\textbf{Precision}&\\textbf{Recall}&\\textbf{F-Score} \\\\");
//		System.out.println("\\hline");
		mTopicsLDAToOrig.forEach((lda, orig) ->{
			System.out.format(lda + "&" + orig + "&%.5f&%.5f&%.5f \\\\%n", mTopicPrecision.get(lda), mTopicRecall.get(lda), mTopicFScore.get(lda));
//			System.out.println("\\hline");
		});

//		mTopicsLDAToOrig.forEach((lda, orig) ->{
//			System.out.println("LDA Topic: " + lda + " ,orig topic: " + orig + " ,p=" + mTopicPrecision.get(lda) + " ,r=" + mTopicRecall.get(lda) + " ,f=" + mTopicFScore.get(lda));
//		});
	}
	
}
