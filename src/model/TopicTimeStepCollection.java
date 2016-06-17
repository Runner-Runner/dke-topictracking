package model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import la.matrix.Matrix;
import la.vector.Vector;
import nmf.Document;
import normalization.Normalizer;

//TODO Merge within same timestep
public class TopicTimeStepCollection implements Serializable {
	private static final long serialVersionUID = 6420397376392250857L;
	private TreeMap<Double, Topic> topics;
	private Date timestamp;
	private int interval;
	private double absoluteValuesTotal;
	
	public TopicTimeStepCollection() {
		// for serializing
	}
	
	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public TreeMap<Double, Topic> getTopics() {
		return topics;
	}

	public List<Topic> getTopicList() {
		List<Topic> topicList = new ArrayList<>();
		topicList.addAll(topics.values());
		return topicList;
	}

	public void setTopics(TreeMap<Double, Topic> topics) {
		this.topics = topics;
	}

	public static void save(String filename, TopicTimeStepCollection topicData) {
		XMLEncoder encoder = null;
		try {
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
		} catch (FileNotFoundException fileNotFound) {
			System.out.println("ERROR: While Creating or Opening the File " + filename);
		}
		encoder.writeObject(topicData);
		encoder.close();
	}

	public static TopicTimeStepCollection load(String filename) {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File " + filename + " not found");
		}
		return (TopicTimeStepCollection) decoder.readObject();
	}

	public void extractTopicsFromMatrices(Matrix topicTermMatrix, Matrix topicDocumentMatrix, Set<String> vocabulary,
			List<Document> documents, int interval, Date timestamp) {
		topics = new TreeMap<>();

		this.timestamp = timestamp;
		this.interval = interval;
		int topicCount = topicTermMatrix.getRowDimension();
		int termCount = topicTermMatrix.getColumnDimension();

		for (int i = 0; i < topicCount; i++) {
			Topic topic = new Topic();
			topic.setInterval(interval);
			topic.setTimeStamp(timestamp);

			Iterator<String> iterator = vocabulary.iterator();
			for (int j = 0; j < termCount; j++) {
				String term = iterator.next();
				double termValue = topicTermMatrix.getEntry(i, j);
				topic.addTerm(term, termValue);
			}

			Vector columnVector = topicDocumentMatrix.getColumnVector(i);
			double tfidfSum = 0;

			TreeMap<Double, Document> documentRankings = new TreeMap<>();

			for (int j = 0; j < columnVector.getDim(); j++) {
				double tfidf = columnVector.get(j);
				tfidfSum += tfidf;
				documentRankings.put(tfidf, documents.get(j));
			}
			topic.setDocumentRankings(documentRankings);
			topic.setAbsoluteRelevance(tfidfSum);
			absoluteValuesTotal += tfidfSum;
			topics.put(tfidfSum, topic);
		}
		// set relative values
		for (Topic topic : topics.values()) {
			topic.setRelativeRelevance(topic.getAbsoluteRelevance() / absoluteValuesTotal);
		}
	}

	public double getAbsoluteValuesTotal() {
		return absoluteValuesTotal;
	}

	public void setAbsoluteValuesTotal(double absoluteValuesTotal) {
		this.absoluteValuesTotal = absoluteValuesTotal;
	}

	public void retranslateStemming() {
		for (Topic topic : topics.values()) {
			TreeMap<Double, String> terms = new TreeMap<>();
			for (Entry<Double, String> entry : topic.getTerms().entrySet()) {
				terms.put(entry.getKey(), Normalizer.getOriginal(entry.getValue()));
			}
			topic.setTerms(terms);
		}
	}

	@Override
	public String toString() {
		Iterator<Map.Entry<Double, Topic>> topicIterator = topics.descendingMap().entrySet().iterator();
		int index = 0;
		String text = "";
		while (topicIterator.hasNext()) {
			index++;
			Map.Entry<Double, Topic> topicEntry = topicIterator.next();
			Double cumulatedTfidf = topicEntry.getKey();
			Topic topic = topicEntry.getValue();
			text += "\nTopic #" + index + " (" + cumulatedTfidf + "):" + topic;
		}
		return text;
	}
}
