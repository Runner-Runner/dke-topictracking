package nmf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Topic;
import model.TopicRiver;
import model.TopicWave;

public class VisualizationDataConverter
{
  public static final String JSON_FILE_NAME = "topics.js";

  private VisualizationDataConverter()
  {
    //Utility Class
  }

  public static void writeJSONData(TopicRiver topicRiver)
  {
    //TODO dynamic timesteps
    int timeSteps = 52;

    List<TopicWave> waves = topicRiver.getWaves();
    HashMap<TopicWave, List<Double>> yValueMap = new HashMap<>();

    HashMap<TopicWave, String> nameMap = new HashMap<>();

    for (TopicWave wave : waves)
    {
      List<Double> yValues = new ArrayList<>();
      TreeMap<Integer, Topic> topicSequence = wave.getTopicSequence();

      HashMap<String, Double> bestTermMap = new HashMap<>();

      for (int i = 1; i <= timeSteps; i++)
      {
        Topic topic = topicSequence.get(i);
        double relativeRelevance;
        if (topic == null)
        {
          relativeRelevance = 0;
        }
        else
        {
          relativeRelevance = topic.getRelativeRelevance();

          HashMap<String, Double> localBestTermMap = topic.getBestTerms(5);
          for (Entry<String, Double> entry : localBestTermMap.entrySet())
          {
            Double existingValue = bestTermMap.get(entry.getKey());
            Double entryValue = entry.getValue();
            if (existingValue != null)
            {
              entryValue += existingValue;
            }
            bestTermMap.put(entry.getKey(), entryValue);
          }
        }
        yValues.add(relativeRelevance);
      }
      yValueMap.put(wave, yValues);

      nameMap.put(wave, wave.getName());
    }

    String jsonText = "var data = [";

    List<String> waveData = new ArrayList<>();
    for (TopicWave wave : waves)
    {
      String waveText = "{\"name\":\"Topic \\\"" + nameMap.get(wave)
              + "\\\"\",\"data\":[";
      List<Double> yValues = yValueMap.get(wave);
      List<String> timeStepData = new ArrayList<>();
      for (int i = 0; i < yValues.size(); i++)
      {
        String timeStepText = "{\"x\":" + i + ",\"y\":" + yValues.get(i) + "}";
        timeStepData.add(timeStepText);
      }
      waveText += String.join(",", timeStepData);
      waveText += "]}";
      waveData.add(waveText);
    }
    jsonText += String.join(",", waveData);
    jsonText += "];";

    File jsonFile = new File(JSON_FILE_NAME);
    try
    {
      try (PrintWriter jsonWriter = new PrintWriter(jsonFile))
      {
        jsonWriter.print(jsonText);
      }
    }
    catch (FileNotFoundException ex)
    {
      Logger.getLogger(VisualizationDataConverter.class.getName()).log(Level.SEVERE, null, ex);
      System.out.println("Visualization .js file could not be written.");
    }
  }
}
