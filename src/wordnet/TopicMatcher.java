package wordnet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;
import nmf.Topic;
import nmf.TopicData;

public class TopicMatcher
{
  private Dictionary dictionary;
  private ArrayList<TopicSeries> sequences;

  public TopicMatcher()
  {
    sequences = new ArrayList<>();
    try
    {
      JWNL.initialize(new FileInputStream("ressources/WNProperities.xml"));
      dictionary = Dictionary.getInstance();
    }
    catch (FileNotFoundException | JWNLException e)
    {
      e.printStackTrace();
    }

  }

  public static void main(String[] args)
  {
    Topic one = new Topic();
    one.addTerm("cook", 10.0);
    one.addTerm("menu", 9.0);
    one.addTerm("meal", 8.0);
    one.addTerm("food", 7.0);
    one.addTerm("bill", 6.0);

    Topic two = new Topic();
    two.addTerm("ship", 10.0);
    two.addTerm("car", 9.0);
    two.addTerm("street", 8.0);
    two.addTerm("container", 7.0);
    two.addTerm("harbour", 6.0);

    Topic three = new Topic();
    three.addTerm("restaurant", 10.0);
    three.addTerm("meat", 9.0);
    three.addTerm("vegetable", 8.0);
    three.addTerm("food", 7.0);
    three.addTerm("spoon", 6.0);
    TopicMatcher tm = new TopicMatcher();
    
    System.out.println(tm.compareTopics(one, two));
    System.out.println(tm.compareTopics(one, three));
    System.out.println(tm.compareTopics(three, two));
  }

  public void add(Topic topic, int timeunit)
  {

  }

  public void compareTopicData(
          TopicData topicData1, TopicData topicData2)
  {
    List<Topic> topics1 = topicData1.getTopics();
    List<Topic> topics2 = topicData2.getTopics();

    for (Topic topic1 : topics1)
    {
      double bestValue = 0;
      Topic bestCompareTopic = null;
      for (Topic topic2 : topics2)
      {
        double compareValue = compareTopics(topic1, topic2);

        if (bestValue < compareValue)
        {
          bestValue = compareValue;
          bestCompareTopic = topic2;
        }
      }
      System.out.println("Topic 1: " + topic1);
      System.out.println("Best Match ("+bestValue+"): " + bestCompareTopic);
    }

  }

  public double compareTopics(Topic one, Topic two)
  {
    int termAmount = 20;
    int matchThreshold = 5;
    double d = 0;
    List<String> bestTerms1 = one.getBestTerms(termAmount);
    List<String> bestTerms2 = two.getBestTerms(termAmount);

    int matches = 0;
    for (String term : bestTerms1)
    {
      if (bestTerms2.contains(term))
      {
        matches++;
      }
    }
    return matches;

//		double d = 0;
//		for(Entry<Double,String> entryOne : one.getTerms().entrySet()){
//			double best = Double.MAX_VALUE;
//			for(Entry<Double,String> entryTwo : two.getTerms().entrySet()){
//				double add = compareWords(entryOne.getValue(), entryTwo.getValue());
//				//System.out.println(entryOne.getValue()+" "+entryTwo.getValue()+" "+add);
//				if(add<best)
//					best = add;
//				
//			}
//			d+=best;///(entryOne.getKey()*entryTwo.getKey());
//		}
//		return d;
  }

  public int compareWords(String one, String two) throws JWNLException
  {
    IndexWord iWordOne = getIndexWord(one);
    IndexWord iWordTwo = getIndexWord(two);
    if (iWordOne == null || iWordTwo == null)
    {
      System.out.println(one + " " + two + " " + iWordOne + " " + iWordTwo);
      if (one.equals(two))
      {
        return 0;
      }
      return 20;
    }
    RelationshipList horizontal = RelationshipFinder.getInstance().findRelationships(iWordOne.getSense(1), iWordTwo.getSense(1), PointerType.SIMILAR_TO);
    RelationshipList vertical = RelationshipFinder.getInstance().findRelationships(iWordOne.getSense(1), iWordTwo.getSense(1), PointerType.HYPERNYM);

    int h = 36, v = 36;
    if (horizontal.size() > 0)
    {
      h = ((Relationship) horizontal.get(0)).getDepth();
    }
    if (vertical.size() > 0)
    {
      v = ((Relationship) vertical.get(0)).getDepth();
    }
    //return h>v?v:h;
    return h + v;
  }

  private IndexWord getIndexWord(String word) throws JWNLException
  {
    IndexWord iWord = dictionary.lookupIndexWord(POS.NOUN, word);
    if (iWord != null)
    {
      return iWord;
    }
    iWord = dictionary.lookupIndexWord(POS.VERB, word);
    if (iWord != null)
    {
      return iWord;
    }
    iWord = dictionary.lookupIndexWord(POS.ADJECTIVE, word);
    if (iWord != null)
    {
      return iWord;
    }
    return dictionary.lookupIndexWord(POS.ADVERB, word);

  }

}
