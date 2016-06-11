package tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

public class IOUtils {

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
		IOUtils.saveContentToFile(content, filename);
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
		IOUtils.saveContentToFile(content, filename);
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
		IOUtils.saveContentToFile(content, filename);
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
		IOUtils.saveContentToFile(content, filename);
	}
}
