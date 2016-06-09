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
  
  public TreeMap<Double, String> getAverageTFIDF(){
	  HashMap<String,Double> terms = new HashMap<>();
	  for(Topic topic:topicSequence.values()){
		  for(Entry<Double,String> entry: topic.getTerms().entrySet()){
			  Double tfidf = terms.get(entry.getValue());
			  if(tfidf == null){
				  terms.put(entry.getValue(), entry.getKey());
			  }
			  else{
				  terms.put(entry.getValue(), tfidf+entry.getKey());
			  }
		  }
	  }
	  TreeMap<Double, String> ret = new TreeMap<>();
	  for(Entry<String,Double> entry:terms.entrySet()){
		  ret.put(entry.getValue()/topicSequence.size(),entry.getKey());
	  }
	  return ret;
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
