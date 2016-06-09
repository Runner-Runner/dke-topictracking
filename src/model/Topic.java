package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import normalization.Normalizer;

public class Topic implements Serializable
{
  private static final long serialVersionUID = 6420397376392250856L;
  private TreeMap<Double, String> terms;
  private TreeMap<Double, String> documentRankings;
  private double relativeRelevance;
  private double absoluteRelevance;

  public double getAbsoluteRelevance() {
	return absoluteRelevance;
}

public void setAbsoluteRelevance(double absoluteRelevance) {
	this.absoluteRelevance = absoluteRelevance;
}

public Topic()
  {
    terms = new TreeMap<>();
  }

  public void setTerms(TreeMap<Double, String> terms)
  {
    this.terms = terms;
  }

  public void setDocumentRankings(TreeMap<Double, String> documentRankings)
  {
    this.documentRankings = documentRankings;
  }

  public void addTerm(String term, Double termValue)
  {
    terms.put(termValue, term);
  }

  public TreeMap<Double, String> getTerms()
  {
    return terms;
  }

  public HashMap<String, Double> getBestTerms(int termCount)
  {
    HashMap<String, Double> bestTerms = new HashMap<>();
    Iterator<Map.Entry<Double, String>> iterator = getTerms().descendingMap().entrySet().iterator();
    for (int i = 0; i < termCount; i++)
    {
      if (!iterator.hasNext())
      {
        break;
      }
      Map.Entry<Double, String> entry = iterator.next();
      bestTerms.put(entry.getValue(), entry.getKey());
    }
    return bestTerms;
  }

  public TreeMap<Double, String> getDocumentRankings()
  {
    return documentRankings;
  }

  public String toShortString()
  {
    Iterator<Map.Entry<Double, String>> termIterator = getTerms().descendingMap().entrySet().iterator();

    String termsText = "";
    int termIndex = 0;
    while (termIterator.hasNext() && termIndex < 15)
    {
      termIndex++;
      Map.Entry<Double, String> termEntry = termIterator.next();
      Double tfidf = termEntry.getKey();
      if (tfidf <= 0.00001)
      {
        break;
      }
      String term = termEntry.getValue();
      String original = Normalizer.getOriginal(term);
      termsText += " " + original;
    }

    termsText += "\t\t\t\t";
    Iterator<Map.Entry<Double, String>> docIterator = getDocumentRankings().descendingMap().entrySet().iterator();
    int docIndex = 0;
    while (docIterator.hasNext() && docIndex < 5)
    {
      docIndex++;
      Map.Entry<Double, String> docEntry = docIterator.next();
      Double tfidf = docEntry.getKey();
      if (tfidf <= 0.00001)
      {
        break;
      }
      String doc = docEntry.getValue();
      termsText += " " + doc;
    }

    return termsText;
  }

  @Override
  public String toString()
  {
    Iterator<Map.Entry<Double, String>> termIterator = getTerms().descendingMap().entrySet().iterator();

    String termsText = "";
    int termIndex = 0;
    while (termIterator.hasNext() && termIndex < 20)
    {
      termIndex++;
      Map.Entry<Double, String> termEntry = termIterator.next();
      Double tfidf = termEntry.getKey();
      if (tfidf <= 0.00001)
      {
        break;
      }
      String term = termEntry.getValue();
      String original = Normalizer.getOriginal(term);
      termsText += " " + original + " (" + tfidf + ")";
    }

    termsText += "\nMost Relevant Documents: ";
    Iterator<Map.Entry<Double, String>> docIterator = getDocumentRankings().descendingMap().entrySet().iterator();
    int docIndex = 0;
    while (docIterator.hasNext() && docIndex < 3)
    {
      docIndex++;
      Map.Entry<Double, String> docEntry = docIterator.next();
      Double tfidf = docEntry.getKey();
      if (tfidf <= 0.00001)
      {
        break;
      }
      String doc = docEntry.getValue();
      termsText += " " + doc + " (" + tfidf + ")";
    }

    return termsText;
  }

public double getRelativeRelevance() {
	return relativeRelevance;
}

public void setRelativeRelevance(double relativeRelevance) {
	this.relativeRelevance = relativeRelevance;
}

  
}
