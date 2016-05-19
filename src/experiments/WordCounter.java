package experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class WordCounter implements WordHandler
{
  private List<String> documentNames;
  private List<HashMap<String, Integer>> documents;
  private HashMap<String, Integer> currentDocument; // word -> count
  private HashMap<String, Integer> vocabulary; // word -> index

  private boolean tfidf = false;

  public WordCounter(String vocabularyFile)
  {
    documents = new LinkedList<>();
    documentNames = new ArrayList<>();
    loadVocabulary(vocabularyFile);
  }

  private void loadVocabulary(String fileName)
  {
    vocabulary = new HashMap<>();
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      String line = br.readLine();
      int index = 0;
      while (line != null)
      {
        vocabulary.put(line, index);
        index++;
        line = br.readLine();
      }
      br.close();
    }
    catch (Exception x)
    {
    }
  }

  public void startNewDocument(String name)
  {
    currentDocument = new HashMap<>();
    documents.add(currentDocument);
    documentNames.add(name);
  }

  public void saveDocuments(String fileName)
  {
    PrintWriter writer;
    try
    {
      writer = new PrintWriter(fileName, "UTF-8");
      for (HashMap<String, Integer> doc : documents)
      {
        // start with number of pairs
        writer.print(doc.size() + " ");
        for (Entry<String, Integer> entry : doc.entrySet())
        {

          String term = entry.getKey();
          String termValue;
          if (tfidf)
          {
            termValue = String.valueOf(calculateTFIDF(term, entry.getValue()));
          }
          else
          {
            termValue = String.valueOf(entry.getValue());
          }

          writer.print(vocabulary.get(term) + ":" + termValue + " ");
        }
        writer.println();
      }
      writer.close();
    }
    catch (FileNotFoundException | UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void addWord(String word)
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

  public double calculateTFIDF(String term, Integer tf)
  {
    if(tf == null)
    {
      return 0;
    }
    double N = documents.size();
    double termDocumentFrequency = 0;
    for (HashMap<String, Integer> document : documents)
    {
      if (document.containsKey(term))
      {
        termDocumentFrequency++;
      }
    }
    double idf = Math.log10(N / termDocumentFrequency);

    return tf * idf;
  }

  public double[][] getDocumentTermMatrix()
  {
    double[][] documentTermMatrix = new double[documents.size()][vocabulary.size()];

    for(int i=0; i<documents.size(); i++)
    {
      HashMap<String, Integer> document = documents.get(i);
      Iterator<String> vocabularyIterator = vocabulary.keySet().iterator();
      for(int j=0; j<vocabulary.size(); j++)
      {
        String term = vocabularyIterator.next();
        Integer tf = document.get(term);
        documentTermMatrix[i][j] = calculateTFIDF(term, tf);
      }
    }
    return documentTermMatrix;
  }

  public HashMap<String, Integer> getVocabulary()
  {
    return vocabulary;
  }
  
  public List<String> getDocumentNames()
  {
    return documentNames;
  }
}
