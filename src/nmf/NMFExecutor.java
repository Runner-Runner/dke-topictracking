package nmf;

import experiments.WordCounter;
import la.matrix.Matrix;
import ml.clustering.Clustering;
import ml.clustering.KMeans;
import ml.clustering.NMF;
import ml.options.KMeansOptions;
import ml.options.NMFOptions;

public class NMFExecutor
{
  private WordCounter wordCounter;
  private int topicCount;
  private TopicData topicData;

  public NMFExecutor(WordCounter wordCounter, int topicCount)
  {
    this.wordCounter = wordCounter;
    this.topicCount = topicCount;
  }

  public void execute()
  {
    double[][] documentTermMatrix = this.wordCounter.getDocumentTermMatrix();

    KMeansOptions options = new KMeansOptions();
    options.nClus = topicCount;
    options.verbose = true;
    options.maxIter = 100;
    KMeans kmeans = new KMeans(options);
    kmeans.feedData(documentTermMatrix);
    kmeans.initialize(null);
    kmeans.clustering();
    Matrix indicatorMatrix = kmeans.getIndicatorMatrix();
    NMFOptions nmfOptions = new NMFOptions();
    nmfOptions.nClus = topicCount;
    nmfOptions.maxIter = 50;
    nmfOptions.verbose = true;
    nmfOptions.calc_OV = false;
//    nmfOptions.epsilon = 1e-5;
    nmfOptions.epsilon = 1e-4;
    Clustering nmf = new NMF(nmfOptions);
//    L1NMFOptions l1nmfOptions = new L1NMFOptions();
//    l1nmfOptions.nClus = 10;
//    l1nmfOptions.gamma = 1 * 0.0001;
//    l1nmfOptions.mu = 1 * 0.1;
//    l1nmfOptions.maxIter = 50;
//    l1nmfOptions.verbose = true;
//    l1nmfOptions.calc_OV = !true;
//    l1nmfOptions.epsilon = 1e-5;
//    Clustering nmf = new L1NMF(l1nmfOptions);

    nmf.feedData(documentTermMatrix);
    // NMF.initialize(null); 
    nmf.clustering(indicatorMatrix);
    
    Matrix topicTerm = nmf.getCenters();
    Matrix topicDocument = nmf.getIndicatorMatrix();
    
    topicData = new TopicData(topicTerm, topicDocument, 
            wordCounter.getVocabulary().keySet(), wordCounter.getDocumentNames());
    
    topicData.getTopics();
    
    System.out.println(topicData);
    
    // If null, KMeans will be used for initialization 
//    System.out.println("Basis Matrix:");
//    printMatrix(full(topicTerm));
//    System.out.println("Indicator Matrix:");
//    printMatrix(full(topicDocument));
  }
}
