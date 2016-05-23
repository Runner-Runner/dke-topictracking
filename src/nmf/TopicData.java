package nmf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import la.matrix.Matrix;
import la.vector.Vector;

public class TopicData
{
  //TODO Link most relevant documents?

  private Matrix topicTermMatrix;
  private Matrix topicDocumentMatrix;
  private Set<String> vocabulary;

  private TreeMap<Double, Topic> topics;
  private final List<String> documentNames;

  public TopicData(Matrix topicTermMatrix, Matrix topicDocumentMatrix,
          Set<String> vocabulary, List<String> documentNames)
  {
    this.topicTermMatrix = topicTermMatrix;
    this.topicDocumentMatrix = topicDocumentMatrix;
    this.vocabulary = vocabulary;
    this.documentNames = documentNames;
    
    loadTopics();
  }

  public Collection<Topic> getTopics()
  {
    return topics.values();
  }

  private void loadTopics()
  {
    topics = new TreeMap<>();

    int topicCount = topicTermMatrix.getRowDimension();
    int termCount = topicTermMatrix.getColumnDimension();

    for (int i = 0; i < topicCount; i++)
    {
      Topic topic = new Topic();

      Iterator<String> iterator = vocabulary.iterator();
      for (int j = 0; j < termCount; j++)
      {
        String term = iterator.next();
        double termValue = topicTermMatrix.getEntry(i, j);
        topic.addTerm(term, termValue);
      }

      Vector columnVector = topicDocumentMatrix.getColumnVector(i);
      double tfidfSum = 0;
      
      TreeMap<Double, String> documentRankings = new TreeMap<>();
      //TODO Maybe just wrong dimension here?
      for (int j = 0; j < columnVector.getDim(); j++)
      {
        double tfidf = columnVector.get(j);
        tfidfSum += tfidf;
        documentRankings.put(tfidf, documentNames.get(j));
      }
      topic.setDocumentRanking(documentRankings);
      topics.put(tfidfSum, topic);
    }
  }

  @Override
  public String toString()
  {
    Iterator<Map.Entry<Double, Topic>> topicIterator = topics.descendingMap().entrySet().iterator();
    int index = 0;
    String text = "";
    while(topicIterator.hasNext())
    {
      index++;
      Map.Entry<Double, Topic> topicEntry = topicIterator.next();
      Double cumulatedTfidf = topicEntry.getKey();
      Topic topic = topicEntry.getValue();
      Iterator<Map.Entry<Double, String>> termIterator = 
              topic.getTerms().descendingMap().entrySet().iterator();
      
      String termsText = "";
      int termIndex = 0;
      while(termIterator.hasNext() && termIndex < 10)
      {
        termIndex++;
        Map.Entry<Double, String> termEntry = termIterator.next();
        Double tfidf = termEntry.getKey();
        if(tfidf <= 0.00001)
        {
          break;
        }
        String term = termEntry.getValue();
        termsText += " " + term + " (" + tfidf + ")";
      }
      
      termsText += "\nMost Relevant Documents: ";
      Iterator<Map.Entry<Double, String>> docIterator = 
              topic.getDocumentRankings().descendingMap().entrySet().iterator();
      int docIndex = 0;
      while(docIterator.hasNext() && docIndex < 3)
      {
        docIndex++;
        Map.Entry<Double, String> docEntry = docIterator.next();
        Double tfidf = docEntry.getKey();
        if(tfidf <= 0.00001)
        {
          break;
        }
        String doc = docEntry.getValue();
        termsText += " " + doc + " (" + tfidf + ")";
      }
      
      text += "\nTopic #"+index + " ("+cumulatedTfidf+"):" + termsText;
    }
    return text;
  }
}
