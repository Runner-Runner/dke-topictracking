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
    runMultipleNMF("/media/Storage/Meine Daten/Schutzbereich/MoS/Research Project 2/savedata/96 - 97",
            "year", "19970501", 7, 1);
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
   * @param intervalCount How many intervals shall be executed? (e.g. 4 weeks).
   * Set 0 if all files should be executed interval-wise.
   */
  public static void runMultipleNMF(String startDirectory, String outputFileName,
          String startFolder, int interval, int intervalCount)
  {
    System.out.println("Run NMF iterations:");
    long completeStartTime = System.currentTimeMillis();
    
    File dir = new File(startDirectory);
    List<List<File>> intervalFiles = new ArrayList<>();
    List<List<Date>> intervalDates = new ArrayList<>();
    DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    listFiles(dir, intervalFiles, intervalDates, format,
            startFolder, interval, intervalCount);

    Date startDate = intervalDates.get(0).get(0);
    List<Date> lastDates = intervalDates.get(intervalDates.size() - 1);
    Date endDate = lastDates.get(lastDates.size() - 1);
    System.out.println(
            "Starting NMF intervals: From " + startDate + " until " + endDate);
    for (int i = 0; i < intervalFiles.size(); i++)
    {
      System.out.println("Interval #" + (i+1) + "/" + 
              (intervalFiles.size()/interval+1) + " (" + interval + " days):");
      long nmfStartTime = System.currentTimeMillis();
      
      List<File> files = intervalFiles.get(i);
      List<Date> dates = intervalDates.get(i);
      runNMF(files, dates, outputFileName);
      
      long nmfElapsedTime = System.currentTimeMillis() - nmfStartTime;
      //In minutes
      nmfElapsedTime = nmfElapsedTime / 1000 / 60;
      System.out.println("Interval #" + i + " done. Duration: " + nmfElapsedTime + " mins.");
      System.gc();
    }
    
    long completeElapsedTime = System.currentTimeMillis() - completeStartTime;
    //In minutes
    completeElapsedTime = completeElapsedTime / 1000 / 60;
    System.out.println("All NMF iterations done. Duration: " + completeElapsedTime + " mins.");
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
          System.out.println("ERROR: Start folder not found.");
          return;
        }
        timeSpanDirectories = new File[startFolderFollowingDirs.size()];
        timeSpanDirectories = startFolderFollowingDirs.toArray(timeSpanDirectories);
      }

      if (interval == 0)
      {
        interval = timeSpanDirectories.length;
      }

      int maxIterations;

      if (intervalCount == 0)
      {
        maxIterations = timeSpanDirectories.length;
      }
      else
      {
        maxIterations = Math.min(intervalCount * interval, timeSpanDirectories.length);
      }

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
    System.out.print("Read stopwords ");
    HashSet<String> stopwords = Normalizer.readStopwords(STOPWORD_PATH);
    System.out.println("- done");

    // create vocabulary
    Vocabulary vocabulary = new Vocabulary(10000);

    // fill vocabulary
    System.out.print("Generate vocabulary ");
    for (File file : files)
    {
      vocabulary.nextFile(file.getName());
      Normalizer.normalize(file, vocabulary, stopwords);
    }
    vocabulary.removeLowOccurrances(5, 5);
    System.out.println("- done, Vocabulary Size: " + vocabulary.size());

    // save vocabulary in xml
    System.out.print("Save vocabulary ");
    vocabulary.saveVocabulary(VOCABULARY_PATH);
    System.out.println("- done");

    // count words (tfidf)
    WordCounter wordCounter = new WordCounter(vocabulary);
    // read each file separate
    System.out.print("Count document words ");
    for (File file : files)
    {
      wordCounter.nextFile(file.getName());
      Normalizer.normalize(file, wordCounter, stopwords);
    }
    System.out.println("- done");

    // run NMF
    NMFExecutor nmfExecutor = new NMFExecutor();
    System.out.println("Run NMF:");
    nmfExecutor.execute(wordCounter.getDocumentTermMatrix());
    System.out.println("Run NMF - done");

    // determine first date
    System.out.println("Determine timestamp");
    Date timestamp = null;
    if (dates.size() > 0)
    {
      Collections.sort(dates);
      timestamp = dates.get(0);
    }
    // create TopicData
    System.out.print("Extract Topics ");
    TopicTimeStepCollection topicData = new TopicTimeStepCollection(nmfExecutor.getTopicTerm(), nmfExecutor.getTopicDocument(),
            wordCounter.getVocabulary().getVocabulary(), wordCounter.getDocumentNames(), timestamp);
    System.out.println(" - done");
    // save topics
    System.out.print("Save extracted topics ");
    outputFileName += " " + timestamp + ".xml";
    topicData.retranslateStemming();
    TopicTimeStepCollection.save(outputFileName, topicData);
    System.out.println(" - done");
  }
}
