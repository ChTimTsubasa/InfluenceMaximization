package InfluMax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import dataAccess.FileOperator;
import dataStructure.Graph;
import dataStructure.GraphOperator;
import dataStructure.Message;
import dataStructure.MessageListOperator;
import dataStructure.MessageTypeCheck;
import dataStructure.NotOriginalMessage;
import dataStructure.ResultSet;
import jgibblda.LDA_InfluMax;
import jgibblda.Model;

public class EvaluationRepost {
	public static void main(String args[]){
		//prepare the data
		String 	rootdir		=	"sampleTest/";
		String 	sourceID	=	"1864252027/";
		String networkSize	=	"1100/";
		String repostFolder	=	"repost/";
		String modelDir		=	rootdir+sourceID+networkSize;
		String outputDir	=	modelDir+repostFolder;
		int seedUserNum		=	10;
					
		//load network, and messages, target messages
		Graph network=GraphOperator.getFromFile(modelDir+"sample.network");
		HashMap<String,Message>allStatusMap=MessageListOperator.readMessageFromFile(modelDir+"samplePreDoc.status");
		HashMap<String,Message>targetMessageMap=MessageListOperator.readMessageFromFile(modelDir+"targetMessages.status");
		Model ldaModel		=	LDA_InfluMax.getModel(modelDir, "model-final");
		Map<String, Integer> wordMap	=ldaModel.data.localDict.word2id;
		double[][]phi		=	ldaModel.phi;
		
		//calculate lambda for each message in the statusMap
		System.out.println("calculate lambda for each message in the statusMap");
		MessageListOperator.updateLambda(allStatusMap, phi, wordMap);
		
		//form a userID-HashSet<message> set
		System.out.println("forming the adj set");
		HashMap<String,HashSet<Message>> allMessageAdjSet=getMessageAdjSet(allStatusMap,network);
		
		//go through all targetMessage, find the users that have print them
		Iterator<Entry<String,Message>> iterTargetMessage=targetMessageMap.entrySet().iterator();
		Entry<String,Message>entryTargetMessage;
		while(iterTargetMessage.hasNext()){
			entryTargetMessage=iterTargetMessage.next();
			Message targetMessage=entryTargetMessage.getValue();
			String targetMessageID=entryTargetMessage.getKey();
			System.out.println("extracting targetMessage " + targetMessageID);
			//calculate the lambda of the TargetMessage
			double[]lambdaOfTarget=MessageListOperator.lambdaCalculation(targetMessage, phi, wordMap);
			targetMessage.setLambda(lambdaOfTarget);
			
			RepostExtractor re=new RepostExtractor(targetMessage, allMessageAdjSet, network);
			//extract the exact users of posting
			Graph exactRepostNework=re.getExactRepostUserNetwork();
			printNetworkOrUserToFile(outputDir,targetMessageID+"Exact",exactRepostNework,seedUserNum);
			for (double threshold=0.2; threshold>=0.05;threshold=threshold/2){
				Graph similarRepostNework=re.getSimilarRepostUserNetwork(threshold);
				printNetworkOrUserToFile(outputDir,targetMessageID+"Sim"+threshold,similarRepostNework,seedUserNum);
			}
		}
		
		
		System.out.println("end");	
	}
	
	/**
	 * 
	 * @param messageMap
	 * @param network 
	 * @return a hash set with userID as key and a hash set of the status this user has sent
	 */
	public static HashMap<String,HashSet<Message>> getMessageAdjSet(HashMap<String,Message> messageMap, Graph network){
		HashMap<String,HashSet<Message>> messageAdjSet=new HashMap<>();
		HashSet<Message> messageSetofAddedUser;
		//iterate the total message
		Iterator <Entry<String,Message>>iter=messageMap.entrySet().iterator();
		Entry<String,Message> entry;
		while(iter.hasNext()){
			entry=iter.next();
			Message message=entry.getValue();
			
			String userID=message.getUserID();
			
			//statused that is not posted by the user in the network should not be included.
			if(!network.containVertex(userID))
				continue;
			
			if(messageAdjSet.containsKey(userID)){
				messageSetofAddedUser=messageAdjSet.get(userID);
				messageSetofAddedUser.add(message);
			}
			else{
				messageSetofAddedUser=new HashSet<Message>();
				messageSetofAddedUser.add(message);
				messageAdjSet.put(userID, messageSetofAddedUser);
			}
			//for  unoriginals status, also include the original one it reposts
			if(!MessageTypeCheck.isOriginalStatus(message)){
				NotOriginalMessage nm=(NotOriginalMessage)message;
				Message originalMessage=messageMap.get(nm.getOriginalStatusID());
				if(!messageSetofAddedUser.contains(originalMessage)){
					messageSetofAddedUser.add(originalMessage);
				}
			}
		}
		return messageAdjSet;
		
	}
	
	/**
	 * print the network or the user to file
	 * @param outputDir
	 * @param targetMessageID
	 * @param g
	 * @param seedUserNum
	 */
	private static void printNetworkOrUserToFile(String outputDir,String targetMessageID,Graph g,int seedUserNum){
		if(g.getVertsNum()==0)
			return;
		if((g.getVertsNum()<=seedUserNum) | (g.getDirectedEdgeNum()==0)){
			g.writeFileWithNodesOnly(outputDir, "repostUser"+targetMessageID);
			return;
		}
		g.setConstantProbablity(0.001);
		g.writeFileWithProbability(outputDir, "repostNetwork"+targetMessageID, 4);
		
	}
}
