package app;


import container.Vocabulary;
import postProcessing.CorpusTopicDataObject;
import postProcessing.DTMEvaluator;
import postProcessing.Evaluator;
import postProcessing.VisDataGenerator;
import preProcessing.CorpusProcessor;
import preProcessing.WordCounter;
import app.Configuration.modes;

public class Application {
	public static void main(String[] args) 
	{
		String propFileName = "resources/config.properties";
		
		Configuration config = new Configuration();
		
		for (String arg: args) 
		{
			if (arg.equals("-generateVocabulary"))
			{
				config.mode = modes.generateVocabulary;
			}
			else if (arg.equals("-generateWordcount"))
			{
				config.mode = modes.generateWordCount;
			}
			else if (arg.equals("-evaluate"))
			{
				config.mode = modes.evaluateTopics;
			}
			else if (arg.equals("-generateVisData"))
			{
				config.mode = modes.generateVisData;
			}
			else if (arg.equals("-evaluateDTM"))
			{
				config.mode = modes.evaluateDTMTopics;
			}
			else if (arg.equals("-generateStats"))
			{
				config.mode = modes.generateStats;
			}
			else
			{
				System.out.println("Application parameters defining operation mode (use one at a time):");
				System.out.println("-generateVocabulary");
				System.out.println("-generateWordcount");
				System.out.println("-evaluate");
				System.out.println("-generateVisData");
				System.out.println("-evaluateDTM");
				System.out.println("See the config.properties for more information.");
				return;
			}
        }
		
		if (!config.loadConfig(propFileName))
		{
			System.out.println("Configuration error.");
			return;
		}
		
		System.out.println("Application mode: " + config.mode);
		System.out.println(config.corpusPath);

		switch(config.mode)
		{
			case generateVocabulary:
			{
				try
				{
					CorpusProcessor p = new CorpusProcessor(
							config.corpusPath,
							config.resultDir,
							config.stopwordsFile,
							config.dictionaryFile,
							config.vocabularyFilenameBase);
					//p.processDocuments(".text");
					//p.processDocuments("2286newsML.text");
					
					p.processDocuments(".xml");
					p.processDocumentsMeta(".xml");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				System.out.println("generateVocabulary finished.");
			}
			break;
			case generateWordCount:
			{
				Vocabulary vocabulary = new Vocabulary(config.resultDir + "/" + config.vocabularyFilenameBase);
				
				WordCounter wc = new WordCounter(config.resultDir, vocabulary);
				wc.processDocuments(".txt");
				
				System.out.println("generateWordCount finished.");
			}
			break;
			case evaluateTopics:
			{
				CorpusTopicDataObject data = new CorpusTopicDataObject(
						config.resultDir + "/" + config.metaDataFilename,
						config.resultDir + "/" + config.topicsPerDocFilename);
				
				Evaluator eva = new Evaluator(data);
				
				eva.evaluteTopics();
		
				System.out.println("evaluateTopics finished.");
			}
			break;
			case generateVisData:
			{
				CorpusTopicDataObject data = new CorpusTopicDataObject(
						config.resultDir + "/" + config.metaDataFilename,
						config.resultDir + "/" + config.topicsPerDocFilename);
				
				VisDataGenerator gen = new VisDataGenerator(data, config.corpusPath);
		
				gen.writeTopicsWithDocWeightJson(config.resultDir + "/" + config.topicTopWordsFilename,
						config.resultDir + "/" + config.topicClustersFilename,
						config.resultDir + "/"+ config.visDataFilename);
				
				System.out.println("generateVisData finished.");
			}
			break;
			case generateStats:
			{
				CorpusTopicDataObject data = new CorpusTopicDataObject(
						config.resultDir + "/" + config.metaDataFilename,
						config.resultDir + "/" + config.topicsPerDocFilename);
				
				data.writeTopicsWithDocWeight(config.resultDir + "/" + config.docsPerTopicFilename);
				
				System.out.println("generateStats finished.");

			}
			break;
			case evaluateDTMTopics:
			{
				CorpusTopicDataObject data = new CorpusTopicDataObject(
						config.resultDir + "/" + config.metaDataFilename,
						config.resultDir + "/" + config.topicsPerDocFilename);
				
				DTMEvaluator eva = new DTMEvaluator(data);
				eva.writeTopicsWithDocsPerTime(config.dtmNumTimesteps,
						true,
						config.resultDir + "/" + config.docsPerTopicFilename);
				
				System.out.println("DTM topic evaluation finished.");
			}
			break;
			default:
				System.out.println("Invalid operation mode.");
				break;
		}
    }
}
