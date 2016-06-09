package nmf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    int timeSteps = 4;

    List<TopicWave> waves = topicRiver.getWaves();
    HashMap<TopicWave, List<Double>> yValueMap = new HashMap<>();

    HashMap<TopicWave, String> nameMap = new HashMap<>();

    for (TopicWave wave : waves)
    {
//      List<Double> yValues = new ArrayList<>();
//      TreeMap<Integer, Topic> topicSequence = wave.getTopicSequence();

//      HashMap<String, Double> bestTermMap = new HashMap<>();
//
//      for (int i = 1; i <= timeSteps; i++)
//      {
//        Topic topic = topicSequence.get(i);
//        double relativeRelevance;
//        if (topic == null)
//        {
//          relativeRelevance = 0;
//        }
//        else
//        {
//          relativeRelevance = topic.getRelativeRelevance(topicRiver.
//                  getTfidfTotal(i));
//
//          HashMap<String, Double> localBestTermMap = topic.getBestTerms(5);
//          for (Entry<String, Double> entry : localBestTermMap.entrySet())
//          {
//            Double existingValue = bestTermMap.get(entry.getKey());
//            Double entryValue = entry.getValue();
//            if (existingValue != null)
//            {
//              entryValue += existingValue;
//            }
//            bestTermMap.put(entry.getKey(), entryValue);
//          }
//        }
//        yValues.add(relativeRelevance);
//      }
//      yValueMap.put(wave, yValues);
//
//      List<Map.Entry<String, Double>> entryList = new LinkedList<>(
//              bestTermMap.entrySet());
//      Collections.sort(entryList, new Comparator<Entry<String, Double>>()
//      {
//        @Override
//        public int compare(Entry<String, Double> o1, Entry<String, Double> o2)
//        {
//          return (int) Math.signum(o2.getValue() - o1.getValue());
//        }
//      });
//      int max = Math.min(entryList.size(), 5);
//      List<String> bestTerms = new ArrayList<String>();
//      for (int i = 0; i < max; i++)
//      {
//        String term = entryList.get(i).getKey();
//        bestTerms.add(term);
//      }
      int termCount = 0;
      List<String> bestTerms = new ArrayList<String>();
      for(Entry<Double,String> terms: wave.getAverageTFIDF().entrySet()){
    	  bestTerms.add(terms.getValue());
    	  termCount++;
    	  if(termCount==5)
    		  break;
      }
      String name = String.join(", ", bestTerms);
      nameMap.put(wave, name);
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
