package InfluMax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import dataAccess.FileOperator;
import dataStructure.Edge;
import dataStructure.Graph;
import dataStructure.GraphOperator;
import dataStructure.Message;
import dataStructure.MessageListOperator;
import dataStructure.Vertex;
import jgibblda.LDA_InfluMax;
import jgibblda.Model;

public class ProbabilityMapping {
	
	public static void main(String args[]){
		String sourceID="1864252027/";
		String subnetworkSize="1100/";
		String dir="sampleTest/"+sourceID+subnetworkSize;
		String dirOut=dir+"input/";
		double speedUp=0.2;
		
		Model model=LDA_InfluMax.getModel(dir, "model-final");
		Graph network=GraphOperator.getFromFile(dir+"sample.network");
		HashMap<String,Message>targetStatusMap	=MessageListOperator.readMessageFromFile(dir+"targetMessages.status");
		HashMap<String,Message>allMessageMap	=MessageListOperator.readMessageFromFile(dir+"samplePreDoc.status");
		HashMap<String,Message>commentMap		=MessageListOperator.readMessageFromFile(dir+"samplePreDoc.comment");
		

		//calculate the vitality of the network users
		updateVitality(network, allMessageMap, commentMap);

		//set constant propagation probability with 0.5
		network.setConstantProbablity(0.5*speedUp);

		network.writeFileWithProbability(dirOut, "constant",4);
		//start mapping
		Iterator<String> iter=targetStatusMap.keySet().iterator();
		while(iter.hasNext()){
			String targetMessageID=iter.next();
			Message targetStatus=targetStatusMap.get(targetMessageID);
			String content=targetStatus.getContent();
			
			//map the probability with the LDA model
			probabilityMapping(network, model, targetStatus, dir);
			
			
			//write the network with probability
			network.writeFileWithProbabilityAll(dirOut, targetMessageID+"pGraph");
			
			//normalize the network probability and write it to file
			network.normalizeProbability(speedUp);
			network.writeFileWithProbabilityAll(dirOut, targetMessageID+"pGraphNormal");
			
		}
					
		System.out.println("finish");
	}
	
	
	/**
	 * calculate the propagation probability of the network
	 * @param network
	 * @param ldaModel
	 * @param targetStatus
	 * @param dir
	 */
	public static void probabilityMapping(Graph network,Model ldaModel,Message targetStatus,String dir){
		int topicNum=ldaModel.K;
		int docNum	=ldaModel.M;
		int wordNum	=ldaModel.V;
		int edgeNum	=network.getDirectedEdgeNum();
		double[][]theta=ldaModel.theta;
		double[][]edgeTheta=new double[edgeNum][topicNum];
		double[][]phi=ldaModel.phi;
		double[] lambda;
		int []edgeToDoc=FileOperator.readArrayFromFile(dir+"edgeToDocMap.txt");
		
		Map<String,Integer> wordMap=ldaModel.data.localDict.word2id;
		
		Map<Integer,String>id2word=ldaModel.data.localDict.id2word;
		
		//relate edgeTheta to theta
		for(int edgeIndex=0;edgeIndex<edgeNum;edgeIndex++){
			for(int topicIndex=0;topicIndex<topicNum;topicIndex++){
				edgeTheta[edgeIndex][topicIndex]=theta[edgeToDoc[edgeIndex]] [topicIndex];
			}
		}
		
		lambda=MessageListOperator.lambdaCalculation(targetStatus,phi,wordMap);
			

		mappingOriginal(network,edgeTheta,lambda);
		mappingWithSimilarity(network,edgeTheta,lambda);
		mappingWithActivity(network,edgeTheta,lambda);
		mappingWithSandA(network,edgeTheta,lambda);

	}


	private static void mappingOriginal(Graph network, double[][] theta, double[] lambda) {
		int edgeNum=network.getDirectedEdgeNum();
		int docNum=theta.length;
		int topicNum=theta[0].length;
		if(!GraphOperator.updateTheta(network, theta)){
			System.out.println("edge number and theta not match");
			return;
		}
		Edge[] edgeArray=network.getAllEdge();
		if(edgeArray.length!=docNum){
			System.out.println("edge number and theta not match");
			return;
		}
		for(int edgeIndex=0; edgeIndex<edgeNum;edgeIndex++){
			edgeArray[edgeIndex].probability[0]=0;
			for(int topicIndex=0;topicIndex<topicNum;topicIndex++){
				edgeArray[edgeIndex].probability[0]+=theta[edgeIndex][topicIndex]*lambda[topicIndex];
			}
		}
	}
	private static void mappingWithSimilarity(Graph network, double[][] theta, double[] lambda) {
		int edgeNum=network.getDirectedEdgeNum();
		int docNum=theta.length;
		int topicNum=theta[0].length;
		
		if(!GraphOperator.updateTheta(network, theta)){
			System.out.println("edge number and theta not match");
			return;
		}
		
		Iterator<String> iter=network.getVertexHashMap().keySet().iterator();
		while(iter.hasNext()){
			String userID=iter.next();
			Vertex v=network.getVertex(userID);
			Edge e=v.firstOutEdge;
			
			while(e!=null){
				Vertex pointVertex=network.getVertex(e.getOutID());
				double pointVertexThetaAvr[]=pointVertex.averageOutTheta;
				e.probability[1]=0;
				
				for(int topicIndex=0;topicIndex<topicNum;topicIndex++){
					e.probability[1]+=Math.sqrt(e.theta[topicIndex]*pointVertexThetaAvr[topicIndex]) * lambda[topicIndex];
				}
				e=e.nextOutEdge;
			}
		}
	}
	private static void mappingWithActivity(Graph network, double[][] theta, double[] lambda) {
		int edgeNum=network.getDirectedEdgeNum();
		int docNum=theta.length;
		int topicNum=theta[0].length;
		if(!GraphOperator.updateTheta(network, theta)){
			System.out.println("edge number and theta not match");
			return;
		}
		Iterator<String> iter=network.getVertexHashMap().keySet().iterator();
		while(iter.hasNext()){
			String userID=iter.next();
			Vertex v=network.getVertex(userID);
			Edge e=v.firstOutEdge;
			
			while(e!=null){
				Vertex pointVertex=network.getVertex(e.getOutID());
				e.probability[2]=e.probability[0]*pointVertex.vitality;
				e=e.nextOutEdge;
			}
		}
		
	}
	private static void mappingWithSandA(Graph network, double[][] theta, double[] lambda){
		int edgeNum=network.getDirectedEdgeNum();
		int docNum=theta.length;
		int topicNum=theta[0].length;

		if(!GraphOperator.updateTheta(network, theta)){
			System.out.println("edge number and theta not match");
			return;
		}

		Iterator<String> iter=network.getVertexHashMap().keySet().iterator();
		while(iter.hasNext()){
			String userID=iter.next();
			Vertex v=network.getVertex(userID);
			Edge e=v.firstOutEdge;
			
			while(e!=null){
				Vertex pointVertex=network.getVertex(e.getOutID());
				e.probability[3]=e.probability[1]*pointVertex.vitality;
				e=e.nextOutEdge;
			}
		}
	}
	
	/**
	 * update the vitality of each node in network
	 * @param network
	 * @param statusMap
	 * @param commentMap
	 */
	public static void updateVitality(Graph network, HashMap<String,Message>statusMap,HashMap<String,Message>commentMap){
		Message messageArray[]=new Message[statusMap.size()+commentMap.size()];
		int messageIndex=0;
		Iterator<Entry<String,Message>> messageIter=statusMap.entrySet().iterator();
		Entry<String,Message>entry;
		while(messageIter.hasNext()){
			entry=messageIter.next();
			Message m=entry.getValue();
			messageArray[messageIndex]=m;
			messageIndex++;
		}
		messageIter=commentMap.entrySet().iterator();
		while(messageIter.hasNext()){
			entry=messageIter.next();
			Message m=entry.getValue();
			messageArray[messageIndex]=m;
			messageIndex++;
		}
		GraphOperator.updateVitality(network, messageArray);
	}
	
	
	private static void printInfo(double[] ds) {
		int all=ds.length;
		double sum=0;
		double exp=0;
		double var=0;
		for(int i=0;i<all;i++){
			sum+=ds[i];
		}
		exp=sum/all;
		
		for(int i=0;i<all;i++){
			var+=Math.pow(ds[i]-exp, 2);
		}
		var=var/all;
		
		System.out.println("exp is "+exp);

		System.out.println("var is "+var);
	}
}
