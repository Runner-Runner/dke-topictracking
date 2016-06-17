package data;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import nmf.Document;

public class Vocabulary implements WordHandler {
	
	private HashMap<String, TermCounter> vocabulary;
	private int currentFileID;

	public Vocabulary(int size) {
		vocabulary = new HashMap<>(size);
		currentFileID = 0;
	}
	@Override
	public void nextDocument(Document document) {
		currentFileID++;
	}
	
	public Set<String> getVocabulary(){
		return vocabulary.keySet();
	}
	
	@Override
	public void addWord(String word) {
		TermCounter counter = vocabulary.get(word);
		if (counter == null)
			vocabulary.put(word, new TermCounter(currentFileID));
		else {
			counter.increase(currentFileID);
		}
	}
	
	public int getTermDocumentFrequency(String term){
		return vocabulary.get(term).getDocumentCounter();
	}

	public int size() {
		return vocabulary.size();
	}

	public void removeLowOccurrances(int minFrequency, int minDocs) {
		HashSet<String> remove = new HashSet<>();
		for (Entry<String, TermCounter> entry : vocabulary.entrySet()) {
			if (entry.getValue().getTermCounter() < minFrequency || entry.getValue().getDocumentCounter() < minDocs) {
				remove.add(entry.getKey());
			}
		}
		vocabulary.keySet().removeAll(remove);
	}
	
	public void saveVocabulary(String filename){
		XMLEncoder encoder=null;
		try{
		encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
		}catch(FileNotFoundException fileNotFound){
			System.out.println("ERROR: While Creating or Opening the File "+filename);
		}
		encoder.writeObject(vocabulary);
		encoder.close();
	}
	
	@SuppressWarnings("unchecked")
	public void loadVocabulary(String filename){
		XMLDecoder decoder=null;
		try {
			decoder=new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File "+filename+" not found");
		}
		this.vocabulary = (HashMap<String, TermCounter>) decoder.readObject();
	}
	public boolean contains(String term){
		return vocabulary.containsKey(term);
	}
	public void saveVocabularyTxt(String fileName) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			for (String s : vocabulary.keySet())
				writer.println(s);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}


