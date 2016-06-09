package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TopicWave
{
  private TreeMap<Integer, Topic> topicSequence;

  public TopicWave(int timeunit, Topic topic)
  {
    topicSequence = new TreeMap<>();
    addTopic(timeunit, topic);
  }

  public Topic getLastTopic()
  {
    return topicSequence.lastEntry().getValue();
  }

  public TreeMap<Integer, Topic> getTopicSequence()
  {
    return topicSequence;
  }

  public void addTopic(int timeunit, Topic topic)
  {
    Topic previousTopic = topicSequence.get(timeunit);
    //TODO Merge if previousTopic != null

    topicSequence.put(timeunit, topic);
  }

  public double getRelativeRelevance()
  {
    double averageRelativeRelevance = 0;
    for (Topic topic : topicSequence.values())
    {
      averageRelativeRelevance += topic.getRelativeRelevance();
    }
    averageRelativeRelevance /= topicSequence.size();
    return averageRelativeRelevance;
  }

  public HashMap<String, Double> getTermWaveRelevances()
  {
    HashMap<String, Double> terms = new HashMap<>();
    double tfidfAll = 0;
    for (Topic topic : topicSequence.values())
    {
      double tfidfTopicSum = 0;
      for (double d : topic.getTerms().keySet())
      {
        tfidfTopicSum += d;
      }
      for (Entry<Double, String> entry : topic.getTerms().entrySet())
      {
        Double tfidf = terms.get(entry.getValue());
        double add = (entry.getKey() / tfidfTopicSum) * topic.getRelativeRelevance();
        tfidfAll += add;
        if (tfidf == null)
        {
          terms.put(entry.getValue(), add);
        }
        else
        {
          terms.put(entry.getValue(), tfidf + add);
        }
      }
      for (Entry<String, Double> entry : terms.entrySet())
      {
        entry.setValue(entry.getValue() / tfidfAll);
      }
    }

    return terms;
  }

  public String getName()
  {
    int termCount = 0;
    List<String> bestTerms = new ArrayList<>();
    for (Entry<Double, String> terms : getTermsAverageTFIDF().descendingMap().
            entrySet())
    {
      bestTerms.add(terms.getValue());
      termCount++;
      if (termCount == 5)
      {
        break;
      }
    }
    String name = String.join(", ", bestTerms);
    return name;
  }

  public TreeMap<Double, String> getTermsAverageTFIDF()
  {
    HashMap<String, Double> termWaveRelevances = getTermWaveRelevances();
    TreeMap<Double, String> ret = new TreeMap<>();
    for (Entry<String, Double> entry : termWaveRelevances.entrySet())
    {
      ret.put(entry.getValue() / topicSequence.size(), entry.getKey());
    }
    return ret;
  }

  public String toString(HashMap<Integer, Double> tfidfTotalMap)
  {
    String text = "";
    for (Entry<Integer, Topic> entry : topicSequence.entrySet())
    {
      double relativeRelevance = entry.getValue().getAbsoluteRelevance()
              / tfidfTotalMap.get(entry.getKey());
      text += "\nTimestep: " + entry.getKey() + ", Topic (" + relativeRelevance * 100
              + "%): " + entry.getValue().toShortString();
    }
    return text;
  }

}
