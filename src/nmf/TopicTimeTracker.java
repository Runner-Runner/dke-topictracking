package nmf;


import model.TopicRiver;

public class TopicTimeTracker {
	public static void main(String[] args) {
		TopicRiver tr = TopicRiver.loadTopicRiver("/home/carsten/workspace/dke-topictracking/days");
		System.out.println(tr);
    
//    TopicGeneralizer.generalize(tr, 0);
    
		VisualizationDataConverter.writeJSONData(tr);
		VisualizationDataConverter.writeCSVData(tr, "test.csv");
	}
	 

}
