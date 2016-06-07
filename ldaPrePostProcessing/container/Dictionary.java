package container;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

import tools.IOUtils;

public class Dictionary {
	
	private HashSet<String> dictionary;

	public HashSet<String> getDictionary() 
	{
		return dictionary;
	}

	public Dictionary(final String filename)
	{
		dictionary = new HashSet<String>();
		
		loadFromFile(filename);
	}
	
	private void loadFromFile(final String filename)
	{
		if (Files.exists(Paths.get(filename))) 
		{
			try (Stream<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset()))
			{
				System.out.println("[Stopwords::loadFromFile] Loading stopwords from " + filename);
				
				lines.forEachOrdered(line -> dictionary.add(line));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean containsWord(final String word)
	{
		return dictionary.contains(word);
	}
	
	public LinkedList<String> getAsList()
	{
		LinkedList<String> list = new LinkedList<String>(dictionary);
		return list;
	}
	
	public void saveSorted(final String fileName)
	{
		System.out.println("[Dictionary::saveSorted] Saving vocabulary to " + fileName);

		String content = "";
		
		ArrayList<String> list = new ArrayList<String>(dictionary);
		Collections.sort(list);
			  
		Iterator<String> it = list.iterator();
		while (it.hasNext())
		{
			String word = (String) it.next();
			content += word + "\n";
		}

		IOUtils.saveContentToFile(content, fileName);
	}
}
