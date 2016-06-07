package tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
}
