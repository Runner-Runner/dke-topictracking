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
import java.util.Arrays;
import normalization.Normalizer;

public class NMFTopicExtractor
{
  private static final String STOPWORD_PATH = "ressources/stopwords.txt";
  private static final String VOCABULARY_PATH = "ressources/vocabulary.xml";

  public static void main(String[] args)
  {
    runMultipleNMF("/media/Storage/Meine Daten/Schutzbereich/MoS/Research Project 2/savedata/08-29 - 09-04",
            "year", "19960904", 2, 10);
  }

  public static void runNMF(String directory, String outputFileName)
  {
    runMultipleNMF(directory, outputFileName, null, 0, 1);
  }

  /**
   * Executes NMF for arbitrary number of folders combined to arbitrary
   * intervals.
   *
   * @param startDirectory Directory containing the folders with all time spans
   * (e.g. days) which contain the text files.
   * @param outputFileName Output without file ending.
   * @param startFolder Folder inside startDirectory from which to start. Null
   * for "take all directories".
   * @param interval How many time spans (e.g. days) make an interval? (e.g. 7
   * for a week). Set 0 if all files should be executed as one interval.
   * @param intervalCount How many intervals shall be executed? (e.g. 4 weeks)
   */
  public static void runMultipleNMF(String startDirectory, String outputFileName,
          String startFolder, int interval, int intervalCount)
  {
    File dir = new File(startDirectory);
    List<List<File>> intervalFiles = new ArrayList<>();
    List<List<Date>> intervalDates = new ArrayList<>();
    DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    listFiles(dir, intervalFiles, intervalDates, format,
            startFolder, interval, intervalCount);

    for (int i = 0; i < intervalFiles.size(); i++)
    {
      List<File> files = intervalFiles.get(i);
      List<Date> dates = intervalDates.get(i);
      runNMF(files, dates, outputFileName);
    }
  }

  /**
   * Traverse directory recursively and add files to file list.
   *
   * @param startDirectory
   * @param intervalFiles
   * @param intervalDates
   * @param format
   */
  private static void listFiles(File startDirectory, List<List<File>> intervalFiles,
          List<List<Date>> intervalDates, DateFormat format, String startFolderName,
          int interval, int intervalCount)
  {
    //Expected format: directory which contains a directory for every 
    //day/time span, which contains the text files

    if (startDirectory.isDirectory())
    {
      File[] timeSpanDirectories = startDirectory.listFiles();

      if (startFolderName != null)
      {
        List<File> startFolderFollowingDirs = new ArrayList<File>();
        boolean foundStartFolder = false;
        for (int i = 0; i < timeSpanDirectories.length; i++)
        {
          File file = timeSpanDirectories[i];
          if (file.getName().equals(startFolderName))
          {
            foundStartFolder = true;
          }

          if (foundStartFolder)
          {
            startFolderFollowingDirs.add(file);
          }
        }

        if (foundStartFolder == false)
        {
          System.out.println("Start folder not found.");
          return;
        }
        timeSpanDirectories = new File[startFolderFollowingDirs.size()];
        timeSpanDirectories = startFolderFollowingDirs.toArray(timeSpanDirectories);
      }

      if (interval == 0)
      {
        interval = timeSpanDirectories.length;
      }

      int maxIterations = Math.min(intervalCount * interval, timeSpanDirectories.length);

      List<File> files = new ArrayList<>();
      List<Date> dates = new ArrayList<>();

      int timespanCount = 0;
      for (int i = 0; i < maxIterations; i++)
      {
        File timespanDirectory = timeSpanDirectories[i];

        if (timespanDirectory.isDirectory())
        {
          timespanCount++;

          try
          {
            Date date = format.parse(timespanDirectory.getName());
            dates.add(date);
          }
          catch (ParseException ex)
          {
            //Ignore
          }

          files.addAll(Arrays.asList(timespanDirectory.listFiles()));

          if (i == maxIterations - 1
                  || (timespanCount != 0 && timespanCount % interval == 0))
          {
            intervalFiles.add(files);
            intervalDates.add(dates);
            files = new ArrayList<>();
            dates = new ArrayList<>();
          }
        }
      }
    }
  }

  public static void runNMF(List<File> files, List<Date> dates, String outputFileName)
  {
    // read stopwords for normalizer
    System.out.print("read stopwords ");
    HashSet<String> stopwords = Normalizer.readStopwords(STOPWORD_PATH);
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
    vocabulary.removeLowOccurrances(5, 5);
    System.out.print("- done");
    System.out.println("Vocabulary Size: " + vocabulary.size());

    // save vocabulary in xml
    System.out.print("save vocabulary ");
    vocabulary.saveVocabulary(VOCABULARY_PATH);
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
    nmfExecutor.execute(wordCounter.getDocumentTermMatrix());
    System.out.println("run NMF - done");

    // determine first date
    System.out.print("determine timestamp ");
    Date timestamp = null;
    if (dates.size() > 0)
    {
      Collections.sort(dates);
      timestamp = dates.get(0);
    }
    // create TopicData
    System.out.print("extract Topics ");
    TopicTimeStepCollection topicData = new TopicTimeStepCollection(nmfExecutor.getTopicTerm(), nmfExecutor.getTopicDocument(),
            wordCounter.getVocabulary().getVocabulary(), wordCounter.getDocumentNames(), timestamp);
    System.out.println(" - done");
    // save topics
    System.out.print("save extracted topics ");
    outputFileName += " " + timestamp + ".xml";
    TopicTimeStepCollection.saveTopicData(outputFileName, topicData);
    System.out.println(" - done");

  }
}
