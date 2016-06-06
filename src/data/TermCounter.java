package data;

import java.io.Serializable;

public class TermCounter implements Serializable{
	private static final long serialVersionUID = -1452038345572883411L;
	
	// counts the number of distinct terms
	private static int numberOfTerms = 0;
	// counts occurrences in all documents
	private int termCounter;
	// counts number of documents containing this term
	private int documentCounter;
	// stores last document id
	private int lastDocumentID;
	// term id
	private int termID;
	

	public TermCounter(int documentID) {
		this.lastDocumentID = documentID;
		termCounter = 1;
		documentCounter = 1;
		termID = ++numberOfTerms;
	}
	public TermCounter(){
		this.lastDocumentID = -1;
		this.termCounter = 0;
		this.documentCounter = 0;
		this.termID = 0;
	}
	public void increase(int documentID) {
		termCounter++;
		
		if (lastDocumentID != documentID){
			documentCounter++;
			lastDocumentID = documentID;
		}
	}

	public int getTermCounter(){
		return termCounter;
	}
	public int getDocumentCounter(){
		return documentCounter;
	}
	public int getLastDocumentID(){
		return lastDocumentID;
	}
	public void setTermCounter(int terms){
		this.termCounter = terms;
	}
	public void setDocumentCounter(int docs){
		this.documentCounter = docs;
	}
	public void setLastDocumentID(int id){
		this.lastDocumentID = id;
	}
	public static int getNumberOfTerms() {
		return numberOfTerms;
	}
	public static void setNumberOfTerms(int numberOfTerms) {
		TermCounter.numberOfTerms = numberOfTerms;
	}
	public int getTermID() {
		return termID;
	}
	public void setTermID(int termID) {
		this.termID = termID;
	}
	
}