package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class WordCounter implements WordHandler {
	private List<String> documentNames;

	// word counts for each document
	private List<HashMap<String, Integer>> documents;
	// number of words in current document
	private HashMap<String, Integer> currentDocument;
	// whole vocabulary (includes idf)
	private Vocabulary vocabulary;

	private boolean tfidf = false;

	public WordCounter(Vocabulary vocabulary) {
		documents = new LinkedList<>();
		documentNames = new ArrayList<>();
		this.vocabulary = vocabulary;
	}

	public void nextFile(String name) {
		currentDocument = new HashMap<>();
		documents.add(currentDocument);
		documentNames.add(name);
	}

	public Vocabulary getVocabulary() {
		return this.vocabulary;
	}
	// public void saveDocuments(String fileName)
	// {
	// PrintWriter writer;
	// try
	// {
	// writer = new PrintWriter(fileName, "UTF-8");
	// for (HashMap<String, Integer> doc : documents)
	// {
	// // start with number of pairs
	// writer.print(doc.size() + " ");
	// for (Entry<String, Integer> entry : doc.entrySet())
	// {
	//
	// String term = entry.getKey();
	// String termValue;
	// if (tfidf)
	// {
	// termValue = String.valueOf(calculateTFIDF(term, entry.getValue()));
	// }
	// else
	// {
	// termValue = String.valueOf(entry.getValue());
	// }
	//
	// writer.print(vocabulary.get(term) + ":" + termValue + " ");
	// }
	// writer.println();
	// }
	// writer.close();
	// }
	// catch (FileNotFoundException | UnsupportedEncodingException e)
	// {
	// e.printStackTrace();
	// }
	// }

	@Override
	public void addWord(String word) {
		if (vocabulary.contains(word)) {
			Integer count = currentDocument.get(word);
			if (count == null) {
				currentDocument.put(word, 1);
			} else {
				currentDocument.put(word, count + 1);
			}
		}
	}

	public double calculateTFIDF(String term, Integer tf) {
		if (tf == null) {
			return 0;
		}
		double N = documents.size();
		double termDocumentFrequency = vocabulary.getTermDocumentFrequency(term);
		double idf = Math.log10(N / termDocumentFrequency);

		return tf * idf;
	}

	public double[][] getDocumentTermMatrix() {
		double[][] documentTermMatrix = new double[documents.size()][vocabulary.size()];
		Collection<String> vocab = vocabulary.getVocabulary();
		for (int i = 0; i < documents.size(); i++) {
			HashMap<String, Integer> document = documents.get(i);
			int j = 0;
			for (String term : vocab) {
				Integer tf = document.get(term);
				documentTermMatrix[i][j] = calculateTFIDF(term, tf);
				j++;
			}
		}
		return documentTermMatrix;
	}

	public List<String> getDocumentNames() {
		return documentNames;
	}
}
