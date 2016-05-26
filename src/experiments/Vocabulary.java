package experiments;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

public class Vocabulary implements WordHandler{
	private HashSet<String> vocabulary;
	
	public Vocabulary(int size) {
		vocabulary = new HashSet<>(size);
	}
	
	@Override
	public void addWord(String word) {
		vocabulary.add(word);
	}
	
	public void saveVocabulary(String fileName){
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			for(String s : vocabulary)
				writer.println(s);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
