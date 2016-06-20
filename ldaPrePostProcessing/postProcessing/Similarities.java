package postProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import tools.Tools;

public class Similarities {

	public static double cosineSimilarity(double[] vectorA, double[] vectorB) 
	{
		if (vectorA.length != vectorB.length)
			return 0;
		
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}   
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	
	// Should take the full vector and normalize by length!
	/**
	 * 
	 * @param mapA
	 * @param mapB
	 * @param numIndices
	 * @return
	 */
	public static int indexDistance(HashMap<Integer, Float> mapA, 
			HashMap<Integer, Float> mapB,
			int numIndices)
	{
		LinkedHashMap<Integer, Float> mapASorted = Tools.sortByValue(mapA);
		LinkedHashMap<Integer, Float> mapBSorted = Tools.sortByValue(mapB);
		
		ArrayList<Integer> indicesA = new ArrayList<Integer>(mapASorted.keySet());
		ArrayList<Integer> indicesB = new ArrayList<Integer>(mapBSorted.keySet());
		ArrayList<Integer> indicesBDone =  new ArrayList<Integer>();
		int distance = 0;
		
		for (int indexA = 0; indexA < indicesA.size() && indexA < numIndices; indexA++)
		{
			int indexAId = indicesA.get(indexA);
			int indexB = indicesB.indexOf(indexAId);
			
			indicesBDone.add(indexB);
			
			if (indexB == -1)
			{
				System.err.println("Index not found: " + indexAId);
				indexB = mapA.size();
			}
			int diff = Math.abs(indexA - indexB);
			distance += diff;
		}
		
		for (int indexB = 0; indexB < indicesB.size() && indexB < numIndices && !indicesBDone.contains(indexB); indexB++)
		{
			int indexBId = indicesB.get(indexB);
			int indexA = indicesA.indexOf(indexBId);
			
			if (indexA == -1)
			{
				System.err.println("Index not found: " + indexBId);
				indexA = mapB.size();
			}
			int diff = Math.abs(indexB - indexA);
			distance += diff;
		}
		
		return distance;
	}
}
