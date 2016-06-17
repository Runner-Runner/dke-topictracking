package nmf;


import model.TopicRiver;

public class TopicTimeTracker {
	public static void main(String[] args) {
		TopicRiver tr = TopicRiver.loadTopicRiver("/mnt/dualuse/sharedData/Git-Projects/dke-topictracking/year");
		System.out.println(tr);
    
//    TopicGeneralizer.generalize(tr, 0);
    
    VisualizationDataConverter.writeJSONData(tr);
	}
	 

}
