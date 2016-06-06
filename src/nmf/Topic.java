package nmf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import normalization.Normalizer;

public class Topic implements Serializable {

	private static final long serialVersionUID = 6420397376392250856L;
	private TreeMap<Double, String> terms;

	public void setTerms(TreeMap<Double, String> terms) {
		this.terms = terms;
	}

	public void setDocumentRankings(TreeMap<Double, String> documentRankings) {
		this.documentRankings = documentRankings;
	}

	private TreeMap<Double, String> documentRankings;

	public Topic() {
		terms = new TreeMap<>();
	}

	public void addTerm(String term, Double termValue) {
		terms.put(termValue, term);
	}

	public TreeMap<Double, String> getTerms() {
		return terms;
	}

	public List<String> getBestTerms(int termCount) {
		List<String> bestTerms = new ArrayList<>();
		Iterator<Map.Entry<Double, String>> iterator = getTerms().descendingMap().entrySet().iterator();
		for (int i = 0; i < termCount; i++) {
			if (!iterator.hasNext()) {
				break;
			}
			bestTerms.add(iterator.next().getValue());
		}
		return bestTerms;
	}

	void setDocumentRanking(TreeMap<Double, String> documentRankings) {
		this.documentRankings = documentRankings;
	}

	public TreeMap<Double, String> getDocumentRankings() {
		return documentRankings;
	}

	@Override
	public String toString() {
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
			termsText += " " + original + " (" + tfidf + ")";
		}

		termsText += "\nMost Relevant Documents: ";
		Iterator<Map.Entry<Double, String>> docIterator = getDocumentRankings().descendingMap().entrySet().iterator();
		int docIndex = 0;
		while (docIterator.hasNext() && docIndex < 3) {
			docIndex++;
			Map.Entry<Double, String> docEntry = docIterator.next();
			Double tfidf = docEntry.getKey();
			if (tfidf <= 0.00001) {
				break;
			}
			String doc = docEntry.getValue();
			termsText += " " + doc + " (" + tfidf + ")";
		}

		return termsText;
	}
}
