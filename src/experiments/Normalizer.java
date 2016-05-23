package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import nmf.NMFExecutor;

public class Normalizer
{
  private static HashMap<String, String> stemmingOriginalMapping;

  static
  {
    stemmingOriginalMapping = new HashMap<>();
  }
  
  public static void main(String[] args)
  {
    String[] directoryPaths =
    {
      "ressources/19960829_small",
//      "ressources/19960830",
//      "ressources/19960831",
//      "ressources/19960901",
    };

    for(String dirPath : directoryPaths)
    {
      Normalizer normalizer = new Normalizer();
      normalizer.start(dirPath);
    }
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
    start(files, inpaths[0]);
  }

  public void start(String directoryPath)
  {
    File dirFile = new File(directoryPath);
    start(dirFile.listFiles(), directoryPath);
  }

  public void start(File[] files, String outputFileName)
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
                s.stem();
                if (!stopwords.contains(original))
                {
                  wordHandler.addWord(s.toString());
                  stemmingOriginalMapping.put(s.toString(), original);
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
    if(original == null)
    {
      original = stemmedTerm;
    }
    return original;
  }
}
