package model;

import nmf.Topic;
import java.util.ArrayList;
import java.util.List;

public class TopicRiver
{
  private List<TopicWave> waves;
  private TopicMatcher topicMatcher;
  
  //TODO Store week date?
  
  public TopicRiver()
  {
    waves = new ArrayList<>();
    topicMatcher = new TopicMatcher();
  }
  
  public void addTopicData(int timeUnit, TopicData topicData)
  {
    for(Topic topic : topicData.getTopics())
    {
      addTopic(timeUnit, topic);
    }
  }
  
  public void addTopic(int timeUnit, Topic topic)
  {
    TopicWave bestWave = null;
    double bestValue = -1;
    //TODO handle multiple best values (same nr of matches)
    //TODO How to merge, how to split?
    for(TopicWave wave : waves)
    {
      double matchScore = topicMatcher.compareTopics(topic, wave.getLastTopic());
      if(matchScore > bestValue)
      {
        bestValue = matchScore;
        bestWave = wave;
      }
    }
    if(bestWave != null && bestValue >= TopicMatcher.TOPIC_THRESHOLD)
    {
      bestWave.addTopic(timeUnit, topic);
    }
    else
    {
      TopicWave newWave = new TopicWave(timeUnit, topic);
      waves.add(newWave);
    }
  }

  @Override
  public String toString()
  {
    String text = "";
    int count = 0;
    for(TopicWave wave : waves)
    {
      count++;
      text += "\nTopicWave #" + count + ": " + wave.toString() + 
              "\n--------------------------";
    }
    return text;
  }
}
