package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TopicRiver
{
  private List<TopicWave> waves;

  private HashMap<Date, Double> tfidfTotalMap;
  private Date startDate;
  private Date endDate;

  public TopicRiver()
  {
    waves = new ArrayList<>();
    tfidfTotalMap = new HashMap<>();
    startDate = new Date(Long.MAX_VALUE);
    endDate = new Date(Long.MIN_VALUE);
  }

  public Double getTfidfTotal(int timeUnit)
  {
    return tfidfTotalMap.get(timeUnit);
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public void addTopicTimeStepCollection(Date timestamp, TopicTimeStepCollection topicData)
  {
    Calendar cal = Calendar.getInstance();
    if (topicData.getTimestamp().before(startDate))
    {
      startDate = topicData.getTimestamp();
    }
    cal.setTime(topicData.getTimestamp());
    cal.add(Calendar.DATE, topicData.getInterval());
    if (endDate.before(cal.getTime()))
    {
      endDate = cal.getTime();
    }
    for (Topic topic : topicData.getTopicList())
    {
      addTopic(timestamp, topic);
    }
    tfidfTotalMap.put(timestamp, topicData.getAbsoluteValuesTotal());
  }

  private void addTopic(Date timestamp, Topic topic)
  {
    TopicWave bestWave = null;
    double bestValue = -1;
    // TODO handle multiple best values (same nr of matches)
    // TODO How to merge, how to split?
    for (TopicWave wave : waves)
    {
      Topic existingTopic = wave.getTopicSequence().get(timestamp);
      Topic secondLastTopic = wave.getLastTopic(2);

      double currentScore;
      if (existingTopic != null && secondLastTopic != null)
      {
        currentScore = TopicMatcher.compareTopics(secondLastTopic, topic);
        double previousScore = TopicMatcher.compareTopics(secondLastTopic, existingTopic);

        if (currentScore <= previousScore)
        {
          continue;
        }
      }
      else
      {
        currentScore = TopicMatcher.compareTopics(topic, wave.getLastTopic());
      }

      if (currentScore > bestValue)
      {
        bestValue = currentScore;
        bestWave = wave;
      }
    }
    if (bestWave != null && bestValue > TopicMatcher.TOPIC_THRESHOLD)
    {
      System.out.println("New Topic: " + topic);
      System.out.println("Best Matching Wave (" + bestValue + "): " + bestWave.getName(", "));
      System.out.println(bestWave.getLastTopic());
      System.out.println("------------------------");

      Topic existingTopic = bestWave.getTopicSequence().get(timestamp);
      bestWave.addTopic(timestamp, topic);
      if (existingTopic != null)
      {
        addTopic(timestamp, existingTopic);
      }
    }
    else
    {
      TopicWave newWave = new TopicWave(timestamp, topic);
      waves.add(newWave);
    }
  }

  public static TopicRiver loadTopicRiver(String directoryName)
  {
    File directory = new File(directoryName);
    if (!directory.isDirectory())
    {
      System.out.println("Path of topic river data is no existing directory!");
      return null;
    }

    File[] files = directory.listFiles();
    TopicRiver topicRiver = new TopicRiver();
    for (int i = 0; i < files.length; i++)
    {
      TopicTimeStepCollection topicTimeStepCollection = TopicTimeStepCollection.load(files[i].getAbsolutePath());
      topicRiver.addTopicTimeStepCollection(topicTimeStepCollection.getTimestamp(), topicTimeStepCollection);
    }
    return topicRiver;
  }

  @Override
  public String toString()
  {
    String text = "Topic River: \n--------------------------";
    int count = 0;
    for (TopicWave wave : waves)
    {
      count++;
      text += "\nTopicWave #" + count + ": " + wave.toString(tfidfTotalMap) + "\n--------------------------";
    }
    return text;
  }

  public List<TopicWave> getWaves()
  {
    return waves;
  }
  
  public void sortList(){
	  TopicWave currentWave = null;
	  double bestValue = Double.MIN_VALUE;
	  for(TopicWave wave: waves){
		  if(wave.getRelativeRelevance()>bestValue){
			  currentWave = wave;
			  bestValue = wave.getRelativeRelevance();
		  }
	  }
	  List<TopicWave> nearestList = new ArrayList<>();
	  nearestList.add(currentWave);
	  bestValue = Double.MIN_VALUE;
	  TopicWave bestMatch=null;
	  waves.remove(currentWave);
	  while(waves.size()>0){
		  for(TopicWave wave: waves){
			  double similarity = TopicMatcher.compareTopicWaves(currentWave, wave);
			  if(similarity>bestValue){
				  bestMatch = wave;
				  bestValue = similarity;
			  }
		  }
		  waves.remove(bestMatch);
		  nearestList.add(bestMatch);
		  currentWave = bestMatch;
		  bestValue = Double.MIN_VALUE;
	  }
  }
}







