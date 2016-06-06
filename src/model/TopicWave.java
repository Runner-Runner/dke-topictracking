package model;

import java.util.Map.Entry;
import java.util.TreeMap;

import nmf.Topic;

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

  @Override
  public String toString()
  {
    String text = "";
    for (Entry<Integer, Topic> entry : sequence.entrySet())
    {
      text += "\nTimestep: " + entry.getKey() + ", Topic (" + 
              entry.getValue().getAbsoluteRelevance() + "): "
              + entry.getValue().toShortString();
    }
    return text;
  }
}
