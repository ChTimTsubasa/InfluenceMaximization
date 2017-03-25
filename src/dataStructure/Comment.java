package dataStructure;

public class Comment implements Message, NotOriginalMessage{
	public String content;
	public String userID;
	public String originalStatusID;
	private double[] lambda;
	
	public Comment(String content, String userID,String originalStatusID){
		this.content=content;
		this.userID=userID;
		this.originalStatusID=originalStatusID;
	}

	@Override
	public String getContent() {
		
		return this.content;
	}

	@Override
	public String getUserID() {
		return this.userID;
	}

	@Override
	public void setContent(String content) {
		this.content=content;
		
	}

	@Override
	public void setUserID(String userID) {
		this.userID=userID;
	}

	@Override
	public String getOriginalStatusID() {
		return this.originalStatusID;
	}

	@Override
	public void setLambda(double[] lambda) {
		this.lambda=lambda;
		
	}

	@Override
	public double[] getLambda() {
		
		return this.lambda;
	}

	@Override
	public double similarityTo(Message anotherMessage) {
		double[]lambdaOfMessage=this.lambda;
		double[]lambdaOfTarget=anotherMessage.getLambda();
		int topicNum=lambdaOfMessage.length;
		double similarity=0;
		for(int i=0;i<topicNum;i++){
			similarity+=lambdaOfMessage[i]*lambdaOfTarget[i];
		}
		return similarity;
	}
}
