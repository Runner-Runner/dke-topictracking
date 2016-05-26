package experiments;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class Vocabulary implements WordHandler{
	private HashMap<String,Integer> vocabulary;
	
	public Vocabulary(int size) {
		vocabulary = new HashMap<>(size);
	}
	
	@Override
	public void addWord(String word) {
		Integer before = vocabulary.get(word);
		if(before == null)
			before = new Integer(0);
		before = new Integer(before.intValue()+1);
		vocabulary.put(word,before);
	}
	
	public int size(){
		return vocabulary.size();
	}
	public void removeLowOccurrances(int min){
		HashSet<String> remove = new HashSet<>();
		for(Entry<String, Integer> entry: vocabulary.entrySet()){
			if(entry.getValue()<min){
				remove.add(entry.getKey());
			}
		}
		System.out.println("removed "+remove.size());
		vocabulary.keySet().removeAll(remove);
	}
	public void saveVocabulary(String fileName){
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			for(String s : vocabulary.keySet())
				writer.println(s);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
