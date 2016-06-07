package container;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;

import tools.IOUtils;

public class Vocabulary 
{

	private Set<String> vocabulary;
	
	final private String filename;
	final private String filenameSorted;
	
	public Vocabulary(final String outFile)
	{
		vocabulary = Collections.synchronizedSet(new LinkedHashSet<String>());
		
		filename = outFile + ".txt";
		filenameSorted = outFile + "_sorted.txt";

		if (Files.exists(Paths.get(filename))) 
		{
			try (Stream<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset()))
			{
				System.out.println("[Vocabulary] Loading existing vocabulry from " + filename);
				
				lines.forEachOrdered(line -> addWord(line));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean containsWord(final String word)
	{
		return vocabulary.contains(word);
	}
	
	public int addWord(final String word)
	{
		synchronized (vocabulary)
		{
			vocabulary.add(word);
			return vocabulary.size();
		}
	}
	
	public LinkedList<String> getAsList()
	{
		synchronized (vocabulary)
		{
			LinkedList<String> list = new LinkedList<String>(vocabulary);
			return list;
		}
	}
	
	public void saveVocabulary()
	{
		System.out.println("[Vocabulary::saveVocabulary] Saving vocabulary to " + filename);

		String content = "";

		synchronized (vocabulary)
		{
			Iterator<String> it = vocabulary.iterator();
			while (it.hasNext())
			{
			        String word = (String) it.next();
			        content += word + "\n";
			}
		}

		IOUtils.saveContentToFile(content, filename);
	}
	
	public void saveVocabularySorted()
	{
		System.out.println("[Vocabulary::saveVocabularySorted] Saving vocabulary to " + filenameSorted);

		String content = "";
		
		synchronized (vocabulary)
		{
			ArrayList<String> list = new ArrayList<String>(vocabulary);
			Collections.sort(list);
			  
			Iterator<String> it = list.iterator();
			while (it.hasNext())
			{
			        String word = (String) it.next();
			        content += word + "\n";
			}
		}

		IOUtils.saveContentToFile(content, filenameSorted);
	}
	

}
