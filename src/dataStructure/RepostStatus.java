package dataStructure;

public class RepostStatus extends Status implements Message, NotOriginalMessage {
	public String originalStatusID;
	public RepostStatus(String content, String userID,String originalStatusID) {
		super(content, userID);
		this.originalStatusID=originalStatusID;
	}
	
	public String toString(){
		String content;
		content=super.toString();
		content+=" "+this.originalStatusID;
		return content;
	}

	@Override
	public String getOriginalStatusID() {
		return this.originalStatusID;
	}
	

}
