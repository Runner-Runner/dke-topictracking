package experiments;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class Vocabulary implements WordHandler {
	private HashMap<String, TermCounter> vocabulary;
	private int currentFileID;

	public Vocabulary(int size) {
		vocabulary = new HashMap<>(size);
		currentFileID = 0;
	}

	public void nextFile() {
		currentFileID++;
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

	public int size() {
		return vocabulary.size();
	}

	public void removeLowOccurrances(int min) {
		int best = 0;
		String b = null;
		HashSet<String> remove = new HashSet<>();
		for (Entry<String, TermCounter> entry : vocabulary.entrySet()) {
			if (entry.getValue().counter() < min || !entry.getValue().inMultipleDocuments()) {
				remove.add(entry.getKey());
			} else if (entry.getValue().counter() > best) {
				best = entry.getValue().counter();
				b = entry.getKey();
			}
		}
		System.out.println("removed " + remove.size());
		System.out.println("best " + b + " " + best);
		vocabulary.keySet().removeAll(remove);
	}
	
	

	public void saveVocabulary(String fileName) {
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

class TermCounter {
	private int counter;
	private int lastID;

	public TermCounter(int id) {
		this.lastID = id;
		counter = 1;
	}

	public void increase(int id) {
		counter++;
		if (lastID != id)
			lastID = -1;
	}

	public int counter() {
		return this.counter;
	}

	public boolean inMultipleDocuments() {
		return lastID == -1;
	}
}
