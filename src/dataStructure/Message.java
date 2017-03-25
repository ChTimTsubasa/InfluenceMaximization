package dataStructure;

public interface Message {
	public String getContent();
	public String getUserID();
	public void setContent(String content);
	public void setUserID(String userID); 
	public void setLambda(double[] lambda);
	public double[] getLambda();
	public double similarityTo(Message anotherMessage);
}
