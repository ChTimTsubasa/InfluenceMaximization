package InfluMax;

public class ActualSeedUser extends SeedUser {
	boolean isExact;
	double  similarity;
	public ActualSeedUser(String messageID, String[]userID,boolean isExact,double  similarity){
		this.messageID=messageID;
		this.userID=userID;
		this.isExact = isExact;
		if(!isExact)
			this.similarity=similarity;
		else
			this.similarity=1000;
	}
	
	
}
