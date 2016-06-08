package model;

import java.util.HashMap;
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

  public String toString(HashMap<Integer, Double> tfidfTotalMap)
  {
    String text = "";
    for (Entry<Integer, Topic> entry : topicSequence.entrySet())
    {
      double relativeRelevance = entry.getValue().getAbsoluteRelevance() / 
              tfidfTotalMap.get(entry.getKey());
      text += "\nTimestep: " + entry.getKey() + ", Topic (" + relativeRelevance 
              + "%): " + entry.getValue().toShortString();
    }
    return text;
  }
}
