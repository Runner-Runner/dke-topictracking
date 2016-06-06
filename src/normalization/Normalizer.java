package normalization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import data.WordHandler;

//TODO remove August, Juli etc.

public class Normalizer {
	private static HashMap<String, String> stemmingOriginalMapping = new HashMap<>();


	public static HashSet<String> readStopwords(String path) {
		HashSet<String> stopwords = new HashSet<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while (line != null) {
				stopwords.add(line.trim());
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Stopwords could not be read.");
		}
		return stopwords;
	}

	public static void normalize(File inputFile, WordHandler wordHandler, HashSet<String> stopwords) {
		// test for recursion
		if (inputFile.isDirectory()) {
			for (File subfile : inputFile.listFiles())
				normalize(subfile, wordHandler, stopwords);
			return;
		}
		char[] w = new char[501]; // word buffer
		Stemmer s = new Stemmer();
		FileInputStream in = null;
		try {
			// input file
			in = new FileInputStream(inputFile);
			// traverse file bytewise
			while (true) {
				int ch = in.read();
				if (Character.isLetter((char) ch)) {
					int j = 0;
					// traverse current word
					while (true) {
						ch = Character.toLowerCase((char) ch);
						w[j] = (char) ch;
						if (j < 500) {
							j++;
						}
						ch = in.read();
						// if not a character, process buffer data
						if (!Character.isLetter((char) ch)) {
							// one letter is not a word, might caused by 's
							if (j > 1) {
								// add chars to stemmer
								for (int c = 0; c < j; c++) {
									s.add(w[c]);
								}
								// check if its a stopword
								String original = s.getOriginal();
								s.stem();
								String americanized = Americanizer.americanize(original).toLowerCase();
								if (!original.equals(americanized)) {
									for (char signleChar : americanized.toCharArray())
										s.add(signleChar);
									s.stem();
									original = americanized;
								}
								if (!stopwords.contains(original)) {
									String stemmString = s.toString();
									wordHandler.addWord(stemmString);
									if (!stemmString.equals(original)) {
										stemmingOriginalMapping.put(stemmString, original);
									}
								}

							}
							// word processed -> go on
							break;
						}
					}
				}
				if (ch < 0) {
					break;
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static String getOriginal(String stemmedTerm) {
		String original = stemmingOriginalMapping.get(stemmedTerm);
		if (original == null) {
			original = stemmedTerm;
		}
		return original;
	}

	public static HashMap<String, String> getStemmingOriginalMapping() {
		return stemmingOriginalMapping;
	}
}
