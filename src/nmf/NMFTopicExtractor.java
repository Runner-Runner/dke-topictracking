package nmf;

import model.TopicTimeStepCollection;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


import data.Vocabulary;
import data.WordCounter;
import java.util.Calendar;

import normalization.Normalizer;

public class NMFTopicExtractor
{
  private static final String STOPWORD_PATH = "ressources/stopwords.txt";
  private static final String VOCABULARY_PATH = "ressources/vocabulary.xml";

  
  public static void main(String[] args)
  {
	if(args.length == 0){
		TreeMap<Date, List<Document>> files = new TreeMap<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			listFiles(new File("/mnt/dualuse/sharedData/Git-Projects/dke-topictracking/Reuters Corpus Volume 1 (RCV1) - Disk 1 of 2/unzipped/extractions/"), files, new SimpleDateFormat("yyyyMMdd"), df.parse("19970103"), 1, 1);
			runMultipleNMF(files, 7, df, new File("days"));
		} catch (ParseException e) {
		}
	}
	else if(args.length != 6){
		printUsage();
		return ;
	}
	else{
	DateFormat format = new SimpleDateFormat(args[2], Locale.ENGLISH);
	int interval=0;
	int sequence=0;
	Date startDate;
	try{
		interval = Integer.parseInt(args[0]);
		sequence = Integer.parseInt(args[1]);
		startDate = format.parse(args[3]);
	}catch(NumberFormatException | ParseException ex){
		printUsage();
		return;
	}
	File inputDirectory = new File(args[4]);
	File outputDirectory = new File(args[5]);
	if(!inputDirectory.isDirectory() || !outputDirectory.isDirectory()){
		System.out.println("given path is not a directory");
		printUsage();
		return;
	}
	TreeMap<Date, List<Document>> files = new TreeMap<>();
	listFiles(inputDirectory, files, format, startDate, interval, sequence);
	runMultipleNMF(files, interval, format, outputDirectory);
	}
  
  }
  
  private static void printUsage(){
	  System.out.println("NMFTopicExtractor:");
	  System.out.println("java -jar NMFTopicExtractor <interval> <sequence> <dateformat> <startdate> <input-directory> <output-directory>");
	  System.out.println("interval (int) - defines the day range of each interval");
	  System.out.println("sequence (int) - defines number of intervals, which will be extracted");
	  System.out.println("dateformat (String) - defines the dateformat for documents directories e.g. yyyyMMdd");
	  System.out.println("startdate (String) define the startdate (in given dateformat)");
	  System.out.println("input-directory (Path) parent directory, which contains all documents");
	  System.out.println("output-directory (Path) directory to store extracted xml files");
  }



  public static void runMultipleNMF(TreeMap<Date, List<Document>> documents, int interval, DateFormat format, File outputDirectory)
  {
    System.out.println("Run NMF iterations:");
    long completeStartTime = System.currentTimeMillis();

    for (Entry<Date,List<Document>> entry: documents.entrySet())
    {
      System.out.println("Interval from " + (entry.getKey().toString()) + " (" + 
              (interval) + " days)");
      long nmfStartTime = System.currentTimeMillis();
      
      runNMF(entry.getKey(),entry.getValue(),outputDirectory,format, interval);
      
      long nmfElapsedTime = System.currentTimeMillis() - nmfStartTime;
      //In minutes
      nmfElapsedTime = nmfElapsedTime / 1000 / 60;
      System.out.print("Interval from " + (entry.getKey().toString()) + " (" + 
              (interval) + " days)");
      System.out.println("- done. Duration: " + nmfElapsedTime + " mins.");
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
  private static void listFiles(File startDirectory, Map<Date,List<Document>> intervalFiles,
         DateFormat format, Date startDate,
          int interval, int intervalCount)
  {
	  if(startDirectory.isDirectory()){
		  try {
			Date date = format.parse(startDirectory.getName());
			if(date.before(startDate))
				return;
			long timeDiff = date.getTime() - startDate.getTime();
		    int dayDiff = (int) TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
		    if(dayDiff < interval*intervalCount){
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				c.add(Calendar.DATE, (dayDiff/interval)*interval);
				List<Document> documents = intervalFiles.get(c.getTime());
				if(documents == null)
					documents = new ArrayList<>();
				for(File file:startDirectory.listFiles()){
					if(!file.isDirectory()){
						Document document = new Document();
						document.setDate(date);
						document.setTitle(file.getName());
						document.setPath(file.getPath());
						documents.add(document);
					}
				}
				intervalFiles.put(c.getTime(), documents);
		    }
		} catch (ParseException e) {
			for(File file:startDirectory.listFiles()){
				if(file.isDirectory()){
					listFiles(file, intervalFiles, format, startDate, interval, intervalCount);
				}
			}
		}
	  }
	
 
  }

  public static void runNMF(Date timestamp, List<Document> documents, File outputDirectory, DateFormat format, int interval)
  {
    // read stopwords for normalizer
    System.out.print("Read stopwords ");
    HashSet<String> stopwords = Normalizer.readStopwords(STOPWORD_PATH);
    System.out.println("- done");

    // create vocabulary
    Vocabulary vocabulary = new Vocabulary(10000);

    // fill vocabulary
    System.out.print("Generate vocabulary ");
    for (Document document : documents)
    {
      vocabulary.nextDocument(document);
      Normalizer.normalize(new File(document.getPath()), vocabulary, stopwords);
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
    for (Document document: documents)
    {
      wordCounter.nextDocument(document);
      Normalizer.normalize(new File(document.getPath()), wordCounter, stopwords);
    }
    System.out.println("- done");

    // run NMF
    NMFExecutor nmfExecutor = new NMFExecutor();
    System.out.println("Run NMF:");
    nmfExecutor.execute(wordCounter.getDocumentTermMatrix());
    System.out.println("Run NMF - done");

    // create TopicData
    System.out.print("Extract Topics ");
    TopicTimeStepCollection ttsc = new TopicTimeStepCollection();
    ttsc.extractTopicsFromMatrices(nmfExecutor.getTopicTerm(), nmfExecutor.getTopicDocument(), wordCounter.getVocabulary().getVocabulary(), wordCounter.getDocumentNames(), interval, timestamp);
    System.out.println(" - done");
    // save topics
    String outputFileName = outputDirectory.getAbsolutePath()+File.separator+format.format(timestamp) + ".xml";
    System.out.print("Save extracted topics "+outputFileName);
    ttsc.retranslateStemming();
    TopicTimeStepCollection.save(outputFileName, ttsc);
    System.out.println(" - done");
  }
}
