package nmf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import model.Topic;
import model.TopicRiver;
import model.TopicTimeStepCollection;
import model.TopicWave;

public class TopicGeneralizer
{
  public static TopicRiver generalize(TopicRiver topicRiver, int timeUnits)
  {
    HashSet<String> vocabulary = new HashSet<>();
    List<String> waveNames = new ArrayList<>();
    List<TopicWave> waves = topicRiver.getWaves();
    for (TopicWave wave : waves)
    {
      waveNames.add(wave.getName(", "));
      for (Topic topic : wave.getTopicSequence().values())
      {
        vocabulary.addAll(topic.getTerms().values());
      }
    }
    double[][] termWaveMatrix = new double[topicRiver.getWaves().size()][vocabulary.size()];
    for (int x = 0; x < waves.size(); x++)
    {
      TopicWave wave = waves.get(x);
      HashMap<String, Double> termWaveRelevances = wave.getTermWaveRelevances();
      Iterator<String> vocabularyIterator = vocabulary.iterator();
      for (int y = 0; y < vocabulary.size(); y++)
      {
        String term = vocabularyIterator.next();
        Double termWaveRelevance = termWaveRelevances.get(term);
        if(termWaveRelevance == null)
        {
          termWaveRelevance = 0.0;
        }
        termWaveMatrix[x][y] = termWaveRelevance * wave.getRelativeRelevance();
      }
    }

    System.out.println("NMF on topic - term:");
    NMFExecutor nmfExecutor = new NMFExecutor();
    nmfExecutor.execute(termWaveMatrix);

    // determine first date
    System.out.println("Determine timestamp");
    Date timestamp = null;
    // create TopicData
    System.out.print("Extract Topics ");
//    TopicTimeStepCollection topicTimeStepCollection = new TopicTimeStepCollection();
//    topicTimeStepCollection.setTimestamp(timestamp);
//    topicTimeStepCollection.extractTopicsFromMatrices(nmfExecutor.getTopicTerm(), nmfExecutor.getTopicDocument(), vocabulary, waveNames);
//    System.out.println(" - done");
//    // save topics
//    System.out.print("Save extracted topics ");
//    String outputFileName = "generalized_" + timestamp + ".xml";
//    TopicTimeStepCollection.save(outputFileName, topicTimeStepCollection);
//    System.out.println(" - done");
//    
//    System.out.println(topicTimeStepCollection);
    //TODO return river or collection?
    return null;
  }
}
