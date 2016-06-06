package model;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;


public class TopicWave
{
  private TreeMap<Integer, Topic> sequence;

  public TopicWave(int timeunit, Topic topic)
  {
    sequence = new TreeMap<>();
    addTopic(timeunit, topic);
  }

  public Topic getLastTopic()
  {
    return sequence.lastEntry().getValue();
  }

  public void addTopic(int timeunit, Topic topic)
  {
    Topic previousTopic = sequence.get(timeunit);
    //TODO Merge if previousTopic != null

    sequence.put(timeunit, topic);
  }

  public String toString(HashMap<Integer, Double> tfidfTotalMap)
  {
    String text = "";
    for (Entry<Integer, Topic> entry : sequence.entrySet())
    {
      double relativeRelevance = entry.getValue().getAbsoluteRelevance() / 
              tfidfTotalMap.get(entry.getKey());
      text += "\nTimestep: " + entry.getKey() + ", Topic (" + relativeRelevance 
              + "%): " + entry.getValue().toShortString();
    }
    return text;
  }
}
