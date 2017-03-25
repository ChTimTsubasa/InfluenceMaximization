package InfluMax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dataAccess.FileOperator;
import dataStructure.Graph;
import dataStructure.GraphOperator;
import dataStructure.Message;
import dataStructure.MessageListOperator;
import jgibblda.LDA_InfluMax;
import jgibblda.Model;

public class EvaluateHR {
	public static void main(String args[]){
		String 	rootdir		=	"sampleTest/";
		String 	sourceID	=	"1864252027/";
		String networkSize	=	"1100/";
		String repostFolder	=	"repost/";
		String resultFolder	=	"result/";
		String modelDir		=	rootdir+sourceID+networkSize;
		String repostDir	=	modelDir+repostFolder;
		String resultDir	=	modelDir+resultFolder;
					
		//load network, and messages, target messages
		Graph network=GraphOperator.getFromFile(modelDir+"sample.network");
		HashMap<String,Message>allStatusMap=MessageListOperator.readMessageFromFile(modelDir+"samplePreDoc.status");
		HashMap<String,Message>targetMessageMap=MessageListOperator.readMessageFromFile(modelDir+"targetMessages.status");
		Model ldaModel		=	LDA_InfluMax.getModel(modelDir, "model-final");
		Map<String, Integer> wordMap	=ldaModel.data.localDict.word2id;
		double[][]phi		=	ldaModel.phi;
		
		System.out.println("calculate lambda for each message in the statusMap");
		MessageListOperator.updateLambda(allStatusMap, phi, wordMap);
		HashMap<String,HashSet<Message>> allMessageAdjSet=EvaluationRepost.getMessageAdjSet(allStatusMap,network);
		
		NodeFileOperator nfo=	new NodeFileOperator(modelDir,0.2,0.05,2);
		HashMap<String ,List<SelectedSeedUser>> selectedSeedUser=nfo.getSelectSeedUsers();
		

		ArrayList<String> fileLines=new ArrayList<>();
		Iterator<String> iterM = targetMessageMap.keySet().iterator();
		while(iterM.hasNext()){
			String messageID=iterM.next();
			
			Message targetMessage=allStatusMap.get(messageID);
			List<SelectedSeedUser>selectList=selectedSeedUser.get(messageID);
			
			for(SelectedSeedUser select:selectList){
				List<String> actualUserID=new LinkedList<String>();
				ActualSeedUser actualSeedUser;
				ResultSet rs;
				//exact
				for(String user:select.userID){
					if(!allMessageAdjSet.containsKey(user))
					continue;
					if(!allMessageAdjSet.containsKey(user))
						continue;
					HashSet<Message> messagesPost=allMessageAdjSet.get(user);
					if(messagesPost.contains(targetMessage))
						actualUserID.add(user);	
				}
				actualSeedUser=new ActualSeedUser(messageID,actualUserID.toArray(new String[actualUserID.size()]),true,1000);
				rs=new ResultSet(select,actualSeedUser);
				rs.changeSeedNumber(select, actualSeedUser, 5);
				fileLines.add(rs.toString());
				
				//similar
				for(double threshold=0.2; threshold>=0.05;threshold=threshold/2){
					actualUserID=new LinkedList<String>();
					for(String user:select.userID){
						if(!allMessageAdjSet.containsKey(user))
							continue;
						
						HashSet<Message> messagePost=allMessageAdjSet.get(user);
						for(Message message:messagePost)
							if(message.similarityTo(targetMessage)>=threshold){
								actualUserID.add(user);	
								break;
							}
						
					}//for users
					actualSeedUser=new ActualSeedUser(messageID,actualUserID.toArray(new String[actualUserID.size()]),false,threshold);
					rs=new ResultSet(select,actualSeedUser);
					rs.changeSeedNumber(select, actualSeedUser, 5);
					fileLines.add(rs.toString());
				}//for threshold
			}//for each selected cases
		}//for each target message
		
		
		
		//constant
		List<SelectedSeedUser>selectList=selectedSeedUser.get("constant");
		iterM = targetMessageMap.keySet().iterator();
		while(iterM.hasNext()){
			//find exact
			String messageID=iterM.next();
			
			Message targetMessage=allStatusMap.get(messageID);
			
			for(SelectedSeedUser select:selectList){
				List<String> actualUserID=new LinkedList<String>();
				ActualSeedUser actualSeedUser;
				ResultSet rs;
				//exact
				
				for(String user:select.userID){if(!allMessageAdjSet.containsKey(user))
					continue;
					if(!allMessageAdjSet.containsKey(user))
						continue;
					HashSet<Message> messagesPost=allMessageAdjSet.get(user);
					if(messagesPost.contains(targetMessage))
						actualUserID.add(user);	
				}
				actualSeedUser=new ActualSeedUser(messageID,actualUserID.toArray(new String[actualUserID.size()]),true,1000);
				rs=new ResultSet(select,actualSeedUser);
				rs.changeSeedNumber(select, actualSeedUser, 5);
				fileLines.add(rs.toString());
				
				//similar
				for(double threshold=0.2; threshold>=0.05;threshold=threshold/2){
					actualUserID=new LinkedList<String>();
		
					for(String user:select.userID){
						if(!allMessageAdjSet.containsKey(user))
							continue;
						
						HashSet<Message> messagePost=allMessageAdjSet.get(user);
						for(Message message:messagePost)
							if(message.similarityTo(targetMessage)>=threshold){
								actualUserID.add(user);	
								break;
							}
					}//for users
					actualSeedUser=new ActualSeedUser(messageID,actualUserID.toArray(new String[actualUserID.size()]),false,threshold);
					rs=new ResultSet(select,actualSeedUser);
					rs.changeSeedNumber(select, actualSeedUser, 5);
					fileLines.add(rs.toString());
				}//for threshold
			}//for each selected cases
		}//for each target message
		FileOperator.writeFile(resultDir+"hit_ratio_5.result", fileLines);
	
	}
			
}
