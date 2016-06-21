package nmf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Topic;
import model.TopicRiver;
import model.TopicWave;

public class VisualizationDataConverter
{
  public static final String JSON_FILE_NAME = "topics.js";
  public static final int EXPORT_DOCUMENT_COUNT = 15;

  private VisualizationDataConverter()
  {
    //Utility Class
  }
  public static void writeCSVData(TopicRiver topicRiver, String outputFile){
	    File topics = new File(outputFile+"-topics.csv");
	    try {
			PrintWriter topicWriter = new PrintWriter(topics);
			//write topic file
			List<TopicWave> waves = topicRiver.getWaves();
			for(TopicWave wave:waves){
				topicWriter.println(wave.getName("; "));
			}
			topicWriter.close();
			
			//write mapping file
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			File topicMapping = new File(outputFile+"-mapping.dat");
			PrintWriter mappingWriter = new PrintWriter(topicMapping);
			Calendar cal = Calendar.getInstance();
	        cal.setTime(topicRiver.getStartDate());
	        Date current = cal.getTime();
	        mappingWriter.println(dateFormat.format(current));
	        while(current.before(topicRiver.getEndDate())){
	        	for(TopicWave wave:waves){
	        		Topic t = wave.getTopic(current);
	        		if(t!=null){
	        			mappingWriter.print(t.getRelativeRelevance()+":");
	        			mappingWriter.print(String.join(", ", t.getRankedDocuments(current, EXPORT_DOCUMENT_COUNT)));
	        		}
	        		else{
	        			mappingWriter.print("0:");
	        		}
	        		mappingWriter.print(" ;");
	        	}
	        	mappingWriter.println();
	        	cal.add(Calendar.DATE, 1);
	        	current = cal.getTime();
	        }
	        mappingWriter.close();
		} catch (FileNotFoundException e) {
			
		}
	    
  }
  
  public static void writeJSONData(TopicRiver topicRiver)
  {
    List<TopicWave> waves = topicRiver.getWaves();

    TreeSet<Date> uniqueTimeSteps = new TreeSet<>();
    for (TopicWave wave : waves)
    {
      Set<Date> keySet = wave.getTopicSequence().keySet();
      uniqueTimeSteps.addAll(keySet);
    }

    HashMap<TopicWave, List<Double>> yValueMap = new HashMap<>();
    HashMap<TopicWave, String> nameMap = new HashMap<>();

    for (TopicWave wave : waves)
    {
      List<Double> yValues = new ArrayList<>();
      TreeMap<Date, Topic> topicSequence = wave.getTopicSequence();

      HashMap<String, Double> bestTermMap = new HashMap<>();

      for (Date d: uniqueTimeSteps)
      {
        Topic topic = topicSequence.get(d);
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

      nameMap.put(wave, wave.getName(", "));
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
