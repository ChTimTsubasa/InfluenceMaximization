package dataStructure;


public class Status implements Message {
	public String content;
	public String userID;
	public double lambda[];
	
	/**
	 * 
	 * @param content
	 * @param userID
	 */
	public Status(String content, String userID){
		this.content=content;
		this.userID=userID;
	}
	
	public String toString(){
		String content;
		content=this.content;
		content=content+" "+this.userID;
		return content;
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
	public void setUserID(String UserID) {
		this.userID=UserID;
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
