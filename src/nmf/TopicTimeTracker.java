package nmf;


import model.TopicRiver;

public class TopicTimeTracker {
	public static void main(String[] args) {
		TopicRiver tr = TopicRiver.loadTopicRiver("/media/Storage/Meine Daten/Schutzbereich/MoS/Research Project 2/TopicMiningProject/ressources/year Tue Aug 20 00:00:00 CEST 1996");
		System.out.println(tr);
    
    VisualizationDataConverter.writeJSONData(tr);
	}
	 

}
