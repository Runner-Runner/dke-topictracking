package experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Normalizer {
	public static final char WORD_DELIMITER = ' ';
	
	public static void main(String[] args) {
		String inpath = "ressources/testArticle.txt";
		String outpath = "ressources/testArticle_n.txt";
		normalize(inpath, outpath);
	}
	public static void normalize(String inputPath, String outputPath) {
		char[] w = new char[501];	//word buffer
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
							if(j>1){
								// add chars to stemmer
								for (int c = 0; c < j; c++)
									s.add(w[c]);
								s.stem();
								out.write(s.toString()+WORD_DELIMITER);
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
		}
		finally{
			out.close();
		}
	}

}
