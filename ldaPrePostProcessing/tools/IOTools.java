package tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class IOTools {

	public static  void saveContentToFile(final String content, final String fileName)
	{
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(fileName, "UTF-8");
			
			writer.print(content);
			
			writer.close();
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
	
	public static <T> void writeListMatrixWithoutSpace(final String filename,
			final List<List<T>> data)
	{
		String content = "";

		for (List<T> list : data)
		{
			for (T item : list)
			{
				content += item;
			}
			content += "\n";
		}
		
		System.out.println("[IOUtils::writeListMatrix] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	public static <T> void writeListMatrix(final String filename,
			final List<List<T>> data)
	{
		String content = "";

		for (List<T> list : data)
		{
			for (T item : list)
			{
				content += item + " ";
			}
			content += "\n";
		}
		
		System.out.println("[IOUtils::writeListMatrix] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	public static void writeIntegerHashListMatrix(final String filename,
			final ArrayList<LinkedHashSet<Integer> > data)
	{
		String content = "";

		for (int i = 0; i < data.size(); i++) 
		{
			for(int j : data.get(i))
			{
				content += j + " ";
			}
			content += "\n";
		}

		System.out.println("[IOUtils::writeIntegerListMatrix] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	public static void writeIntegerListMatrix(final String filename,
			final ArrayList<ArrayList<Integer> > data)
	{
		String content = "";

		for (int i = 0; i < data.size(); i++) 
		{
			ArrayList<Integer> row = data.get(i);
			
			for (int j = 0; j < row.size(); j++) 
			{
				content += row.get(j) + " ";
			}
			content += "\n";
		}

		System.out.println("[IOUtils::writeIntegerListMatrix] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	public static void writeDoubleMatrix(final String filename,
			final double[][] matrix)
	{
		String content = "";

		for (int i = 0; i < matrix.length; i++) 
		{
			for (int j = 0; j < matrix[i].length; j++) 
			{
				content += matrix[i][j] + " ";
			}
			content += "\n";
		}

		System.out.println("[IOUtils::writeDoubleMatrix] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	public static void writeFloatMatrix(final String filename,
			final float[][] matrix)
	{
		String content = "";

		for (int i = 0; i < matrix.length; i++) 
		{
			for (int j = 0; j < matrix[i].length; j++) 
			{
				content += matrix[i][j] + " ";
			}
			content += "\n";
		}

		System.out.println("[IOUtils::writeFloatMatrix] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	public static void writeIntMatrix(final String filename,
			final int[][] matrix)
	{
		String content = "";

		for (int i = 0; i < matrix.length; i++) 
		{
			for (int j = 0; j < matrix[i].length; j++) 
			{
				content += matrix[i][j] + " ";
			}
			content += "\n";
		}

		System.out.println("[IOUtils::writeDoubleMatrix] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	public static void writeTopicTopWords(final String filename, 
			final String[][] wordLists)
	{
		String content = "";

		for (int i = 0; i < wordLists.length; i++) 
		{
			for (int j = 0; j < wordLists[i].length; j++) 
			{
				content += wordLists[i][j] + " ";
			}
			content += "\n";
		}

		System.out.println("[IOUtils::writeTopicTopWords] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
	}
	
	public static void writeTimestepTopicsAsJason(final String filename,
			final String[][] wordLists,
			final float[][] timestepTopics)
	{
		ArrayList<String> topicStrings = new ArrayList<String>();
		for (int topic = 0; topic < timestepTopics.length; ++topic)
		{
			ArrayList<String> strings = new ArrayList<String>();
//			for (int word = 0; word < wordLists[topic].length; ++word)
//			{
//				strings.add(wordLists[topic][word]);
//			}
			strings.add("temp");
			String topicName = String.join(" ", strings);
			
			strings.clear();
			for (int timestep = 0; timestep < timestepTopics[topic].length; ++timestep)
			{
				strings.add("{\"x\":" + timestep + ",\"y\":" + timestepTopics[topic][timestep] + "}");
			}
			String topicValues = String.join(",", strings);;
			
			
			topicStrings.add("{\"name\":\"Topic \\\"" + topicName
					+ "\\\"\",\"data\":[" + topicValues + "]}");

		}

		String content = "var data = [" + String.join(",", topicStrings) + "];";

		System.out.println("[IOUtils::writeTimestepTopicsAsJason] Saving data to " + filename);
		IOTools.saveContentToFile(content, filename);
		
//		List<String> waveData = new ArrayList<>();
//	    for (TopicWave wave : waves)
//	    {
//	      String waveText = "{\"name\":\"Topic \\\"" + nameMap.get(wave)
//	              + "\\\"\",\"data\":[";
//	      List<Double> yValues = yValueMap.get(wave);
//	      List<String> timeStepData = new ArrayList<>();
//	      for (int i = 0; i < yValues.size(); i++)
//	      {
//	        String timeStepText = "{\"x\":" + i + ",\"y\":" + yValues.get(i) + "}";
//	        timeStepData.add(timeStepText);
//	      }
//	      waveText += String.join(",", timeStepData);
//	      waveText += "]}";
//	      waveData.add(waveText);
//	    }
//	    jsonText += String.join(",", waveData);
//	    jsonText += "];";
	}
	

}
