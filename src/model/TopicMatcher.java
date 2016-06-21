package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;

public class TopicMatcher
{

  public static final double TOPIC_THRESHOLD = 0.2;
  
  public TopicMatcher()
  {

  }

  /**
   * Calculate similarity via Cosine Similarity metric. Did not yield better 
   * results than the weighted term metric while creating more topics.
   * 
   * @param topic1
   * @param topic2
   * @return 
   */
  public double compareTopicsCosine(Topic topic1, Topic topic2)
  {
    Set<Map.Entry<Double, String>> termSet1 = topic1.getTerms().entrySet();
    HashMap<String, Double> terms2 = topic2.getBestTerms(-1);
    
    double dotProduct = 0.0;
    double sumTopic1 = 0.0;
    double sumTopic2 = 0.0;
    for(Entry<Double, String> entry1 : termSet1)
    {
      Double tfidf1 = entry1.getKey();
      Double tfidf2 = terms2.get(entry1.getValue());
      if(tfidf2 == null)
      {
        continue;
      }
      dotProduct += tfidf1 * tfidf2;
      sumTopic1 += tfidf1 * tfidf1;
      sumTopic2 += tfidf2 * tfidf2;
    }
    double normTopic1 = Math.sqrt(sumTopic1);
    double normTopic2 = Math.sqrt(sumTopic2);
    
    double cosineDiff = dotProduct / (normTopic1 * normTopic2);
    
    return cosineDiff;
  }
  
  public double compareTopics(Topic topic1, Topic topic2)
  {
    int termAmount = 20;
    HashMap<String, Double> bestTerms1 = topic1.getBestTerms(termAmount);
    HashMap<String, Double> bestTerms2 = topic2.getBestTerms(termAmount);

    int matches = 0;
    double score = 0;
    for (String term : bestTerms1.keySet())
    {
      if (bestTerms2.keySet().contains(term))
      {
        double combinedValue = bestTerms1.get(term) + bestTerms2.get(term);
        score += combinedValue;
        matches++;
      }
    }
    score *= matches;
    return score;
  }

 


}
