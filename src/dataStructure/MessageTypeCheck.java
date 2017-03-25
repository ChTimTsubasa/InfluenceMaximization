package dataStructure;

public class MessageTypeCheck {
	public static Class checkMessageType(Message m){
		return m.getClass();
	}
	public static boolean isRetweetedStatus(Message m){
		return m.getClass().equals(RepostStatus.class);
	}
	public static boolean isComment(Message m){
		return m.getClass().equals(Comment.class);
	}
	public static boolean isOriginalStatus(Message m){
		return m.getClass().equals(Status.class);
	}
}
