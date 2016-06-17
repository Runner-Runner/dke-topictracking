package data;

import nmf.Document;

public interface WordHandler {
	public void addWord(String word);

	void nextDocument(Document document);
}
