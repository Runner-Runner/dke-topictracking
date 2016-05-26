package wordnet;

import java.util.TreeMap;

import nmf.Topic;

public class TopicSeries {
	private TreeMap<Integer, Topic> sequence;
	
	public TopicSeries() {
		sequence = new TreeMap<>();
	}
	
	public Topic getLastTopic(){
		return sequence.get(sequence.size()-1);
	}
	public void addTopic(Topic topic, int timeunit){
		sequence.put(timeunit, topic);
	}
}
