package nmf;

import experiments.Utilities;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import la.matrix.Matrix;
import la.matrix.SparseMatrix;
import la.vector.Vector;
import normalization.Normalizer;

public class TopicData {
	private Matrix topicTermMatrix;
	private Matrix topicDocumentMatrix;
	private Set<String> vocabulary;

	private TreeMap<Double, Topic> topics;
	private List<String> documentNames;

	private static final String TOPIC_DOCUMENT = "topicdocument:";
	private static final String TOPIC_TERM = "topicterm:";
	private static final String VOCABULARY = "vocabulary:";
	private static final String DOCUMENTNAMES = "documentnames:";
	private static final String STEMMING_ORIGINAL_MAPPING = "stemmingOriginalMapping:";

	private static final String OUTPUT_FILE_NAME = "ressources/topicdata.txt";

	public TopicData(Matrix topicTermMatrix, Matrix topicDocumentMatrix, Set<String> vocabulary,
			List<String> documentNames) {
		this.topicTermMatrix = topicTermMatrix;
		this.topicDocumentMatrix = topicDocumentMatrix;
		this.vocabulary = vocabulary;
		this.documentNames = documentNames;

		loadTopics();
	}

	public List<Topic> getTopics() {
		List<Topic> topicList = new ArrayList<>();
		topicList.addAll(topics.values());
		return topicList;
	}

	public void saveTopics(String filename) {
		XMLEncoder encoder = null;
		try {
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
		} catch (FileNotFoundException fileNotFound) {
			System.out.println("ERROR: While Creating or Opening the File " + filename);
		}
		encoder.writeObject(topics);
		encoder.close();
	}

	@SuppressWarnings("unchecked")
	public TreeMap<Double, Topic> loadTopics(String filename) {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File " + filename + " not found");
		}
		return (TreeMap<Double, Topic>) decoder.readObject();
	}

	private void loadTopics() {
		topics = new TreeMap<>();

		int topicCount = topicTermMatrix.getRowDimension();
		int termCount = topicTermMatrix.getColumnDimension();

		for (int i = 0; i < topicCount; i++) {
			Topic topic = new Topic();

			Iterator<String> iterator = vocabulary.iterator();
			for (int j = 0; j < termCount; j++) {
				String term = iterator.next();
				double termValue = topicTermMatrix.getEntry(i, j);
				topic.addTerm(term, termValue);
			}

			Vector columnVector = topicDocumentMatrix.getColumnVector(i);
			double tfidfSum = 0;

			TreeMap<Double, String> documentRankings = new TreeMap<>();

			for (int j = 0; j < columnVector.getDim(); j++) {
				double tfidf = columnVector.get(j);
				tfidfSum += tfidf;
				documentRankings.put(tfidf, documentNames.get(j));
			}
			topic.setDocumentRanking(documentRankings);
			topics.put(tfidfSum, topic);
		}
	}

	public File writeToFile() {
		File outputFile = Utilities.getNextUnusedFile(new File(OUTPUT_FILE_NAME));
		try (PrintWriter writer = new PrintWriter(outputFile)) {
			writer.println(TOPIC_TERM);
			for (int i = 0; i < topicTermMatrix.getColumnDimension(); i++) {
				for (int j = 0; j < topicTermMatrix.getRowDimension(); j++) {
					writer.print(topicTermMatrix.getEntry(j, i) + ";");
				}
				writer.println();
			}

			writer.println();
			writer.println(TOPIC_DOCUMENT);
			for (int i = 0; i < topicDocumentMatrix.getColumnDimension(); i++) {
				for (int j = 0; j < topicDocumentMatrix.getRowDimension(); j++) {
					writer.print(topicDocumentMatrix.getEntry(j, i) + ";");
				}
				writer.println();
			}

			writer.println();
			writer.println(VOCABULARY);
			writer.println(vocabulary);

			writer.println();
			writer.println(DOCUMENTNAMES);
			writer.println(documentNames);

			writer.println();
			writer.println(STEMMING_ORIGINAL_MAPPING);
			writer.println(Normalizer.getStemmingOriginalMapping());
		} catch (FileNotFoundException ex) {
			Logger.getLogger(TopicData.class.getName()).log(Level.SEVERE, null, ex);
		}
		return outputFile;
	}

	public static TopicData loadFromFile(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			List<Double> topicTermList = new ArrayList<>();
			int width = 0;
			int height = 0;
			while ((line = reader.readLine()) != null) {
				if (line.contains(TOPIC_TERM)) {
					continue;
				} else if (line.contains(TOPIC_DOCUMENT)) {
					break;
				}
				if (line.trim().isEmpty()) {
					continue;
				}

				height++;

				String[] texts = line.split(";");
				width = texts.length;
				for (String text : texts) {
					double value = Double.parseDouble(text);
					topicTermList.add(value);
				}
			}

			Matrix topicTermMatrix = new SparseMatrix(width, height);
			int listIndex = -1;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					listIndex++;
					topicTermMatrix.setEntry(i, j, topicTermList.get(listIndex));
				}
			}

			List<Double> topicDocumentList = new ArrayList<>();
			width = height = 0;
			while ((line = reader.readLine()) != null) {
				if (line.contains(VOCABULARY)) {
					break;
				}
				if (line.trim().isEmpty()) {
					continue;
				}

				height++;

				String[] texts = line.split(";");
				width = texts.length;
				for (String text : texts) {
					double value = Double.parseDouble(text);
					topicDocumentList.add(value);
				}
			}

			Matrix topicDocumentMatrix = new SparseMatrix(width, height);
			listIndex = -1;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					listIndex++;
					topicDocumentMatrix.setEntry(i, j, topicTermList.get(listIndex));
				}
			}

			Set<String> vocabulary = new HashSet<>();
			while ((line = reader.readLine()) != null) {
				if (line.contains(DOCUMENTNAMES)) {
					break;
				}
				if (line.trim().isEmpty()) {
					continue;
				}

				line = line.replace("[", "");
				line = line.replace("]", "");
				String[] texts = line.split("\\s*\\,\\s*");
				vocabulary.addAll(Arrays.asList(texts));
			}

			List<String> documentNames = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				if (line.contains(STEMMING_ORIGINAL_MAPPING)) {
					break;
				}
				if (line.trim().isEmpty()) {
					continue;
				}

				line = line.replace("[", "");
				line = line.replace("]", "");
				String[] texts = line.split("\\s*\\,\\s*");
				documentNames.addAll(Arrays.asList(texts));
			}

			HashMap<String, String> stemmingOriginalMapping = new HashMap<String, String>();
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				line = line.replace("{", "");
				line = line.replace("}", "");
				String[] texts = line.split("\\s\\,\\s");
				for (String text : texts) {
					String[] split = text.split("=");
					if (text.length() == 2) {
						stemmingOriginalMapping.put(split[0], split[1]);
					}
				}
			}

			TopicData topicData = new TopicData(topicTermMatrix, topicDocumentMatrix, vocabulary, documentNames);
			Normalizer.getStemmingOriginalMapping().putAll(stemmingOriginalMapping);

			return topicData;
		} catch (IOException ex) {
			Logger.getLogger(TopicData.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
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
