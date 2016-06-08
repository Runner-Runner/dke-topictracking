package nmf;


import model.TopicTimeStepCollection;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import data.Vocabulary;
import data.WordCounter;
import normalization.Normalizer;

public class NMFTopicExtractor
{
  
  private static final String stopwordpath = "ressources/stopwords.txt";
  private static final String vocabularyPath = "ressources/vocabulary.xml";

  public static void main(String[] args)
  {
		runNMF("ressources/corpus", "weekFour_topics.xml");
  }


  public static void runNMF(String directory, String outputFileName)
  {
    File dir = new File(directory);
    ArrayList<File> files = new ArrayList<>();
    ArrayList<Date> dates = new ArrayList<>();
    DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    listFiles(dir, files, dates, format);
    runNMF(files, dates, outputFileName);
  }

  // traverse directory recursive and add files to filelist
  private static void listFiles(File file, List<File> files, List<Date> dates, DateFormat format)
  {
	try {
		Date date = format.parse(file.getName());
		dates.add(date);
	} catch (ParseException e) {
	}
    if (file.isDirectory())
    {
      for (File f : file.listFiles())
      {
        listFiles(f, files, dates, format);
      }
    }
    else
    {
      files.add(file);
    }
  }

  public static void runNMF(List<File> files, List<Date> dates, String outputFileName)
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

    // determine first date
    System.out.print("determine timestamp ");
    Date timestamp = null;
    if(dates.size()>0){
    	Collections.sort(dates);
    	timestamp = dates.get(0);
    }
    // create TopicData
    System.out.print("extract Topics ");
    TopicTimeStepCollection topicData = new TopicTimeStepCollection(nmfExecutor.getTopicTerm(), nmfExecutor.getTopicDocument(),
            wordCounter.getVocabulary().getVocabulary(), wordCounter.getDocumentNames(),timestamp);
    System.out.println(" - done");
    // save topics
    System.out.print("save extracted topics ");
    TopicTimeStepCollection.saveTopicData(outputFileName, topicData);
    System.out.println(" - done");

  }
}
