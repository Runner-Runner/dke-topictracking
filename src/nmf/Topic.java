package nmf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Topic
{
  private TreeMap<Double, String> terms;
  private TreeMap<Double, String> documentRankings;

  public Topic()
  {
    terms = new TreeMap<>();
  }

  public void addTerm(String term, Double termValue)
  {
    terms.put(termValue, term);
  }

  public TreeMap<Double, String> getTerms()
  {
    return terms;
  }

  public List<String> getBestTerms(int termCount)
  {
    List<String> bestTerms = new ArrayList<>();
    Iterator<Map.Entry<Double, String>> iterator
            = getTerms().descendingMap().entrySet().iterator();
    for (int i = 0; i < termCount; i++)
    {
      if (!iterator.hasNext())
      {
        break;
      }
      bestTerms.add(iterator.next().getValue());
    }
    return bestTerms;
  }

  void setDocumentRanking(TreeMap<Double, String> documentRankings)
  {
    this.documentRankings = documentRankings;
  }

  public TreeMap<Double, String> getDocumentRankings()
  {
    return documentRankings;
  }
}
