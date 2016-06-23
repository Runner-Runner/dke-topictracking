package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TopicWave {
	private TreeMap<Date, Topic> topicSequence;

	public TopicWave(Date date, Topic topic) {
		topicSequence = new TreeMap<>();
		addTopic(date, topic);
	}

	public Topic getLastTopic() {
		return topicSequence.lastEntry().getValue();
	}

	public Topic getLastTopic(int nthIndex) {
		Iterator<Topic> iterator = topicSequence.descendingMap().values().iterator();
		Topic topic = null;
		for (int i = 0; i < nthIndex; i++) {
			if (!iterator.hasNext()) {
				break;
			}
			topic = iterator.next();
		}
		return topic;
	}

	public TreeMap<Date, Topic> getTopicSequence() {
		return topicSequence;
	}

	public void addTopic(Date timestamp, Topic topic) {
		topicSequence.put(timestamp, topic);
	}

	public double getRelativeRelevance() {
		double averageRelativeRelevance = 0;
		for (Topic topic : topicSequence.values()) {
			averageRelativeRelevance += topic.getRelativeRelevance();
		}
		averageRelativeRelevance /= topicSequence.size();
		return averageRelativeRelevance;
	}

	public HashMap<String, Double> getTermWaveRelevances() {
		HashMap<String, Double> terms = new HashMap<>();
		double tfidfAll = 0;
		for (Topic topic : topicSequence.values()) {
			double tfidfTopicSum = 0;
			for (double d : topic.getTerms().keySet()) {
				tfidfTopicSum += d;
			}
			for (Entry<Double, String> entry : topic.getTerms().entrySet()) {
				Double tfidf = terms.get(entry.getValue());
				double add = (entry.getKey() / tfidfTopicSum) * topic.getRelativeRelevance();
				tfidfAll += add;
				if (tfidf == null) {
					terms.put(entry.getValue(), add);
				} else {
					terms.put(entry.getValue(), tfidf + add);
				}
			}
			for (Entry<String, Double> entry : terms.entrySet()) {
				entry.setValue(entry.getValue() / tfidfAll);
			}
		}

		return terms;
	}

	public String getName(String delimitter) {
		int termCount = 0;
		List<String> bestTerms = new ArrayList<>();
		for (Entry<Double, String> terms : getTermsAverageTFIDF().descendingMap().entrySet()) {
			bestTerms.add(terms.getValue());
			termCount++;
			if (termCount == 5) {
				break;
			}
		}
		String name = String.join(delimitter, bestTerms);
		return name;
	}

	public TreeMap<Double, String> getTermsAverageTFIDF() {
		HashMap<String, Double> termWaveRelevances = getTermWaveRelevances();
		TreeMap<Double, String> ret = new TreeMap<>();
		for (Entry<String, Double> entry : termWaveRelevances.entrySet()) {
			ret.put(entry.getValue() / topicSequence.size(), entry.getKey());
		}
		return ret;
	}

	public String toString(HashMap<Date, Double> tfidfTotalMap) {
		String text = "";
		for (Entry<Date, Topic> entry : topicSequence.entrySet()) {
			double relativeRelevance = entry.getValue().getAbsoluteRelevance() / tfidfTotalMap.get(entry.getKey());
			text += "\nTimestep: " + entry.getKey() + ", Topic (" + relativeRelevance * 100 + "%): "
					+ entry.getValue().toShortString();
		}
		return text;
	}

	public Topic getTopic(Date date) {
		Calendar cal = Calendar.getInstance();
		for (Topic topic : topicSequence.values()) {
			Date start = topic.getTimeStamp();
			cal.setTime(start);
			cal.add(Calendar.DATE, topic.getInterval());
			Date endDate = cal.getTime();
			if (start.compareTo(date) <= 0 && endDate.compareTo(date) >= 0)
				return topic;
		}
		return null;
	}
	
	public Map<String, Double> getBestTerms(int count){
		int termCount = 0;
		Map<String, Double> bestTerms = new HashMap<>();
		for (Entry<Double, String> terms : getTermsAverageTFIDF().descendingMap().entrySet()) {
			bestTerms.put(terms.getValue(), terms.getKey());
			termCount++;
			if (termCount == 5) {
				break;
			}
		}
		return bestTerms;
	}


}
