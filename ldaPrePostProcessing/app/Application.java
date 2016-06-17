package app;


import container.Vocabulary;
import edu.stanford.nlp.io.IOUtils;
import postProcessing.ReutersMetaData;
import postProcessing.TopicDistributions;
import postProcessing.DTMEvaluator;
import postProcessing.WordDistributions;
import postProcessing.Evaluator;
import postProcessing.VisDataGenerator;
import preProcessing.CorpusProcessor;
import preProcessing.WordCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

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
			else if (arg.equals("-evaluateWordDistributions"))
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
				System.out.println("-generateVocabulary");
				System.out.println("-generateWordcount");
				System.out.println("-evaluate");
				System.out.println("-generateVisData");
				System.out.println("-evaluateDTM");
				System.out.println("-evaluateWordDistributions");
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
				ReutersMetaData reutersData = new ReutersMetaData(config.resultDir + "/" + config.metaDataFilename);
				
				TopicDistributions ldaData = new TopicDistributions(config.resultDir + "/" + config.topicsPerDocFilename);
				
				Evaluator eva = new Evaluator(reutersData, ldaData);
				
				eva.evaluteTopics();
		
				System.out.println("evaluateTopics finished.");
			}
			break;
			case generateVisData:
			{
				ReutersMetaData reutersData = new ReutersMetaData(config.resultDir + "/" + config.metaDataFilename);
				
				TopicDistributions ldaData = new TopicDistributions(config.resultDir + "/" + config.topicsPerDocFilename);

				VisDataGenerator gen = new VisDataGenerator(reutersData, ldaData, config.corpusPath);
		
				gen.writeTopicsWithDocWeightJson(config.resultDir + "/" + config.topicTopWordsFilename,
						config.resultDir + "/" + config.topicClustersFilename,
						config.resultDir + "/"+ config.visDataFilename);
				
				System.out.println("generateVisData finished.");
			}
			break;
			case evaluateWordDistributions:
			{
				WordDistributions dtmWDs = new WordDistributions(config.dtmNumTimesteps,
						config.resultDir + "/" + config.dtmTopicWordDistributions);
				
				String filename = config.resultDir + "/" + config.IntraTopicSimilaritiesFilename;
				
				System.out.println("computing IntraTopicDistances.");
				
				//tools.IOUtils.writeDoubleMatrix(filename, dtmWDs.computeIntraTopicSimilarities());
				tools.IOUtils.writeIntMatrix(filename, dtmWDs.computeIntraTopicDistances(10));
				
				filename = config.resultDir + "/" + config.InterTopicSimilaritiesFilename;

				System.out.println("computing InterTopicDistances.");
				
				int timeStep = 0;
//				double[][] interTopipcSimiliarity = dtmWDs.computeInterTopicSimilarities(timeStep);
//				tools.IOUtils.writeDoubleMatrix(filename, interTopipcSimiliarity);
				
				int[][] interTopipcSimiliarity = dtmWDs.computeInterTopicDistances(timeStep, 10);
				tools.IOUtils.writeIntMatrix(filename, interTopipcSimiliarity);
				
				System.out.println("evaluateWordDistributions finished.");

			}
			break;
			case evaluateDTMTopics:
			{
				ReutersMetaData reutersData = new ReutersMetaData(config.resultDir + "/" + config.metaDataFilename);
				
				TopicDistributions ldaData = new TopicDistributions(config.resultDir + "/" + config.topicsPerDocFilename);

				WordDistributions wordData = new WordDistributions(config.dtmNumTimesteps,
						config.resultDir + "/" + config.dtmTopicWordDistributions);
				
				DTMEvaluator eva = new DTMEvaluator(reutersData, ldaData, wordData, config.dtmNumTimesteps);
				
				Vocabulary vocabulary = new Vocabulary(config.resultDir + "/" + config.vocabularyFilenameBase);
				
				double[][] topicSimilarities = wordData.computeIntraTopicSimilarities();

				float[][] timestepTopics = eva.computeTopicsWithDocsPerTime(true, 0.1f);
				
				eva.addTopics(topicSimilarities,
						config.similarityTreshold,
						config.numTopWords,
						vocabulary, 
						config.numTopWords,
						timestepTopics,
						config.resultDir + "/topicTopDocs3.txt",
						config.resultDir + "/topicTopWords3.txt",
						config.resultDir + "/topics3.txt");
				
				
				
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
				
				float[][] timestepTopics = reutersData.computeAnnotatedTopicsWithDocsPerTime(config.dtmNumTimesteps);
				
				tools.IOUtils.writeTimestepTopicsAsJason(config.resultDir + "/topics_a.js", topicNamesTimesteppd, timestepTopics);
				
				System.out.println("DTM topic evaluation finished.");
			}
			break;
			default:
				System.out.println("Invalid operation mode.");
				break;
		}
    }
}
