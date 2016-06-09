package nmf;


import model.TopicRiver;

public class TopicTimeTracker {
	public static void main(String[] args) {
		TopicRiver tr = TopicRiver.loadTopicRiver("/media/Storage/Meine Daten/Schutzbereich/MoS/Research Project 2/savedata/temp");
		System.out.println(tr);
    
    VisualizationDataConverter.writeJSONData(tr);
	}
	 

}
