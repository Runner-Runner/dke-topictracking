package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import nmf.NMFExecutor;

public class Normalizer
{

  public static void main(String[] args)
  {
    Normalizer normalizer = new Normalizer();
//    normalizer.start("ressources/art1.txt", "ressources/art2.txt");
    normalizer.start("ressources/19960829_small");
  }

  public void start(String... inpaths)
  {
    File[] files = new File[inpaths.length];
    for (int i = 0; i < files.length; i++)
    {
      files[i] = new File(inpaths[i]);
    }
    start(files);
  }

  public void start(String directoryPath)
  {
    File dirFile = new File(directoryPath);
    start(dirFile.listFiles());
  }

  public void start(File[] files)
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

    NMFExecutor nmfExecutor = new NMFExecutor(wordCounter, 50);
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

  public static void normalize(File inputFile, WordHandler wordHandler, HashSet<String> stopwords)
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

}
