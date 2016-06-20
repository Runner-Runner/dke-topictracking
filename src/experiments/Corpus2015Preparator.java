package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Corpus2015Preparator
{
  public static final String OUTPUT_DIRECTORY = "Year2015";

  public static void main(String[] args)
  {
    prepareFiles("/media/Storage/Meine Daten/Schutzbereich/MoS/Research Project 2/savedata/Corpus 2015/raw");
  }

  private Corpus2015Preparator()
  {

  }

  public static void prepareFiles(String directoryPath)
  {
    File outputDir = new File(OUTPUT_DIRECTORY);
    if (!outputDir.exists())
    {
      outputDir.mkdir();
    }

    File directory = new File(directoryPath);
    File[] monthFiles = directory.listFiles();
    System.out.println("Preparing files of Corpus 2015 ...");
    for (File monthFile : monthFiles)
    {
      if (monthFile.isDirectory())
      {
        String month = monthFile.getName();

        System.out.println("Preparing month: " + month);

        File[] dayFiles = monthFile.listFiles();
        for (File dayFile : dayFiles)
        {
          if (dayFile.isDirectory())
          {
            String day = dayFile.getName();

            System.out.println("Preparing day: " + day);

            String dayName = "2015" + month + day;

            File newDayDirectory = new File(outputDir, dayName);
            if (!newDayDirectory.exists())
            {
              newDayDirectory.mkdir();
            }

            processTextFiles(dayFile, newDayDirectory);
          }
        }
      }
    }
  }

  private static void processTextFiles(File dayFile, File newDayDirectory)
  {
    File[] textFiles = dayFile.listFiles();

    int fileCount = 0;

    for (File textFile : textFiles)
    {
      List<String> textLines = new ArrayList<>();
      try
      {
        BufferedReader reader = new BufferedReader(new FileReader(textFile));
        String line;
        while ((line = reader.readLine()) != null)
        {
          textLines.add(line);
        }
        reader.close();

        //Skip error files
        if (textLines.size() < 2 || textLines.get(1).contains("ERROR"))
        {
          continue;
        }

        //Remove link, date, tags
        textLines.remove(textLines.size() - 1);
        textLines.remove(0);
        textLines.remove(0);
        if (textLines.get(1).contains(" -- "))
        {
          textLines.remove(0);
          textLines.remove(0);
        }

        fileCount++;
        String subfileName = newDayDirectory.getName() + "_" + fileCount + ".txt";
        File newDaySubfile = new File(newDayDirectory, subfileName);
        if (newDaySubfile.exists())
        {
          continue;
        }
        newDaySubfile.createNewFile();
        PrintWriter printWriter = new PrintWriter(newDaySubfile);
        for (String textLine : textLines)
        {
          printWriter.write(textLine);
        }
        printWriter.close();
      }
      catch (IOException ex)
      {

      }

    }
  }
}
