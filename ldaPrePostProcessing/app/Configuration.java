package app;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

	public enum modes 
	{
		generateVocabulary,
		generateWordCount,
		generateVisData,
		evaluateTopics,
		evaluateWordDistributions,
		evaluateDTMTopics
	}
	
	public modes mode;
	
	public String corpusPath;
	public String stopwordsFile;
	public String dictionaryFile;
	
	public String resultDir;
	public String vocabularyFilenameBase;
	
	public String metaDataFilename;
	public String topicsPerDocFilename;
	
	public String topicTopWordsFilename;
	public String topicClustersFilename;
	public String visDataFilename;
	
	public String docsPerTopicFilename;
	
	public String dtmTopicScorePerTimestepFilename;
	public String dtmTopicWordDistributions;
	public int dtmNumTimesteps;
	
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
			//new FileInputStream(propFileName);
			prop.load(is);
			is.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("[Configuration::loadConfig] ERROR: config file not found : " + propFileName);
			return false;
			
		} 
		catch (IOException e)
		{
			System.out.println("[Configuration::loadConfig] ERROR ");
			e.printStackTrace();
			return false;
		}

		corpusPath = prop.getProperty("CorpusPath");
		stopwordsFile = prop.getProperty("StopwordsFile");
		dictionaryFile = prop.getProperty("DictionaryFile");
		resultDir = prop.getProperty("ResultDir");
		vocabularyFilenameBase = prop.getProperty("VocabularyFilenameBase");
		metaDataFilename = prop.getProperty("MetaDataFilename");
		topicsPerDocFilename = prop.getProperty("TopicsPerDocFilename");
		topicTopWordsFilename = prop.getProperty("TopicTopWordsFilename");
		topicClustersFilename = prop.getProperty("TopicClustersFilename");
		visDataFilename = prop.getProperty("VisDataFilename");
		
		docsPerTopicFilename = prop.getProperty("DocsPerTopicFilename");
		
		switch(mode)
		{
			case generateVocabulary:
			{
				if (corpusPath == null
						|| stopwordsFile == null
						|| dictionaryFile == null
						|| resultDir == null
						|| vocabularyFilenameBase == null)
				{
					System.out.println("corpusPath, stopwordsFile, dictionaryFile, resultDir or vocabularyFilenameBase missing.");
					return false;
				}
			}
			break;
			case generateWordCount:
			{
				if (resultDir == null
						|| vocabularyFilenameBase == null)
				{
					System.out.println("resultDir or vocabularyFilenameBase missing.");
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
					System.out.println("corpusPath or resultDir missing.");
					return false;
				}
			}
			break;
			case generateVisData:
			{
				if (resultDir == null
						|| corpusPath == null
						|| metaDataFilename == null
						|| topicsPerDocFilename == null
						|| topicTopWordsFilename == null
						|| topicClustersFilename == null
						|| visDataFilename == null)
				{
					System.out.println("corpusPath or resultDir missing.");
					return false;
				}
			}
			break;
			case evaluateWordDistributions:
			{
				String dtmNumTimestepsString = prop.getProperty("DTMNumTimesteps");
				dtmTopicWordDistributions = prop.getProperty("DTMTopicsFilename");
				IntraTopicSimilaritiesFilename = prop.getProperty("IntraTopicSimilaritiesFilename");
				InterTopicSimilaritiesFilename = prop.getProperty("InterTopicSimilaritiesFilename");
				
				if (dtmTopicWordDistributions == null
						|| dtmNumTimestepsString == null
						|| IntraTopicSimilaritiesFilename == null
						|| InterTopicSimilaritiesFilename == null)
				{
					System.out.println("docsPerTopicFilename or DTMNumTimesteps missing.");
					return false;
				}
				
				try
				{
					dtmNumTimesteps = Integer.parseInt(dtmNumTimestepsString);
				}
				catch (NumberFormatException e)
				{
					System.out.println("DTMNumTimesteps is not an integer.");
					return false;
				}
			}
			break;
			case evaluateDTMTopics:
			{
				dtmTopicScorePerTimestepFilename = prop.getProperty("TopicScorePerTimestepFilename");
				dtmTopicWordDistributions = prop.getProperty("DTMTopicsFilename");
				String dtmNumTimestepsString = prop.getProperty("DTMNumTimesteps");
				
				if (dtmTopicScorePerTimestepFilename == null
						||dtmTopicWordDistributions == null
						|| dtmNumTimestepsString == null)
				{
					System.out.println("TopicScorePerTimestepFilename or DTMTopicsFilename or DTMNumTimesteps missing.");
					return false;
				}

				try
				{
					dtmNumTimesteps = Integer.parseInt(dtmNumTimestepsString);
				}
				catch (NumberFormatException e)
				{
					System.out.println("DTMNumTimesteps is not an integer.");
					return false;
				}

			}
			break;
			default:
			{
				System.out.println("No operation mode given.");
				return false;
			}
		}
		
		return true;
	}
}
