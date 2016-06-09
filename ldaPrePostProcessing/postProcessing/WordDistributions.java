package postProcessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class WordDistributions {

	private ArrayList<ArrayList<ArrayList<Float>>> topicTimestepWordDistributions;
	
	public ArrayList<ArrayList<ArrayList<Float>>> getTopicTimestepWordDistributions() 
	{
		return topicTimestepWordDistributions;
	}
	
	public ArrayList<Float> getWordDistribution(final int topicId, final int timestep)
	{
		return topicTimestepWordDistributions.get(topicId).get(timestep);
	}

	private Integer lineIndex;
	
	public WordDistributions(final int numTimesteps, final String betaFilename)
	{
		assert(numTimesteps > 0);
		
		topicTimestepWordDistributions = new ArrayList<>();
		
		loadTopicWordDistributions(numTimesteps, betaFilename);
		
		System.out.println("[DTMWordDistributions] initialization done.");
	}
	
	private void loadTopicWordDistributions(final int numTimesteps, final String filename)
	{
		if (Files.exists(Paths.get(filename))) 
		{
			synchronized (lineIndex) {
				lineIndex = 0;
			}

			try (Stream<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset()))
			{
				System.out.println("[DTMWordDistributions::loadTopicWordDistributions] Loading data from " + filename);
				
				lines.forEachOrdered(line -> readLine(numTimesteps, line));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("ERROR: file does not exist: " + filename);
		}
	}
	
	private void readLine(final int numTimesteps, final String line)
	{
		String[] parts = line.split(" ");
		ArrayList<Float> liWordDistribution = new ArrayList<Float>();
		
		for (int i = 0; i < parts.length; i++)
		{
			float wordProb = Float.parseFloat(parts[i]);
			liWordDistribution.add(wordProb);
		}

		synchronized (topicTimestepWordDistributions)
		{
			synchronized (lineIndex)
			{
			
				int currentTopic = lineIndex / numTimesteps;
				if (topicTimestepWordDistributions.size() <= currentTopic)
				{
					ArrayList<ArrayList<Float>> liTimesteps = new ArrayList<>(numTimesteps); 
					
					topicTimestepWordDistributions.add(liTimesteps);
				}
				
				int currentTimestep = lineIndex % numTimesteps;
				assert(topicTimestepWordDistributions.get(currentTopic).size() == currentTimestep);
				
				topicTimestepWordDistributions.get(currentTopic).add(liWordDistribution);

				++lineIndex;
			}
		}
	}
	
	private void test(final String file)
	{
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);

			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			System.out.println(topicTimestepWordDistributions.size());
			for (ArrayList<ArrayList<Float>> timesteps : topicTimestepWordDistributions) 
			{
				System.out.println(timesteps.size());
				for (ArrayList<Float> words : timesteps)
				{
					System.out.println(words.size());

					String line = null;
					if ((line = br.readLine()) != null)
					{
						String[] parts = line.split(" ");
						for (int i = 0; i < parts.length; i++)
						{
							float wordProb = Float.parseFloat(parts[i]);
							assert(words.get(i) == wordProb);
						}
					}

				}
			}

			br.close();

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
