package preProcessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import container.Vocabulary;

public class WordCounter {
	private LinkedList<LinkedHashMap<Integer, Integer>> documents;
	private LinkedList<String> vocabulary; // word -> index
	final private String corpusPath;
	final private String resultDir;
	final private String resultFilename = "wc.txt";

	public WordCounter(final String resultDir, final Vocabulary vocabulary)
	{
		this.resultDir = resultDir;
		this.corpusPath = resultDir + "/processedDocs";

		this.vocabulary = vocabulary.getAsList();
		
		documents = new LinkedList<LinkedHashMap<Integer, Integer>>();
		
		if (Files.exists(Paths.get(resultDir + "/" + resultFilename))) 
		{
			try (Stream<String> lines = Files.lines(Paths.get(resultDir + "/" + resultFilename), Charset.defaultCharset()))
			{
				System.out.println("[WordCounter] Loading existing word counts from " + resultDir + "/" + resultFilename);
				
				lines.forEachOrdered(doc -> readCounts(doc));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private void readCounts(String doc)
	{
		String[] words = doc.split(" ");
		
		int numWords = Integer.parseInt(words[0]);
		
		LinkedHashMap<Integer, Integer> currentDocument = new LinkedHashMap<Integer, Integer>();
		
		for (int i = 1; i < words.length; ++i)
		{
			String[] wordAndCount = words[i].split(":");
			
			int word = Integer.parseInt(wordAndCount[0]);
			int count = Integer.parseInt(wordAndCount[1]);
			
			currentDocument.put(word, count);
		}
		
		if (currentDocument.size() == numWords)
		{
			documents.add(currentDocument);
		}
		else
		{
			System.out.println("[WordCounter::readCounts] ERROR: size does not fit");
		}
	}
	
	private void count(final String text)
	{
		LinkedHashMap<Integer, Integer> currentDocument = new LinkedHashMap<Integer, Integer>();
		
		String splitter = " ";
		if (text.contains(splitter)) 
		{
			String[] parts = text.split(Pattern.quote(splitter));
			
			for (String part : parts)
			{
				if (vocabulary.contains(part))
				{
					Integer index = vocabulary.indexOf(part);
					if (currentDocument.containsKey(index))
					{
						Integer count = currentDocument.get(index);
						currentDocument.put(index, ++count);
					}
					else
					{
						currentDocument.put(index, 1);
					}
				}
			}
		}
		
		documents.add(currentDocument);
	}

	private String readDocument(final Path filePath)
	{
		try 
		{
			return new String(Files.readAllBytes(filePath));
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public void processDocument(final Path filePath)
	{
		String text = readDocument(filePath);

		count(text);
	}

	public void processDocuments(final String extension)
	{
		try (Stream<Path> paths = Files.walk(Paths.get(corpusPath))) 
		{
			System.out.println("[WordCounter::processDocuments] Reading documents from " + corpusPath);

			paths
				.filter(p -> !p.toFile().isDirectory() && p.toFile().getAbsolutePath().endsWith(extension))
				.sorted()
				.forEachOrdered(p -> processDocument(p));	
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		saveWordCounts();
	}
	
	public void saveWordCounts()
	{
		// has to be done linewise, writing everything as one whole string is too much !
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(resultDir + "/" + resultFilename, "UTF-8");
			
			System.out.println("[WordCounter::saveWordCounts] Saving word counts to " + resultDir + "/" + resultFilename);

			Iterator<LinkedHashMap<Integer, Integer>> it = documents.iterator();
			while (it.hasNext())
			{
				LinkedHashMap<Integer, Integer> currentDoc = it.next();
				
				String content = currentDoc.size() + " ";
				
				for (Entry<Integer, Integer> entry : currentDoc.entrySet()) 
				{
					content += entry.getKey() + ":" + entry.getValue() + " ";
				}
				writer.println(content);
			}
			
			writer.close();
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
