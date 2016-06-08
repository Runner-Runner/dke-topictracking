package nmf;


import model.TopicRiver;

public class TopicTimeTracker {
	public static void main(String[] args) {
		TopicRiver tr = TopicRiver.loadTopicRiver("directory");
		System.out.println(tr);
	}
	 

}
