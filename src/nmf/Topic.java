package nmf;

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

  void setDocumentRanking(TreeMap<Double, String> documentRankings)
  {
    this.documentRankings = documentRankings;
  }

  public TreeMap<Double, String> getDocumentRankings()
  {
    return documentRankings;
  }
}
