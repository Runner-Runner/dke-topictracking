package model;

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

//TODO Merge within same timestep
public class TopicTimeStepCollection
{
  private Matrix topicTermMatrix;
  private Matrix topicDocumentMatrix;
  private Set<String> vocabulary;

  private TreeMap<Double, Topic> topics;
  private List<String> documentNames;

  private double absoluteValuesTotal;

  private static final String TOPIC_DOCUMENT = "topicdocument:";
  private static final String TOPIC_TERM = "topicterm:";
  private static final String VOCABULARY = "vocabulary:";
  private static final String DOCUMENTNAMES = "documentnames:";
  private static final String STEMMING_ORIGINAL_MAPPING = "stemmingOriginalMapping:";

  private static final String OUTPUT_FILE_NAME = "ressources/topicdata.txt";

  private static final long serialVersionUID = 6420397376392250857L;

  public TopicTimeStepCollection()
  {
    //for serializing
  }

  public TopicTimeStepCollection(Matrix topicTermMatrix, Matrix topicDocumentMatrix, Set<String> vocabulary,
          List<String> documentNames)
  {
    this.topicTermMatrix = topicTermMatrix;
    this.topicDocumentMatrix = topicDocumentMatrix;
    this.vocabulary = vocabulary;
    this.documentNames = documentNames;

    extractTopicsFromMatrices();
  }

  public TreeMap<Double, Topic> getTopics()
  {
    return topics;
  }
  
  public List<Topic> getTopicList()
  {
    List<Topic> topicList = new ArrayList<>();
    topicList.addAll(topics.values());
    return topicList;
  }

  public void setTopics(TreeMap<Double, Topic> topics)
  {
    this.topics = topics;
  }

  public static void saveTopicData(String filename, TopicTimeStepCollection topicData)
  {
    XMLEncoder encoder = null;
    try
    {
      encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
    }
    catch (FileNotFoundException fileNotFound)
    {
      System.out.println("ERROR: While Creating or Opening the File " + filename);
    }
    encoder.writeObject(topicData);
    encoder.close();
  }
  
  public static TopicTimeStepCollection loadTopicData(String filename)
  {
    XMLDecoder decoder = null;
    try
    {
      decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
    }
    catch (FileNotFoundException e)
    {
      System.out.println("ERROR: File " + filename + " not found");
    }
    return (TopicTimeStepCollection) decoder.readObject();
  }
  
  private void extractTopicsFromMatrices()
  {
    topics = new TreeMap<>();

    int topicCount = topicTermMatrix.getRowDimension();
    int termCount = topicTermMatrix.getColumnDimension();

    for (int i = 0; i < topicCount; i++)
    {
      Topic topic = new Topic();

      Iterator<String> iterator = vocabulary.iterator();
      for (int j = 0; j < termCount; j++)
      {
        String term = iterator.next();
        double termValue = topicTermMatrix.getEntry(i, j);
        topic.addTerm(term, termValue);
      }

      Vector columnVector = topicDocumentMatrix.getColumnVector(i);
      double tfidfSum = 0;

      TreeMap<Double, String> documentRankings = new TreeMap<>();

      for (int j = 0; j < columnVector.getDim(); j++)
      {
        double tfidf = columnVector.get(j);
        tfidfSum += tfidf;
        documentRankings.put(tfidf, documentNames.get(j));
      }
      topic.setDocumentRankings(documentRankings);
      topic.setAbsoluteRelevance(tfidfSum);
      absoluteValuesTotal += tfidfSum;
      topics.put(tfidfSum, topic);
    }
  }

  public double getAbsoluteValuesTotal()
  {
    return absoluteValuesTotal;
  }

  public void setAbsoluteValuesTotal(double absoluteValuesTotal)
  {
    this.absoluteValuesTotal = absoluteValuesTotal;
  }

  @Override
  public String toString()
  {
    Iterator<Map.Entry<Double, Topic>> topicIterator = topics.descendingMap().entrySet().iterator();
    int index = 0;
    String text = "";
    while (topicIterator.hasNext())
    {
      index++;
      Map.Entry<Double, Topic> topicEntry = topicIterator.next();
      Double cumulatedTfidf = topicEntry.getKey();
      Topic topic = topicEntry.getValue();
      text += "\nTopic #" + index + " (" + cumulatedTfidf + "):" + topic;
    }
    return text;
  }
}
