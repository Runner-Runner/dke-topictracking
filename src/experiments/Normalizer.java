package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

public class Normalizer {
	public static final char WORD_DELIMITER = ' ';

	public static void main(String[] args) {
		String inpath = "ressources/testArticle.txt";
		String outpath = "ressources/testArticle_n.txt";
		String stopwordpath = "ressources/stopwords.txt";
		normalize(inpath, outpath, readStopwords(stopwordpath));
	}

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
		}
		return stopwords;
	}

	public static void normalize(String inputPath, String outputPath, HashSet<String> stopwords) {
		char[] w = new char[501]; // word buffer
		Stemmer s = new Stemmer();
		FileInputStream in = null;
		PrintWriter out = null;
		try {
			// input file
			in = new FileInputStream(inputPath);

			// if outputFile doesnt exists, then create it
			File outputFile = new File(outputPath);
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			// output file
			out = new PrintWriter(outputFile, "UTF-8");

			// traverse file bytewise
			while (true) {
				int ch = in.read();
				if (Character.isLetter((char) ch)) {
					int j = 0;
					// traverse current word
					while (true) {
						ch = Character.toLowerCase((char) ch);
						w[j] = (char) ch;
						if (j < 500)
							j++;
						ch = in.read();
						// if not a character, process buffer data
						if (!Character.isLetter((char) ch)) {
							// one letter is not a word, might caused by 's
							if (j > 1) {
								// add chars to stemmer
								for (int c = 0; c < j; c++)
									s.add(w[c]);
								// check if its a stopword
								String original = s.getOriginal();
								s.stem();
								if(!stopwords.contains(original)){
									out.write(s.toString() + WORD_DELIMITER);
								}
								
							}
							// word processed -> go on
							break;
						}
					}
				}
				if (ch < 0)
					break;
			}
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			out.close();
		}
	}

}
