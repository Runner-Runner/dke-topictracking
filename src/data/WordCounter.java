package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WordCounter implements WordHandler
{
  private List<String> documentNames;

  // word counts for each document
  private List<HashMap<String, Integer>> documents;
  // number of words in current document
  private HashMap<String, Integer> currentDocument;
  // whole vocabulary (includes idf)
  private Vocabulary vocabulary;

  private boolean tfidf = false;

  public WordCounter(Vocabulary vocabulary)
  {
    documents = new LinkedList<>();
    documentNames = new ArrayList<>();
    this.vocabulary = vocabulary;
  }

  public void nextFile(String name)
  {
    currentDocument = new HashMap<>();
    documents.add(currentDocument);
    documentNames.add(name);
  }

  public Vocabulary getVocabulary()
  {
    return this.vocabulary;
  }

  @Override
  public void addWord(String word)
  {
    if (vocabulary.contains(word))
    {
      Integer count = currentDocument.get(word);
      if (count == null)
      {
        currentDocument.put(word, 1);
      }
      else
      {
        currentDocument.put(word, count + 1);
      }
    }
  }

  public double calculateTFIDF(String term, Integer tf)
  {
    if (tf == null)
    {
      return 0;
    }
    double N = documents.size();
    double termDocumentFrequency = vocabulary.getTermDocumentFrequency(term);
    double idf = Math.log10(N / termDocumentFrequency);

    return tf * idf;
  }

  public double[][] getDocumentTermMatrix()
  {
    double[][] documentTermMatrix = new double[documents.size()][vocabulary.size()];
    Collection<String> vocab = vocabulary.getVocabulary();
    for (int i = 0; i < documents.size(); i++)
    {
      HashMap<String, Integer> document = documents.get(i);
      int j = 0;
      for (String term : vocab)
      {
        Integer tf = document.get(term);
        documentTermMatrix[i][j] = calculateTFIDF(term, tf);
        j++;
      }
    }
    return documentTermMatrix;
  }

  public List<String> getDocumentNames()
  {
    return documentNames;
  }
}
