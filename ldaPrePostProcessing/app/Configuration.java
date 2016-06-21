package app;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Configuration {

	/**
	 * Application operation modes
	 */
	public enum modes 
	{
		generateVocabulary,
		generateWordCount,
		evaluateTopics,
		evaluateWordDistributions,
		evaluateDTMTopics,
		evaluateReutersTopics
	}
	
	public modes mode;
	
	public String corpusPath;
	public String stopwordsFile;
	public String dictionaryFile;
	public List<String> nerExclusionCategories;
	
	public String resultDir;
	public String vocabularyFilenameBase;
	public String dataFilenameBase;
	
	public String metaDataFilename;

	public String topicsPerDocFilename;
	public String wordsPerTopicsFilename;
	public int numTimesteps;
	
	public float similarityTreshold;
	
	public int numTopWords;
	public int numTopDocs;
	
	public String outDocsPerTopicFilename;
	public String outWordsPerTopicFilename;
	public String outTopicScoresPerTimestepFilename;
	public String outVisDataFilename;
			
//	public String docsPerTopicFilename;
//	
//	public String dtmTopicScorePerTimestepFilename;
	
	public String IntraTopicSimilaritiesFilename;
	public String InterTopicSimilaritiesFilename;
	
	public Configuration()
	{}
	
	public boolean loadConfig(String propFileName)
	{
		Properties prop = new Properties();

		try
		{
			InputStream is = getClass().getClassLoader().getResourceAsStream(propFileName);
			prop.load(is);
			is.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("[Configuration::loadConfig] ERROR: config file not found : " + propFileName);
			return false;
			
		} 
		catch (IOException e)
		{
			System.err.println("[Configuration::loadConfig] ERROR ");
			e.printStackTrace();
			return false;
		}

		corpusPath = prop.getProperty("CorpusPath");
		resultDir = prop.getProperty("ResultDir");
		vocabularyFilenameBase = prop.getProperty("VocabularyFilenameBase");
		metaDataFilename = prop.getProperty("MetaDataFilename");
		
		topicsPerDocFilename = prop.getProperty("TopicsPerDocsFilename");
		wordsPerTopicsFilename = prop.getProperty("WordsPerTopicsFilename");
				
//		docsPerTopicFilename = prop.getProperty("DocsPerTopicFilename");
//		dtmTopicScorePerTimestepFilename = prop.getProperty("TopicScorePerTimestepFilename");
		

		switch(mode)
		{
			case generateVocabulary:
			{
				stopwordsFile = prop.getProperty("StopwordsFile");
				dictionaryFile = prop.getProperty("DictionaryFile");
				String nerExclusionCategoriesSring = prop.getProperty("NERExclusionCategories");
				
				if (corpusPath == null
						|| stopwordsFile == null
						|| dictionaryFile == null
						|| resultDir == null
						|| vocabularyFilenameBase == null
						|| nerExclusionCategoriesSring == null)
				{
					System.err.println("CorpusPath, StopwordsFile, DictionaryFile, ResultDir, NERExclusionCategories or VocabularyFilenameBase missing.");
					return false;
				}

				try
				{
					nerExclusionCategories = Arrays.asList(nerExclusionCategoriesSring.split(","));
				}
				catch (Exception e)
				{
					System.err.println("NERExclusionCategories wrong format.");
					return false;
				}
			}
			break;
			case generateWordCount:
			{
				dataFilenameBase = prop.getProperty("DataFilenameBase");
				String dtmNumTimestepsString = prop.getProperty("NumTimesteps");
				
				if (resultDir == null
						|| vocabularyFilenameBase == null
						|| dataFilenameBase == null
						|| dtmNumTimestepsString == null)
				{
					System.err.println("ResultDir, VocabularyFilenameBase, NumTimesteps or DataFilenameBase missing.");
					return false;
				}
				
				try
				{
					numTimesteps = Integer.parseInt(dtmNumTimestepsString);
				}
				catch (NumberFormatException e)
				{
					System.err.println("NumTimesteps is not an integer.");
					return false;
				}
			}
			break;
			case evaluateTopics:
			{
				if (resultDir == null
						|| corpusPath == null
						|| metaDataFilename == null
						|| topicsPerDocFilename == null)
				{
					System.err.println("corpusPath or resultDir missing.");
					return false;
				}
			}
			break;
			case evaluateWordDistributions:
			{
				String dtmNumTimestepsString = prop.getProperty("NumTimesteps");
				IntraTopicSimilaritiesFilename = prop.getProperty("IntraTopicSimilaritiesFilename");
				InterTopicSimilaritiesFilename = prop.getProperty("InterTopicSimilaritiesFilename");
				
				if (resultDir == null
						|| wordsPerTopicsFilename == null
						|| dtmNumTimestepsString == null
						|| IntraTopicSimilaritiesFilename == null
						|| InterTopicSimilaritiesFilename == null)
				{
					System.err.println("docsPerTopicFilename or DTMNumTimesteps missing.");
					return false;
				}
				
				try
				{
					numTimesteps = Integer.parseInt(dtmNumTimestepsString);
				}
				catch (NumberFormatException e)
				{
					System.err.println("NumTimesteps is not an integer.");
					return false;
				}
			}
			break;
			case evaluateDTMTopics:
			{
				outDocsPerTopicFilename = prop.getProperty("OutDocsPerTopicFilename");
				outWordsPerTopicFilename = prop.getProperty("OutWordsPerTopicFilename");
				outTopicScoresPerTimestepFilename = prop.getProperty("OutTopicScoresPerTimestepFilename");
				outVisDataFilename = prop.getProperty("OutVisDataFilename");
				
				String dtmNumTimestepsString = prop.getProperty("NumTimesteps");
				String similarityTresholdString = prop.getProperty("SimilarityTreshold");
				String numTopWordsString = prop.getProperty("NumTopWords");
				String numTopDocsString = prop.getProperty("NumTopDocs");
				
				if (resultDir == null
						|| metaDataFilename == null
						|| topicsPerDocFilename == null
						|| wordsPerTopicsFilename == null
						|| vocabularyFilenameBase == null
						|| dtmNumTimestepsString == null
						|| similarityTresholdString == null
						|| numTopWordsString == null
						|| numTopDocsString == null
						|| outDocsPerTopicFilename == null
						|| outWordsPerTopicFilename == null
						|| outTopicScoresPerTimestepFilename == null
						|| outVisDataFilename == null)
				{
					System.err.println("ResultDir, TopicsPerDocsFilename, WordsPerTopicsFilename, VocabularyFilenameBase, MetaDataFilename, OutDocsPerTopicFilename, OutWordsPerTopicFilename, OutTopicScoresPerTimestepFilename, OutVisDataFilename, NumTopWords, NumTopDocs, NumTimesteps or SimilarityTreshold missing.");
					return false;
				}

				try
				{
					numTimesteps = Integer.parseInt(dtmNumTimestepsString);
					similarityTreshold = Float.parseFloat(similarityTresholdString);
					numTopWords = Integer.parseInt(numTopWordsString);
					numTopDocs = Integer.parseInt(numTopDocsString);
				}
				catch (NumberFormatException e)
				{
					System.err.println("NumTopWords, NumTopDocs, NumTimesteps or SimilarityTreshold wrongly formatted.");
					return false;
				}
			}
			break;
			case evaluateReutersTopics:
			{
				String dtmNumTimestepsString = prop.getProperty("NumTimesteps");
				
				if (resultDir == null
						|| metaDataFilename== null
						|| dtmNumTimestepsString == null)
				{
					System.err.println("resultDir or metaDataFilename or DTMNumTimesteps missing.");
					return false;
				}

				try
				{
					numTimesteps = Integer.parseInt(dtmNumTimestepsString);
				}
				catch (NumberFormatException e)
				{
					System.err.println("NumTimesteps is not an integer.");
					return false;
				}
			}
			break;
			default:
			{
				System.err.println("No operation mode given.");
				return false;
			}
		}
		
		return true;
	}
}
