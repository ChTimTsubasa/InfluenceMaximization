package dataStructure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import dataAccess.FileOperator;

public class MessageListOperator {
	public static final String SPLIT_CHAR="SPLIT";
	
	public static void printMessageToFile(HashMap<String, Message> statusList,String dir){
		ArrayList<String> fileLines=new ArrayList<String>(statusList.size());
		String line;
		Iterator iter;
		iter=statusList.entrySet().iterator();
 	    Map.Entry<String,Message> entry2;
 	    
 	    while(iter.hasNext()){
	        entry2= (Entry<String, Message>) iter.next();
	        Message message=entry2.getValue();
	        String statusID=""+entry2.getKey();
	        if(message.getContent().length()==0)
	        	message.setContent(" ");
	        message.setContent(message.getContent().replace("\r", " "));
	        message.setContent(message.getContent().replace("\n", " "));
	        message.setContent(message.getContent().replace("  ", " "));
	        line=statusID+SPLIT_CHAR+message.getUserID()+SPLIT_CHAR+message.getContent()+SPLIT_CHAR+message.getClass().getSimpleName();
	        if (MessageTypeCheck.isRetweetedStatus(message)){
	        	RepostStatus rs=(RepostStatus)message;
	        	line+=SPLIT_CHAR+rs.originalStatusID;
	        }
	        else if(MessageTypeCheck.isComment(message)){
	        	Comment c=(Comment)message;
	        	line+=SPLIT_CHAR+c.originalStatusID;
	        }
 	    	fileLines.add(line);
 	    }
 	    FileOperator.writeFile(dir, fileLines);
	}
	/**
	 * 
	 * @param dir
	 * @return
	 */
	public static  HashMap<String, Message>  readMessageFromFile(String dir){
		ArrayList<String> fileLines=FileOperator.readFile(dir);
		HashMap<String, Message> messageList=new LinkedHashMap<String, Message>(fileLines.size());
		Message message;
		for(String line:fileLines){
			String[] subString=line.split(SPLIT_CHAR);
			String statusID=subString[0];
			String userID=subString[1];

			String content=subString[2];
			if (content.equals(" "))
				content="";
			String messageType=subString[3];
			

			if(messageType.equals(Status.class.getSimpleName())){
					message=new Status(content,userID);}
			else{
				String originalStatusID=subString[4];
				if(messageType.equals(RepostStatus.class.getSimpleName()))
					message=new RepostStatus(content,userID,originalStatusID);
				else
					message=new Comment(content,userID,originalStatusID);
			}
			
			messageList.put(statusID, message);
		}
		return messageList;
	}
	
	
	public static ArrayList<String> writeContentToArrayList(HashMap<String,Message> statusList){
		ArrayList<String> contentList=new ArrayList<String>(statusList.size());
		Iterator iter;
		iter=statusList.entrySet().iterator();
 	    Map.Entry<String,Message> entry;
 	    
 	    while(iter.hasNext()){
	        entry= (Entry<String, Message>) iter.next();
	        Message message=entry.getValue();
	        
	        message.setContent(message.getContent().replace("\r", " "));
	        message.setContent(message.getContent().replace("\n", " "));
	        message.setContent(message.getContent().replace("  ", " "));
 	    	if(message.getContent().length()==0){
	        	message.setContent(" ");
	        }

 	    	contentList.add(message.getContent());
 	    }
 	    return contentList;
	}
	
	public static void writeContentToFile(HashMap<String,Message> statusList,String dir){
		ArrayList<String>fileLines=writeContentToArrayList(statusList);
 	    FileOperator.writeFile(dir, fileLines);
;	}
	
	
	public static void updateContentFromFile(HashMap<String,Message> messageList,String dir){
		ArrayList<String> fileLines=FileOperator.readFile(dir);
		updateContentFromArrayList(messageList,fileLines);
	}
	public static boolean updateContentFromArrayList(HashMap<String,Message> messageList,ArrayList<String>contentList){
		if(messageList.size()!=contentList.size())
			return false;
		
		Message message;
		Iterator iter;
		iter=messageList.entrySet().iterator();
 	    Map.Entry<String,Message> entry;
		for(int i=0;i<contentList.size();i++){
	        String contentLine=contentList.get(i);
	        if(contentLine.equals(" ")){
	        	contentLine="";
	        }
			entry= (Entry<String, Message>) iter.next();
	        message=entry.getValue();
	        
	        message.setContent(contentLine);
		}
		return true;
	}
	
	/**
	 * calculate the lambda of the message
	 * @param content
	 * @param phi
	 * @param wordMap
	 * @return
	 */
	public static double[] lambdaCalculation(Message message,double[][]phi,Map<String,Integer>wordMap){
		int topicNum=phi.length;
		String content=message.getContent();
		int[]vote=new int[topicNum];
		double[]lambda=new double[topicNum];
		for(int i=0;i<topicNum;i++){
			vote[i]=0;
			lambda[i]=0;
		}

		String[]words=content.split(" ");
		
		int wordIndex=0;
		int topicIndex=0;
		int voteTotal=0;
		for(String word:words){
			if(!wordMap.containsKey(word))
				continue;
			wordIndex=wordMap.get(word);
			//System.out.println(word);
			//find the most likely topic index for such word
			double phiMin=-1;
			int minTopicIndex=-1;
			for(topicIndex=0;topicIndex<topicNum;topicIndex++){
				if(phiMin<phi[topicIndex][wordIndex]){
					phiMin=phi[topicIndex][wordIndex];
					minTopicIndex=topicIndex;
				}
				voteTotal++;
				
				vote[minTopicIndex]++;
			}
		}
		
		for(int i=0;i<topicNum;i++){
			lambda[i]=((double)vote[i])/((double)voteTotal);
		}
		return lambda;
	}
	
	/**
	 * update the lambda of all message in the message list
	 * @param messageList
	 * @param phi
	 * @param wordMap
	 */
	public static void updateLambda(HashMap<String,Message>messageList,double[][]phi,Map<String,Integer> wordMap){
		Iterator<Entry<String,Message>>iter=messageList.entrySet().iterator();
		Entry<String,Message>entry;
		while(iter.hasNext()){
			entry=iter.next();
			Message m=entry.getValue();
			double[] lambda=lambdaCalculation(m,phi,wordMap);
			m.setLambda(lambda);
		}
	}
}
