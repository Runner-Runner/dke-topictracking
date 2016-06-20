package preProcessing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import data.DocumentHandlerInterface;
import data.ReutersXMLHandler;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import tools.IOTools;
import wordContainer.Dictionary;
import wordContainer.Vocabulary;

public class PreProcessor 
{

	final private String corpusPath;
	
	final private String resultPath;
	
	final private String processedDocsPath;
	
	final private String processedDocsDir = "processedDocs";
	
	final private String docFilename = "docs";
	
	final private String metaDataFilename = "metadata.txt";
	
	private DocumentHandlerInterface docReader;
	
	private Vocabulary vocabulary;
	
	private Vocabulary nonVocabulary;
	
	private Vocabulary nerVocabulary;
	
	private Vocabulary spellVocabulary;
	
	private Vocabulary tokenVocabulary;
	
	private Dictionary dictionary;
	
	private Dictionary stopWords;
	
	private HashSet<String> NEExcludedCategories;
	
	private LinkedList<String> liDocData;
	
	private int numTokens;
	private int numCorrections;

	/**
	 * Corpus document preprocessor
	 * Creates output directories, if they do not exist.
	 * 
	 * @param corpusDir
	 * @param resultDir
	 * @param stopwordsFile
	 * @param dicFile
	 * @param nerExclusionCategories
	 * @param vocFilename
	 * @param docReader
	 * @throws Exception
	 */
	public PreProcessor(final String corpusDir, 
			final String resultDir, 
			final String stopwordsFile, 
			final String dicFile,
			final List<String> nerExclusionCategories,
			final String vocFilename,
			DocumentHandlerInterface docReader) 
					throws Exception
	{
		this.corpusPath = corpusDir;
		this.resultPath = resultDir;
		
		this.docReader = docReader;
		
		if (!Files.exists(Paths.get(resultDir)))
		{
			Files.createDirectory(Paths.get(resultDir));
		}
		else if (!Files.isDirectory(Paths.get(resultDir)))
		{
			throw new Exception("ERROR: " + resultDir + " already exists but is no a directory.");
		}
		
		processedDocsPath = resultDir + "/" + processedDocsDir;
		if (!Files.exists(Paths.get(processedDocsPath)))
		{
			Files.createDirectory(Paths.get(processedDocsPath));
		}
		else if (!Files.isDirectory(Paths.get(processedDocsPath)))
		{
			throw new Exception("ERROR: " + processedDocsPath + " already exists but is no a directory.");
		}
		
		dictionary = new Dictionary(dicFile);
		
		stopWords = new Dictionary(stopwordsFile);

		NEExcludedCategories = new HashSet<String>();
		
		for (String category : nerExclusionCategories)
		{
			NEExcludedCategories.add(category.trim());
		}
		
//		NEExcludedCategories.add("DATE");
//		NEExcludedCategories.add("DURATION");
////		NEExcludedCategories.add("LOCATION");
////		NEExcludedCategories.add("MISC");
//		NEExcludedCategories.add("MONEY");
//		NEExcludedCategories.add("NUMBER");
////		NEExcludedCategories.add("O");
//		NEExcludedCategories.add("ORDINAL");
////		NEExcludedCategories.add("ORGANIZATION");
//		NEExcludedCategories.add("PERCENT");
////		NEExcludedCategories.add("PERSON");
//		NEExcludedCategories.add("TIME");
//		NEExcludedCategories.add("SET");
				
		vocabulary = new Vocabulary(resultDir + "/" + vocFilename);
		
		nonVocabulary = new Vocabulary(resultDir + "/" + vocFilename + "non");
		
		nerVocabulary = new Vocabulary(resultDir + "/" + vocFilename +  "ner");
		
		spellVocabulary = new Vocabulary(resultDir + "/" + vocFilename +  "spell");
		
		tokenVocabulary = new Vocabulary(resultDir + "/" + vocFilename +  "token");
		
		liDocData = new LinkedList<String>();
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
	
	/**
	 * Main NLP processing
	 * 
	 * @param text	raw document text
	 * @return refined text
	 */
	private String getLemmas(final String text)
	{
		String processedText = "";
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for(CoreMap sentence: sentences) 
		{
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) 
			{
				// this is the lemmatized text of the token
				String lemma = token.getString(LemmaAnnotation.class);
				
				// this is the NER label of the token
			    String ne = token.get(NamedEntityTagAnnotation.class);
			    
			    if (!NEExcludedCategories.contains(ne))
			    {
			    	if (!NLPUtils.containsSpecialChars(lemma))
			    	{
					    lemma = lemma.replace("-", "");

					    lemma = spellCheck(lemma, 1);
			    		
			    		if (!stopWords.containsWord(lemma.toLowerCase()))
					    {
							String lowerCase = lemma.toLowerCase();
					    	vocabulary.addWord(lowerCase);
							processedText += lowerCase + " ";
					    }
			    		else
			    		{
			    			nonVocabulary.addWord(lemma);
			    		}
			    	}
			    	else
		    		{
		    			nonVocabulary.addWord(lemma);
		    		}
			    }
			    else
			    {
			    	nerVocabulary.addWord(ne + " : " + lemma);
			    }
				
			    tokenVocabulary.addWord(token.get(TextAnnotation.class));
				++numTokens;
			}
		}
		
		return processedText;
	}
	
	private void saveProcessedText(final Path filePath, 
			final String document)
	{
		String filename = filePath.getFileName().toString().split("\\.")[0];
		
		String outFile = processedDocsPath + "/" + filename + ".txt";
		
		IOTools.saveContentToFile(document, outFile);
	}
	
	public void processDocument(final Path filePath)
	{
		String text = docReader.readDocumentText(filePath, false);
		//String text = readDocument(filePath);
		
		String processedText = getLemmas(text);
		
		saveProcessedText(filePath, processedText);
	}
	
	public void processDocumentMeta(final Path filePath)
	{
        String metaData = docReader.readDocumentMetaData(filePath);
        
        //System.out.println("metadata: " + data);
        liDocData.add(metaData);
	}
	
	public void processDocumentsMeta(final String extension)
	{
		try (Stream<Path> paths = Files.walk(Paths.get(corpusPath))) 
		{
			System.out.println("[CorpusProcessor::processDocumentsMeta] Reading meta data from " + corpusPath);

			// this works recursively in subdurs
			paths
				.filter(p -> !p.toFile().isDirectory() && p.toFile().getAbsolutePath().endsWith(extension))
				.sorted()
				.forEachOrdered(p -> processDocumentMeta(p));	

		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		saveMetaData(resultPath + "/" + metaDataFilename);
	}
	
	public void processDocuments(final String extension)
	{
		//Files.walk(path).collect(toList()).parallelStream()
		try (Stream<Path> paths = Files.walk(Paths.get(corpusPath))) 
		{
			System.out.println("[CorpusProcessor::processDocuments] Reading documents from " + corpusPath);

			paths
				.filter(p -> !p.toFile().isDirectory() && p.toFile().getAbsolutePath().endsWith(extension))
				.parallel().forEach(p -> processDocument(p));	

		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Number of tokens overall: " + numTokens);
		System.out.println("Number of spell corrections: " + numCorrections);

		vocabulary.saveVocabulary();
		
		vocabulary.saveVocabularySorted();
		
		nonVocabulary.saveVocabularySorted();
		
		nerVocabulary.saveVocabularySorted();
		
		spellVocabulary.saveVocabularySorted();
		
		tokenVocabulary.saveVocabularySorted();
	}
	
	private void saveMetaData(final String fileName)
	{
		System.out.println("[CorpusProcessor::saveMetaData] Saving metadata to " + fileName);

		String content = "";
		
		Iterator<String> it = liDocData.iterator();
		while (it.hasNext())
		{
			String data = (String) it.next();
			content += data + "\n";
		}

		IOTools.saveContentToFile(content, fileName);
	}
	
	private String spellCheck(final String word, 
			final int numDiffs)
	{
		int wordLength = word.length();

		if (Character.isUpperCase(word.charAt(0)) || wordLength < 4 || dictionary.containsWord(word.toLowerCase()))
		{
			return word;
		}
		
		List<String> dict = dictionary.getAsList();
		
		int ambiguities = 0;
		String temp = word;
		
		for (String correct : dict)
		{
			int correctLength = correct.length();
			if ((correctLength == wordLength)
					|| (correctLength == (wordLength + numDiffs))
					|| (wordLength == (correctLength + numDiffs)))
			{
				int distance = NLPUtils.levenshteinDistance(correct, word);
				
				if (distance == 0)
				{
					return word;
				}
				else if (distance == numDiffs)
				{
					temp = correct;
					++ambiguities;
				}
			}
		}
		
		if (ambiguities == 1)
		{
			spellVocabulary.addWord(word + " : " + temp);
			++numCorrections;
			return temp;
			
		}
		return word;
	}
}
