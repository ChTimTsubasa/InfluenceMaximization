package InfluMax;

import java.util.ArrayList;
import java.util.HashMap;

import dataAccess.FileOperator;
import dataStructure.Graph;
import dataStructure.GraphOperator;
import dataStructure.Message;
import dataStructure.MessageListOperator;
import jgibblda.LDA_InfluMax;
import jgibblda.Model;

public class DocAssignLDATrainingCompare {
	//process the two steps: Document assign to LDA training 
	public static void main(String args[]){
		int topicNum=20;
		String sourceID="1864252027/";
		int subnetworkSize=100;
		ArrayList<String>fileLines=new ArrayList();
		
		//assign the edge documents to LDA input
		for(subnetworkSize=0;subnetworkSize<=3300;subnetworkSize+=200){
			System.out.println("assigning network with size "+subnetworkSize);
			String dir="sampleTest/"+sourceID+subnetworkSize+"/";
			
			//read training data from file
			Graph network=GraphOperator.getFromFile(dir+"sample.network");
			HashMap<String,Message>trainingStatusMap=MessageListOperator.readMessageFromFile(dir+"trainingSet.status");
			HashMap<String,Message>commentMap=MessageListOperator.readMessageFromFile(dir+"samplePreDoc.comment");
			
			DocumentAssign.relateMessageToNetwork(network,trainingStatusMap,commentMap);
			DocumentAssign.optimizedAssign(network,dir,"ldaInput.txt");
			//DocumentAssign.originalAssign(network,dir+"ldaInputOR.txt");	
		}
		
		//train the data respectively
		for(subnetworkSize=100;subnetworkSize<=3300;subnetworkSize+=200){
			System.out.println("\n\n\ntraining network with size "+subnetworkSize);
			String dir="sampleTest/"+sourceID+subnetworkSize+"/";
			String line=""+subnetworkSize+" ";
			
			//training with original assignment
			long startTime=System.currentTimeMillis();   //start time
			Model m= LDA_InfluMax.estimationFromScratch(50/((double)topicNum),0.01,topicNum,
					100,50,dir,"ldaInputOR.txt",50);
			long endTime=System.currentTimeMillis(); //end time
			System.out.println("runtime in ms: "+(endTime-startTime)+"ms");
			System.out.println("inputSize: "+m.M);
			line +=(endTime-startTime)+" "+m.M+" ";
			
			//training with Optimized assignment
			startTime=System.currentTimeMillis();   //start time
			m= LDA_InfluMax.estimationFromScratch(50/((double)topicNum),0.01,topicNum,
					100,50,dir,"ldaInput.txt",50);
			endTime=System.currentTimeMillis(); //end time
			System.out.println("runtime in ms: "+(endTime-startTime)+"ms");
			System.out.println("inputSize: "+m.M);
			line +=(endTime-startTime)+" "+m.M;
			
			fileLines.add(line);
		}
		
		FileOperator.writeFile("sampleTest/"+sourceID+"ldaTrainingCompare.txt", fileLines);
		
	}
	
}
