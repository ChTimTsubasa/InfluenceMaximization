package InfluMax;

public class SelectedSeedUser extends SeedUser {
	boolean isNormalized;
	int type;
	final static int	ORIGINAL=0;
	final static int	SIMILARITY=1;
	final static int	VITALITY=2;
	final static int	SIMANDVIT=3;
	final static int	CONSTANT=4;
	public SelectedSeedUser(String messageID, String[]userID,boolean isNormalized,int type){
		this.messageID=messageID;
		this.userID=userID;
		this.isNormalized = isNormalized;
		this.type=type;
	}
}
