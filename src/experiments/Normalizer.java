package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import nmf.NMFExecutor;
import nmf.TopicData;
import wordnet.TopicMatcher;

public class Normalizer
{
  private static HashMap<String, String> stemmingOriginalMapping;
  static
  {
    stemmingOriginalMapping = new HashMap<>();
  }
  public Normalizer() {
  }

  public static void main(String[] args)
  {
//    testWithTopicFiles();
    
//    String[] directoryPaths =
//    {
//      "ressources/month",
////      "ressources/19960829",
////      "ressources/19960830",
////      "ressources/19960831",
////      "ressources/19960901",
//    };
//
//    for (String dirPath : directoryPaths)
//    {
//      Normalizer normalizer = new Normalizer();
//      normalizer.start(dirPath);
//    }
	    String stopwordpath = "ressources/stopwords.txt";
	    String vocabularyPath = "ressources/vocabulary.txt";
	    String wordVectorPath = "ressources/wordVectors.txt";

	    HashSet<String> stopwords = readStopwords(stopwordpath);

	    // create vocabulary
	    Vocabulary vocabulary = new Vocabulary(1000);
	    // add textfile to vocabulary -> usually more than one
//			normalize(inpath, vocabulary, stopwords);
	    File dir = new File("ressources/corpus/19960829");
	    Normalizer n = new Normalizer();
	    for (File file : dir.listFiles())
	    {
	      n.normalize(file, vocabulary, stopwords);
	    }
  }

  public static void testWithTopicFiles()
  {
    File file1 = new File("ressources/topicdata.txt");
    File file2 = new File("ressources/topicdata_2.txt");
    
    TopicData topicData1 = TopicData.loadFromFile(file1);
    TopicData topicData2 = TopicData.loadFromFile(file2);
    
    TopicMatcher topicMatcher = new TopicMatcher();
    topicMatcher.compareTopicData(topicData1, topicData2);
    
    int a = 3;
    
  }
  
  public void start(String... inpaths)
  {
    
    if (inpaths.length == 0)
    {
      return;
    }

    File[] files = new File[inpaths.length];
    for (int i = 0; i < files.length; i++)
    {
      files[i] = new File(inpaths[i]);
    }
    start(Arrays.asList(files), inpaths[0]);
  }

  public void start(String directoryPath)
  {
    File dirFile = new File(directoryPath);
    File[] listFiles = dirFile.listFiles();
    List<File> upperFileList = new ArrayList<>();
    upperFileList.addAll(Arrays.asList(listFiles));
    
    List<File> textFiles = new ArrayList<>();
    for(File file : upperFileList)
    {
      if(file.isDirectory())
      {
        textFiles.addAll(Arrays.asList(file.listFiles()));
      }
      else
      {
        textFiles.add(file);
      }
    }
    
    start(textFiles, directoryPath);
  }

  public void start(List<File> files, String outputFileName)
  {
    String stopwordpath = "ressources/stopwords.txt";
    String vocabularyPath = "ressources/vocabulary.txt";
    String wordVectorPath = "ressources/wordVectors.txt";

    HashSet<String> stopwords = readStopwords(stopwordpath);

    // create vocabulary
    Vocabulary vocabulary = new Vocabulary(1000);
    // add textfile to vocabulary -> usually more than one
//		normalize(inpath, vocabulary, stopwords);
    for (File file : files)
    {
      normalize(file, vocabulary, stopwords);
    }
    // write vocabulary to file
    vocabulary.saveVocabulary(vocabularyPath);

    // count words
    WordCounter wordCounter = new WordCounter(vocabularyPath);
    // read each file separate

    for (File file : files)
    {
      wordCounter.startNewDocument(file.getName());
      normalize(file, wordCounter, stopwords);
    }

    NMFExecutor nmfExecutor = new NMFExecutor(wordCounter, 50, outputFileName);
    nmfExecutor.execute();

    // save counted words
    wordCounter.saveDocuments(wordVectorPath);
  }

  public static HashSet<String> readStopwords(String path)
  {
    HashSet<String> stopwords = new HashSet<String>();
    BufferedReader br = null;
    try
    {
      br = new BufferedReader(new FileReader(path));
      String line = br.readLine();
      while (line != null)
      {
        stopwords.add(line.trim());
        line = br.readLine();
      }
      br.close();
    }
    catch (Exception e)
    {
      System.out.println("Stopwords could not be read.");
    }
    return stopwords;
  }

  public void normalize(File inputFile, WordHandler wordHandler, HashSet<String> stopwords)
  {
    char[] w = new char[501]; // word buffer
    Stemmer s = new Stemmer();
    FileInputStream in = null;
    try
    {
      // input file
      in = new FileInputStream(inputFile);

      // traverse file bytewise
      while (true)
      {
        int ch = in.read();
        if (Character.isLetter((char) ch))
        {
          int j = 0;
          // traverse current word
          while (true)
          {
            ch = Character.toLowerCase((char) ch);
            w[j] = (char) ch;
            if (j < 500)
            {
              j++;
            }
            ch = in.read();
            // if not a character, process buffer data
            if (!Character.isLetter((char) ch))
            {
              // one letter is not a word, might caused by 's
              if (j > 1)
              {
                // add chars to stemmer
                for (int c = 0; c < j; c++)
                {
                  s.add(w[c]);
                }
                // check if its a stopword
                String original = s.getOriginal();
                String americanized = Americanizer.americanize(original).toLowerCase();
                s.stem();
                if(!original.equals(americanized)){
                	System.out.println("changed "+original+" to "+americanized);
                	for(char signleChar:americanized.toCharArray())
                		s.add(signleChar);
                	s.stem();
                	original = americanized;
                }
                if (!stopwords.contains(original))
                {
                  String stemmString = s.toString();
                  wordHandler.addWord(stemmString);
                  if (!stemmString.equals(original))
                  {
                    stemmingOriginalMapping.put(stemmString, original);
                  }
                }

              }
              // word processed -> go on
              break;
            }
          }
        }
        if (ch < 0)
        {
          break;
        }
      }
      in.close();
    }
    catch (FileNotFoundException e)
    {
    }
    catch (IOException e)
    {
    }
  }

  public static String getOriginal(String stemmedTerm)
  {
    String original = stemmingOriginalMapping.get(stemmedTerm);
    if (original == null)
    {
      original = stemmedTerm;
    }
    return original;
  }

  public static HashMap<String, String> getStemmingOriginalMapping()
  {
    return stemmingOriginalMapping;
  }
}
