package postProcessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;

import preProcessing.ReutersXMLHandler;
import tools.IOUtils;

public class VisDataGenerator {

	final String corpusDir;
	
//	HashMap<Integer, String> liIndexToDoc;
//	
//	// topics per document
//	HashMap<Integer, ArrayList<Integer> > mLDATopicsPerDocument;
//	HashMap<Integer, ArrayList<String> > liDocsToOrigTopics;
//	
//	// documents per topic
////	HashMap<Integer, ArrayList<Integer> > liLDATopicsToDocs;
//	HashMap<Integer, HashMap<Integer, Double> > mLDADocumentsPerTopic;
//	HashMap<String, ArrayList<Integer> > liOrigTopicsToDocs;

	CorpusTopicDataObject data;

	LinkedHashMap<Integer, Integer> topicsByNumberOfDocs;

	private Integer indexCounter = 0;

	public VisDataGenerator(CorpusTopicDataObject data,
			final String corpusDir)
	{
		this.corpusDir = corpusDir;
		
		this.data = data;
		
		topicsByNumberOfDocs = new LinkedHashMap<Integer, Integer>();
		

		System.out.println("[VisDataGenerator] initialization done.");
		
	}
	
	public void writeTopicsWithDocWeightJson(final String topicWordsFilename, 
			final String topicClusterFilename, 
			final String filename)
	{
		HashMap<Integer, String> topicsToClusters = new HashMap<Integer, String>();
		if (Files.exists(Paths.get(topicClusterFilename))) 
		{
			try (Stream<String> lines = Files.lines(Paths.get(topicClusterFilename), Charset.defaultCharset()))
			{
				System.out.println("[VisDataGenerator::writeTopicsWithDocWeightJson] Loading clusters from " + topicClusterFilename);
				
				lines.forEachOrdered(line -> 
				{
					String[] parts = line.split(" ");
					topicsToClusters.put(Integer.parseInt(parts[0]), parts[1]);
				});
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("[VisDataGenerator::writeTopicsWithDocWeightJson] file does not exist: " + topicClusterFilename);
			return;
		}
		
		HashMap<Integer, String>  topicsToClustersSorted = sortByValue(topicsToClusters);
		
		final HashMap<String, ArrayList<Integer> > clustersToTopics = new HashMap<String, ArrayList<Integer> >();
		
		topicsToClustersSorted.forEach((topic, cluster) ->
		{
			if(clustersToTopics.containsKey(cluster))
			{
				clustersToTopics.get(cluster).add(topic);
			}
			else
			{
				ArrayList<Integer> topics = new ArrayList<Integer>();
				topics.add(topic);
				clustersToTopics.put(cluster, topics);
			}
//			
//			if (clustersToTopics.containsKey(cluster))
//			{
//				Integer num = clustersToTopics.get(cluster);
//				clustersToNumTopics.put(cluster, ++num);
//			}
//			else
//			{
//				clustersToNumTopics.put(cluster, 1);
//			}
		});
		
		
		// Get the words for the topics
		
		final HashMap<Integer, String> topicsToWords = new HashMap<Integer, String>();
		if (Files.exists(Paths.get(topicWordsFilename))) 
		{
			try (Stream<String> lines = Files.lines(Paths.get(topicWordsFilename), Charset.defaultCharset()))
			{
				System.out.println("[VisDataGenerator::writeTopicsWithDocWeightJson] Loading words from " + topicWordsFilename);
				
				synchronized(indexCounter)
				{
					indexCounter = 0;
				}
				
				lines.forEachOrdered(line -> 
				{
					topicsToWords.put(indexCounter, line);
					synchronized(indexCounter)
					{
						indexCounter++;
					}
				});
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("[VisDataGenerator::writeTopicsWithDocWeightJson] file does not exist: " + topicWordsFilename);
			return;
		}
		

		// Generate json for vis
		
		String content = "\"groups\": [\n";
		
		for (Entry<String, ArrayList<Integer>> clustersToTopicsEntry : clustersToTopics.entrySet()) 
		{
			String cluster = clustersToTopicsEntry.getKey();
			ArrayList<Integer> topics = clustersToTopicsEntry.getValue();

			LinkedHashMap<Integer, Integer> topicsByCluster = new LinkedHashMap<Integer, Integer>();
			Integer numDocsOverall = 0;
			for (Integer topic : topics) 
			{
				Integer numDocs = data.getLDADocumentsPerTopic().get(topic).size();
				numDocsOverall += numDocs;
				topicsByCluster.put(topic, numDocs);
			}
			
			topicsByCluster = sortByValue(topicsByCluster);

			String clusterContent = "";
			
			LinkedHashMap<String, Integer> mTopCLusterWords = new LinkedHashMap<String, Integer>();
			
			for (Entry<Integer, Integer> topicsByClusterEntry : topicsByCluster.entrySet()) 
			{
				Integer topic = topicsByClusterEntry.getKey();
				Integer numDocs = topicsByClusterEntry.getValue();

				String[] topicWords = topicsToWords.get(topic).split(" ");
				String wordRep = "<h4>Top words defining this topic:</h4><h5><p>";// topicsToWords.get(topic)
				for (String word : topicWords)
				{
					wordRep += word + "</p><p>";
					
					if (mTopCLusterWords.containsKey(word))
					{
						Integer count = mTopCLusterWords.get(word);
						mTopCLusterWords.put(word, ++count);
					}
					else
					{
						mTopCLusterWords.put(word, 1);
					}
				}
				wordRep += "</p></h5>";
						
				clusterContent += "\t{\n";
				clusterContent += "\t\t\"id\": " + topic + ",\n";
				clusterContent += "\t\t\"label\": \"" + topicWords[0] + "\",\n";
				//					content += "\t\t\"label\": \"topic " + topic + "\",\n";
				clusterContent += "\t\t\"full\": \"" + wordRep + "\",\n";
				clusterContent += "\t\t\"parentlabel\": \"" + cluster + "\",\n";
//				content += "\t\t\"weight\": " + numDocs + "\n";
//				content += "\t},\n";

				// list top documents for each topic

				clusterContent += "\t\t\"weight\": " + numDocs + ",\n";// comma here because groups are following now
				
				HashMap<Integer, Double> docs = data.getLDADocumentsPerTopic().get(topic);
				docs = sortByValue(docs);

				clusterContent += "\t\t\"groups\": [\n";

				int topDocs = 10;
				Iterator<Map.Entry<Integer, Double>> entries = docs.entrySet().iterator();
				while (topDocs > 0 && entries.hasNext())
				{
					Entry<Integer, Double> entry = entries.next();
					int id = entry.getKey();
					int weight = (int) Math.round(entry.getValue());
					String docName = data.getIndexToDoc().get(id);

					String docContent = getDocumentText(docName + ".xml");
					
					for (String word : topicWords)
					{
						boolean contains = false;
						for (String temp : topicWords)
						{
							if (!temp.equals(word) && temp.contains(word))
							{
								contains = true;
							}
						}
						if (!contains)
						{
							//<font size="3" color="red">This is some text!</font>
							docContent = docContent.replace(word, "<font color=ff0000>" + word + "</font>");
						}
					}
					
					clusterContent += "\t\t{\n";
					clusterContent += "\t\t\t\t\"id\": " + id + ",\n";
					clusterContent += "\t\t\t\"label\": \"" + docName + "\",\n";
					clusterContent += "\t\t\"full\": \"" + docContent + "\",";
					clusterContent += "\t\t\t\"parentlabel\": \"" + topicWords[0] + "\",\n";
					clusterContent += "\t\t\t\"weight\": " + weight + "\n";
					clusterContent += "\t\t},\n";

					--topDocs;
				}
				clusterContent += "]\n";
				clusterContent += "\t},\n";

			}
			
			// Do not mess the order if there was only one topic
			if( mTopCLusterWords.size() > 1)
			{
				mTopCLusterWords = sortByValue(mTopCLusterWords);
			}
			
			Object[] aTopCLusterWords = mTopCLusterWords.keySet().toArray();
					
			String clusterRep = "<h4>Top words defining this cluster:</h4><h5><p>";// topicsToWords.get(topic)
			for (int i =0; i < 20 && i < aTopCLusterWords.length; ++i)
			{
				clusterRep += (String)(aTopCLusterWords[i]) + "</p><p>";
			}
			clusterRep += "</p></h5>";
			
			content += "{\n";
			content += "\t\"id\": " + (cluster.split("r")[1] + 666) + ",\n"; // id conflict with topics !
			content += "\t\"label\": \"" + aTopCLusterWords[0] + "\",\n";//cluster
			content += "\t\"full\": \"" + clusterRep + "\",";
			content += "\t\"weight\": " + topics.size() + ",\n";
			content += "\t\"groups\": [\n";
			content += clusterContent;
			content += "]\n";
			content += "\t},\n";

		}
		content += "]\n";
		
		System.out.println("[VisDataGenerator::writeTopicsWithDocWeightJson] Saving topics with number of docs to " + filename);
		IOUtils.saveContentToFile(content, filename);

//		PrintWriter writer;
//		try 
//		{
//
//			writer = new PrintWriter(filename, "UTF-8");
//			
//			writer.print(content);
//			
//			writer.println("\"groups\": [");
//			
//		
//			clustersToTopics.forEach((cluster, topics) ->
//			{
//				writer.println("{");
//				writer.println("\t\"id\": " + (cluster.split("r")[1] + 666) + ",");
//				writer.println("\t\"label\": \"" + cluster + "\",");
//				writer.println("\t\"weight\": " + topics.size() + ",");
//				writer.println("\t\"groups\": [");
//				
//				LinkedHashMap<Integer, Integer> topicsByCluster = new LinkedHashMap<Integer, Integer>();
//				Integer numDocsOverall = 0;
//				for (Integer topic : topics) 
//				{
//					Integer numDocs = mLDADocumentsPerTopic.get(topic).size();
//					numDocsOverall += numDocs;
//					topicsByCluster.put(topic, numDocs);
//				}
//				
//				topicsByCluster = sortByValue(topicsByCluster);
//
//				topicsByCluster.forEach((topic, numDocs) ->
//				{
//
//					String topWord = topicsToWords.get(topic).split(" ")[0];
//					writer.println("\t{");
//					writer.println("\t\t\"id\": " + topic + ",");
//					writer.println("\t\t\"label\": \"" + topWord + "\",");
//					//					writer.println("\t\t\"label\": \"topic " + topic + "\",");
//					writer.println("\t\t\"full\": \"" + topicsToWords.get(topic) + "\",");
//					writer.println("\t\t\"parentlabel\": \"" + cluster + "\",");
////					writer.println("\t\t\"weight\": " + numDocs);
////					writer.println("\t},");
//
//					// list top documents for each topic
//
//					writer.println("\t\t\"weight\": " + numDocs + ",");// comma here because groups are following now
//					
//					HashMap<Integer, Double> docs = mLDADocumentsPerTopic.get(topic);
//					docs = sortByValue(docs);
//
//					writer.println("\t\t\"groups\": [");
//
//					int topDocs = 1;
//					Iterator<Map.Entry<Integer, Double>> entries = docs.entrySet().iterator();
//					while (topDocs > 0 && entries.hasNext())
//					{
//						Entry<Integer, Double> entry = entries.next();
//						int id = entry.getKey();
//						int weight = (int) Math.round(entry.getValue());
//						String docName = liIndexToDoc.get(id);
//
//						String content = getDocumentText(docName + ".xml");
//						
//						writer.println("\t\t{");
//						writer.println("\t\t\t\t\"id\": " + id + ",");
//						writer.println("\t\t\t\"label\": \"" + docName + "\",");
//						writer.println("\t\t\"full\": \"" + content + "\",");
//						writer.println("\t\t\t\"parentlabel\": \"" + topWord + "\",");
//						writer.println("\t\t\t\"weight\": " + weight);
//						writer.println("\t\t},");
//
//						--topDocs;
//					}
//					writer.println("]");
//					writer.println("\t},");
//
//				});
//				writer.println("]");
//				writer.println("\t},");
//
//			});
//			writer.println("]");
//			
//			writer.close();
//		} 
//		catch (FileNotFoundException | UnsupportedEncodingException e)
//		{
//			e.printStackTrace();
//		}
		
	}
	
	public void writeTopicsWithDocWeightJson2(final String filename)
	{
//		liLDATopicsToDocs.forEach((temp, docs) -> {
//			topicsByNumberOfDocs.put(temp, docs.size());
//		});
		
		Integer numDocsOverall = 0;
		for (Entry<Integer, HashMap<Integer, Double>> entry : data.getLDADocumentsPerTopic().entrySet()) 
		{
			numDocsOverall += entry.getValue().size();
			topicsByNumberOfDocs.put(entry.getKey(), entry.getValue().size());
		}
		
		topicsByNumberOfDocs = sortByValue(topicsByNumberOfDocs);
		
		PrintWriter writer;
		try 
		{
			System.out.println("[VisDataGenerator::writeTopicsWithDocWeightJson] Saving topics with number of docs to " + filename);

			writer = new PrintWriter(filename, "UTF-8");
			
			writer.println("\"groups\": [");
			
			topicsByNumberOfDocs.forEach((topic, numDocs) ->{
				writer.println("{");
				writer.println("\t\"id\": " + topic + ",");
				writer.println("\t\"label\": \"topic " + topic + "\",");
				writer.println("\t\"weight\": " + numDocs);
				writer.println("},");
			});
			writer.println("]");
			
			writer.close();
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> 
    sortByValue( Map<K, V> map )
	{
		LinkedHashMap<K, V> result = new LinkedHashMap<>();
	    Stream<Map.Entry<K, V>> st = map.entrySet().stream();
	
	    st.sorted( Map.Entry.comparingByValue(Comparator.reverseOrder()) )
	        .forEachOrdered( e -> result.put(e.getKey(), e.getValue()) );
	
	    return result;
	}
	
	public String getDocumentText(final String fileName)
	{
		String text = "";
		
		//Files.walk(path).collect(toList()).parallelStream()
		try (Stream<Path> paths = Files.walk(Paths.get(corpusDir))) 
		{
			Optional<Path> file = paths
				.filter(p -> !p.toFile().isDirectory() && p.toFile().getAbsolutePath().endsWith(fileName)).findFirst();
			
			if (file.isPresent())
			{
				text = ReutersXMLHandler.readXMLDocumentText(file.get(), true);
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return text;
	}
}
