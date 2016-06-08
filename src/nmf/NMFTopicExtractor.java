package nmf;


import model.TopicTimeStepCollection;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import data.Vocabulary;
import data.WordCounter;
import normalization.Normalizer;

public class NMFTopicExtractor
{
  
  private static final String stopwordpath = "ressources/stopwords.txt";
  private static final String vocabularyPath = "ressources/vocabulary.xml";

  public static void main(String[] args)
  {
		runNMF("ressources/weekFour", "weekFour_topics.xml");
  }


  public static void runNMF(String directory, String outputFileName)
  {
    File dir = new File(directory);
    ArrayList<File> files = new ArrayList<>();
    listFiles(dir, files);
    runNMF(files, outputFileName);
  }

  // traverse directory recursive and add files to filelist
  private static void listFiles(File file, List<File> files)
  {
    if (file.isDirectory())
    {
      for (File f : file.listFiles())
      {
        listFiles(f, files);
      }
    }
    else
    {
      files.add(file);
    }
  }

  public static void runNMF(List<File> files, String outputFileName)
  {
    // read stopwords for normalizer
    System.out.print("read stopwords ");
    HashSet<String> stopwords = Normalizer.readStopwords(stopwordpath);
    System.out.println("- done");

    // create vocabulary
    Vocabulary vocabulary = new Vocabulary(10000);

    // fill vocabulary
    System.out.print("generate vocabulary ");
    for (File file : files)
    {
      vocabulary.nextFile(file.getName());
      Normalizer.normalize(file, vocabulary, stopwords);
    }
    vocabulary.removeLowOccurrances(2, 2);
    System.out.println("- done");

    // save vocabulary in xml
    System.out.print("save vocabulary ");
    vocabulary.saveVocabulary(vocabularyPath);
    System.out.println("- done");

    // count words (tfidf)
    WordCounter wordCounter = new WordCounter(vocabulary);
    // read each file separate
    System.out.print("count document words ");
    for (File file : files)
    {
      wordCounter.nextFile(file.getName());
      Normalizer.normalize(file, wordCounter, stopwords);
    }
    System.out.println("- done");

    // run NMF
    NMFExecutor nmfExecutor = new NMFExecutor();
    System.out.println("run NMF ");
    nmfExecutor.execute(wordCounter.getDocumentTermMatrix(), 50);
    System.out.println("run NMF - done");

    // create TopicData
    System.out.print("extract Topics ");
    TopicTimeStepCollection topicData = new TopicTimeStepCollection(nmfExecutor.getTopicTerm(), nmfExecutor.getTopicDocument(),
            wordCounter.getVocabulary().getVocabulary(), wordCounter.getDocumentNames());
    System.out.println(" - done");
    // save topics
    System.out.print("save extracted topics ");
    TopicTimeStepCollection.saveTopicData(outputFileName, topicData);
    System.out.println(" - done");

  }
}
