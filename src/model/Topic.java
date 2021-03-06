package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import nmf.Document;
import normalization.Normalizer;

public class Topic implements Serializable {
	private static final long serialVersionUID = 6420397376392250856L;
	private TreeMap<Double, String> terms;
	private TreeMap<Double, String> relativeTerms;
	private TreeMap<Double, Document> documentRankings;
	private double relativeRelevance;
	private double absoluteRelevance;
	private Date timeStamp;
	private int interval;

	public Topic() {
		terms = new TreeMap<>();
	}
	
	
	
	public Date getTimeStamp() {
		return timeStamp;
	}



	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}



	public int getInterval() {
		return interval;
	}



	public void setInterval(int interval) {
		this.interval = interval;
	}



	public double getAbsoluteRelevance() {
		return absoluteRelevance;
	}

	public void normalizeTerms() {
		double all = 0;
		for (double relevance : terms.keySet()) {
			all += relevance;
		}
		relativeTerms = new TreeMap<>();
		for (Entry<Double, String> entry : terms.entrySet()) {
			relativeTerms.put(entry.getKey() / all, entry.getValue());
		}
	}
	
	public TreeMap<Double,String> getNormalizedTerms(){
		if(relativeTerms == null)
    {
      normalizeTerms();
    }
    return relativeTerms;
	}

	public void setAbsoluteRelevance(double absoluteRelevance) {
		this.absoluteRelevance = absoluteRelevance;
	}


	public void setTerms(TreeMap<Double, String> terms) {
		this.terms = terms;
	}

	public void setDocumentRankings(TreeMap<Double, Document> documentRankings) {
		this.documentRankings = documentRankings;
	}

	public void addTerm(String term, Double termValue) {
		terms.put(termValue, term);
	}

	public TreeMap<Double, String> getTerms() {
		return terms;
	}

	public HashMap<String, Double> getBestTerms(int termCount) {
		HashMap<String, Double> bestTerms = new HashMap<>();
		Iterator<Map.Entry<Double, String>> iterator = getNormalizedTerms().
            descendingMap().entrySet().iterator();
		
    if(termCount == -1)
    {
      termCount = terms.size();
    }
    
    for (int i = 0; i < termCount; i++) {
			if (!iterator.hasNext()) {
				break;
			}
			Map.Entry<Double, String> entry = iterator.next();
			bestTerms.put(entry.getValue(), entry.getKey());
		}
		return bestTerms;
	}

	public TreeMap<Double, Document> getDocumentRankings() {
		return documentRankings;
	}

	public String toShortString() {
		Iterator<Map.Entry<Double, String>> termIterator = getTerms().descendingMap().entrySet().iterator();

		String termsText = "";
		int termIndex = 0;
		while (termIterator.hasNext() && termIndex < 20) {
			termIndex++;
			Map.Entry<Double, String> termEntry = termIterator.next();
			Double tfidf = termEntry.getKey();
			if (tfidf <= 0.00001) {
				break;
			}
			String term = termEntry.getValue();
			String original = Normalizer.getOriginal(term);
			termsText += " " + original;
		}

		termsText += "\t\t\t\t";
		Iterator<Map.Entry<Double, Document>> docIterator = getDocumentRankings().descendingMap().entrySet().iterator();
		int docIndex = 0;
		while (docIterator.hasNext() && docIndex < 5) {
			docIndex++;
			Map.Entry<Double, Document> docEntry = docIterator.next();
			Double tfidf = docEntry.getKey();
			if (tfidf <= 0.00001) {
				break;
			}
			String doc = docEntry.getValue().getTitle();
			termsText += " " + doc;
		}

		return termsText;
	}

	@Override
	public String toString() {
		Iterator<Map.Entry<Double, String>> termIterator = getNormalizedTerms().descendingMap().entrySet().iterator();

		String termsText = "";
		int termIndex = 0;
		while (termIterator.hasNext() && termIndex < 20) {
			termIndex++;
			Map.Entry<Double, String> termEntry = termIterator.next();
			Double tfidf = termEntry.getKey();
			if (tfidf <= 0.00001) {
				break;
			}
			String term = termEntry.getValue();
			String original = Normalizer.getOriginal(term);
			termsText += " " + original + " (" + tfidf + ")";
		}

		termsText += "\nMost Relevant Documents: ";
		Iterator<Map.Entry<Double, Document>> docIterator = getDocumentRankings().descendingMap().entrySet().iterator();
		int docIndex = 0;
		while (docIterator.hasNext() && docIndex < 3) {
			docIndex++;
			Map.Entry<Double, Document> docEntry = docIterator.next();
			Double tfidf = docEntry.getKey();
			if (tfidf <= 0.00001) {
				break;
			}
			String doc = docEntry.getValue().getTitle();
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
	public List<String> getRankedDocuments(Date date, int count){
		List<String> list = new ArrayList<>();
		int counter = 0;
		for(Entry<Double, Document> entry: documentRankings.descendingMap().entrySet()){
			if(entry.getValue().getDate().equals(date)){
				list.add(entry.getValue().getTitle());
				counter++;
				if(counter >= count)
					break;
			}
		}
		return list;
	}

}
