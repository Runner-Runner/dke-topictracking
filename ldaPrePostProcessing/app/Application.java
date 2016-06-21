package app;


import data.DocumentHandlerInterface;
import data.ReutersMetaData;
import data.ReutersXMLHandler;
import data.TopicDistributions;
import data.WordDistributions;
import postProcessing.DTMEvaluator;
import postProcessing.Evaluator;
import preProcessing.PreProcessor;
import preProcessing.WordCounter;
import wordContainer.Vocabulary;
import app.Configuration.modes;

public class Application {
	public static void main(String[] args) 
	{
		String propFileName = "resources/config.properties";
		
		Configuration config = new Configuration();
		
		if (args.length < 1)
		{
			System.out.println("Application parameters defining operation mode (use one at a time):");
			System.out.println("-generateVocabulary\t\tGenerate vocabulary and intermediate document text files.");
			System.out.println("-generateWordcount\t\tGenerate word counts from vocabulary and intermediate document text files.");
			System.out.println("-evaluateDTM\t\t\tGenerate output for further processing like topic scores, word and document lists.");
//			System.out.println("-evaluate\t\t\tbla");
			System.out.println("-evaluateSimilarities\t\tCompute cosine similarites among topic time steps as well as between all topics");
			System.out.println("See the resources/config.properties for more information.");
			return;
		}
		
		/**
		 * Resolving operation mode from command line parameter
		 */
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
			else if (arg.equals("-evaluateDTM"))
			{
				config.mode = modes.evaluateDTMTopics;
			}
			else if (arg.equals("-evaluateSimilarities"))
			{
				config.mode = modes.evaluateWordDistributions;
			}
			else if(arg.equals("-evaluateReuters"))
			{
				config.mode = modes.evaluateReutersTopics;
			}
			else
			{
				System.out.println("Application parameters defining operation mode (use one at a time):");
				System.out.println("-generateVocabulary\t\tGenerate vocabulary and intermediate document text files.");
				System.out.println("-generateWordcount\t\tGenerate word counts from vocabulary and intermediate document text files.");
				System.out.println("-evaluateDTM\t\t\tGenerate output for further processing like topic scores, word and document lists.");
//				System.out.println("-evaluate\t\t\tbla");
				System.out.println("-evaluateSimilarities\tCompute cosine similarites among topic time steps as well as between all topics");
				System.out.println("See the resources/config.properties for more information.");
				return;
			}
        }
		
		if (!config.loadConfig(propFileName))
		{
			System.err.println("Configuration error.");
			return;
		}
		
		System.out.println("Application mode: " + config.mode);
		
		/**
		 * Operate according to mode
		 */
		switch(config.mode)
		{
			case generateVocabulary:
			{
				try
				{
					System.out.println("Corpus path: " + config.corpusPath);
					
					DocumentHandlerInterface docReader = new ReutersXMLHandler();
					
					PreProcessor p = new PreProcessor(
							config.corpusPath,
							config.resultDir,
							config.stopwordsFile,
							config.dictionaryFile,
							config.nerExclusionCategories,
							config.vocabularyFilenameBase,
							docReader);
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
				
				WordCounter wc = new WordCounter(config.resultDir, 
						config.dataFilenameBase,
						vocabulary);
				wc.processDocuments(".txt");
				
				ReutersMetaData reutersData = new ReutersMetaData(config.resultDir + "/" + config.metaDataFilename);
				
				reutersData.generateTimestepFile(config.resultDir + "/" + config.dataFilenameBase + "-seq.dat",
						config.numTimesteps);
				
				System.out.println("generateWordCount finished.");
			}
			break;
			case evaluateTopics:
			{
				ReutersMetaData reutersData = new ReutersMetaData(config.resultDir + "/" + config.metaDataFilename);
				
				TopicDistributions ldaData = new TopicDistributions(config.resultDir + "/" + config.topicsPerDocFilename);
				
				Evaluator eva = new Evaluator(reutersData, ldaData);
				
				eva.evaluteTopics(0.1f);
		
				System.out.println("evaluateTopics finished.");
			}
			break;
			case evaluateWordDistributions:
			{
				WordDistributions dtmWDs = new WordDistributions(config.numTimesteps,
						config.resultDir + "/" + config.wordsPerTopicsFilename);
				
				String filename = config.resultDir + "/" + config.IntraTopicSimilaritiesFilename;
				
				boolean distanceInsteadOfSimilarity = false;
				
				if (distanceInsteadOfSimilarity)
				{
					System.out.println("computing IntraTopicDistances.");
					tools.IOTools.writeIntMatrix(filename, dtmWDs.computeIntraTopicDistances(config.numTopWords));
				}
				else
				{
					System.out.println("computing IntraTopicSimilarities.");
					tools.IOTools.writeDoubleMatrix(filename, dtmWDs.computeIntraTopicSimilarities());
				}
				
				for (int timeStep = 0; timeStep < config.numTimesteps; ++timeStep)
				{
					filename = config.resultDir + "/" + config.InterTopicSimilaritiesFilename + timeStep + ".txt";

					if (distanceInsteadOfSimilarity)
					{
						System.out.println("computing InterTopicDistances.");
						int[][] interTopipcSimiliarity = dtmWDs.computeInterTopicDistances(timeStep, config.numTopWords);
						tools.IOTools.writeIntMatrix(filename, interTopipcSimiliarity);
					}
					else
					{
						System.out.println("computing InterTopicSimilarities.");
						double[][] interTopipcSimiliarity = dtmWDs.computeInterTopicSimilarities(timeStep);
						tools.IOTools.writeDoubleMatrix(filename, interTopipcSimiliarity);
					}
				}
				
				System.out.println("evaluateWordDistributions finished.");

			}
			break;
			case evaluateDTMTopics:
			{
				ReutersMetaData reutersData = new ReutersMetaData(config.resultDir + "/" + config.metaDataFilename);
				
				TopicDistributions ldaData = new TopicDistributions(config.resultDir + "/" + config.topicsPerDocFilename);

				WordDistributions wordData = new WordDistributions(config.numTimesteps,
						config.resultDir + "/" + config.wordsPerTopicsFilename);
				
				DTMEvaluator eva = new DTMEvaluator(reutersData, ldaData, wordData, config.numTimesteps);
				
				Vocabulary vocabulary = new Vocabulary(config.resultDir + "/" + config.vocabularyFilenameBase);
				
				double[][] topicSimilarities = wordData.computeIntraTopicSimilarities();

				float[][] timestepTopics = eva.computeTopicsWithDocsPerTime(true, 0.1f);
				
				eva.addTopics(topicSimilarities,
						config.intraSimilarityTreshold,
						config.numTopDocs,
						vocabulary, 
						config.numTopWords,
						timestepTopics,
						config.resultDir + "/" + config.outDocsPerTopicFilename,
						config.resultDir + "/" + config.outWordsPerTopicFilename,
						config.resultDir + "/" + config.outTopicScoresPerTimestepFilename,
						config.resultDir + "/" + config.outVisDataFilename);

				
				eva.aggregateTopics(config.interSimilarityTreshold, config.resultDir + "/" + config.outTopicAggregationFilename);
				
//				eva.writeTopicsWithDocWeight(config.resultDir + "/" + config.docsPerTopicFilename);

//				eva.writeTopicsWithDocsPerTime(true,
//						config.resultDir + "/" + config.docsPerTopicFilename);
			
				//String[][] topWords = wordData.getTopicsAsWordsForTimeStep(vocabulary, 5, 0);
				//tools.IOUtils.writeTopicTopWords(config.resultDir + "/topicTopWords2.txt", topWords);

//				tools.IOUtils.writeTimestepTopicsAsJason(config.resultDir + "/topics2_n.js", 
//						topWords, 
//						timestepTopics2);
				
				System.out.println("DTM topic evaluation finished.");
			}
			break;
			case evaluateReutersTopics:
			{
				ReutersMetaData reutersData = new ReutersMetaData(config.resultDir + "/" + config.metaDataFilename);
				
				String[] topicNames = reutersData.getTopicNamesAsArray();
				
				String[][] topicNamesTimesteppd = new String[topicNames.length][1];
				
				for (int i = 0; i < topicNames.length; i++) 
				{
					topicNamesTimesteppd[i][0] = topicNames[i];
				}
				
				float[][] timestepTopics = reutersData.computeAnnotatedTopicsWithDocsPerTime(config.numTimesteps);
				
				tools.IOTools.writeTimestepTopicsAsJason(config.resultDir + "/topics_a.js", topicNamesTimesteppd, timestepTopics);
				
				System.out.println("DTM topic evaluation finished.");
			}
			break;
			default:
				System.out.println("Invalid operation mode.");
				break;
		}
    }
}
