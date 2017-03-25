package InfluMax;

public class ResultSet {
	private String messageID;
	private double precision;
	private double recall;
	private double fMeasure;
	private int truePositive;
	private int falsePositive;
	private int falseNegative;
	
	boolean isSelectNormalized;
	int	selectType;
	boolean isActualExact;
	double  actualSimilarity;
	
	
	public ResultSet(SelectedSeedUser sSeeds, ActualSeedUser aSeeds){
		
		this.messageID=sSeeds.messageID;
		if(this.messageID.equals("constant")){
			this.messageID=aSeeds.messageID;
		}
		this.isSelectNormalized	=	sSeeds.isNormalized;
		this.selectType			=	sSeeds.type;
		this.isActualExact		=	aSeeds.isExact;
		this.actualSimilarity	=	aSeeds.similarity;

		String[]sUserIDs=sSeeds.userID;
		String[]aUserIDs=aSeeds.userID;		
		//System.out.println(aUserIDs.length);

		this.countMatch(sUserIDs,aUserIDs);
	}
	
	public void changeSeedNumber(SelectedSeedUser sSeeds, ActualSeedUser aSeeds,int seedUserNum){
		String[]sUserIDs=new String[seedUserNum];
		String[]aUserIDs=aSeeds.userID;
		//System.out.println(aUserIDs.length);
		for(int i=0;i<seedUserNum;i++){
			sUserIDs[i]=sSeeds.userID[i];
			//aUserIDs[i]=aSeeds.userID[i];	
		}
		this.countMatch(sUserIDs,aUserIDs);
	}
	
	private void countMatch(String[]sUserIDs, String[]aUserIDs){
		this.truePositive=0;
		this.falsePositive=0;
		this.falseNegative=0;
		for(String sUserID:sUserIDs){
			for(String aUserID:aUserIDs){
				if(aUserID.equals(sUserID)){
					truePositive++;
				}
			}
		}
		
		falsePositive=sUserIDs.length-truePositive;
		falseNegative=aUserIDs.length-truePositive;
		this.calPrecision();
		this.calRecall();
		this.calFMeasure();
	}
	
	
	
	
	private void calPrecision(){
		if(this.truePositive+this.falsePositive==0){
			this.precision=0;
			return;
		}
		this.precision=((double)this.truePositive)/((double)(this.truePositive+this.falsePositive));
	}
	
	private void calRecall(){
		if(this.truePositive+this.falseNegative==0){
			this.recall=0;
			return;
		}
		this.recall=((double)this.truePositive)/((double)(this.truePositive+this.falseNegative));
		
	}
	
	private void calFMeasure(){
		if(this.precision==0&this.recall==0){
			this.fMeasure=0;
			return;
		}
		this.fMeasure=2*this.precision*this.recall/(this.precision+this.recall);
	}

	public String getMessageID() {
		return messageID;
	}
	
	public double getPrecision(){
		return this.precision;
	}
	
	public double getRecall(){
		return this.recall;
	}
	
	public double getFMeasure(){
		return this.fMeasure;
	}
	
	public double reCalFMesure(double a){
		double scale=Math.pow(a, 2);
		this.fMeasure=(scale+1)*this.precision*this.recall/(scale*this.precision+this.recall);
		
		return this.fMeasure;
	}
	

	@Override
	public String toString(){

		int tfSN;
		int tfAE;
		if(isSelectNormalized)
			tfSN=1;
		else
			tfSN=0;
		
		if(isActualExact)
			tfAE=1;
		else
			tfAE=0;
		
		String sp=" ";
		String s = messageID+sp+
				selectType+sp+
				tfSN+sp+
				actualSimilarity+sp+
				tfAE+sp+
				precision+sp+
				recall+sp+
				fMeasure+sp+
				truePositive+sp+
				falsePositive+sp+
				falseNegative;
				
		return s;
	}
}
