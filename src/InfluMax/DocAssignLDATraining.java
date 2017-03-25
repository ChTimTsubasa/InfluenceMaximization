package InfluMax;

import java.util.HashMap;

import dataStructure.Graph;
import dataStructure.GraphOperator;
import dataStructure.Message;
import dataStructure.MessageListOperator;
import jgibblda.LDA_InfluMax;
import jgibblda.Model;

public class DocAssignLDATraining {
	//process the two steps: Document assign to LDA training 
	public static void main(String args[]){
		//parameters that can be modified
		int topicNum=100;
		String sourceID="1864252027/";
		int subnetworkSize=1100;
		String dir="sampleTest/"+sourceID+subnetworkSize+"/";
		String fileName="ldaInput.txt";
		int iterationNum=1000;
		
		//read training data from file
		System.out.println("loading files");
		Graph network=GraphOperator.getFromFile(dir+"sample.network");
		HashMap<String,Message>trainingStatusMap=MessageListOperator.readMessageFromFile(dir+"trainingSet.status");
		HashMap<String,Message>commentMap=MessageListOperator.readMessageFromFile(dir+"samplePreDoc.comment");
		
		DocumentAssign.relateMessageToNetwork(network,trainingStatusMap,commentMap);
		DocumentAssign.optimizedAssign(network,dir,fileName);
		
		System.out.println("Training");	
		//training with Optimized assignment
		LDA_InfluMax.estimationFromScratch(50/((double)topicNum),0.01,topicNum,
				iterationNum,iterationNum/2,dir,fileName,50);
	}
	
}
