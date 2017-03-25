package InfluMax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import dataAccess.FileOperator;
import dataStructure.Comment;
import dataStructure.Edge;
import dataStructure.Graph;
import dataStructure.GraphOperator;
import dataStructure.Message;
import dataStructure.MessageTypeCheck;
import dataStructure.RepostStatus;
import dataStructure.Status;
import dataStructure.Vertex;

public class DocumentAssign {
	
	/**the optimized document assignment
	 * 
	 * @param network
	 * @param dir
	 * @param fileName 
	 * @param topicNum
	 */
	public static void optimizedAssign(Graph network,String dir, String fileName){
		Edge[] edges=network.getAllEdge();
		ArrayList<String> docMessage=new ArrayList<>();//edge
		ArrayList<String> fileDocMessage=new ArrayList<>();
		int[] edgeToDoc=new int[edges.length];
		HashMap<String,Integer> uniqDocSet=new LinkedHashMap<String,Integer>();
		int edgeIndex=0,docIndex=0;
		for(Edge edge:edges){
			ArrayList<String>edgeMessageList=edge.getMessageList();
			String edgeString="";
			StringBuffer sb=new StringBuffer();
			for(String temp:edgeMessageList){
				sb.append(temp);
			}
			edgeString=sb.toString();
			
			if(uniqDocSet.containsKey(edgeString)){
				edgeToDoc[edgeIndex]=uniqDocSet.get(edgeString);
				edgeIndex++;
			}
			else{
				docMessage.add(edgeString);
				edgeToDoc[edgeIndex]=docIndex;
				uniqDocSet.put(edgeString,docIndex);
				edgeIndex++;docIndex++;
			}
			//System.out.println(""+edgeIndex+" "+docIndex);
		}
		fileDocMessage.add(""+docMessage.size());
		fileDocMessage.addAll(docMessage);
		
		writeArrayToFile(dir+"/edgeToDocMap.txt",edgeToDoc);
		FileOperator.writeFile(dir+"/"+fileName, fileDocMessage);
	}
	
	/**
	 * the original assignment
	 * @param network
	 * @param dir
	 * @param topicNum
	 */
	public static void originalAssign(Graph network,String dir){
		Edge[] edges=network.getAllEdge();
		ArrayList<String> docMessage=new ArrayList<>();//edge
		ArrayList<String> fileDocMessage=new ArrayList<>();
		
		int edgeNum=0;
		for(Edge edge:edges){
			edgeNum++;
			
			ArrayList<String>edgeMessageList=edge.getMessageList();
			String edgeString="";
			StringBuffer sb=new StringBuffer();
			int heapMax=0;
			for(String temp:edgeMessageList){
				try{
					sb.append(temp);
					heapMax++;
				}
				catch(Error e){
					System.out.println("heap space out of memory "+heapMax+" "+edgeNum);
					System.exit(0);
				}
			}
			edgeString=sb.toString();
			docMessage.add(edgeString);
		}
		fileDocMessage.add(""+docMessage.size());
		fileDocMessage.addAll(docMessage);
		
		FileOperator.writeFile(dir, fileDocMessage);
		
	}
	
	/**
	 * relate the message to the network
	 * @param network
	 * @param statusMap
	 * @param commentMap
	 */
	public static void relateMessageToNetwork(Graph network, HashMap<String,Message>statusMap,HashMap<String,Message>commentMap){
		HashSet<String>includedStatusSet=new HashSet<>();//the hash set to store statuses that has been related to network
		
		Iterator<Entry<String, Message>> messageIter=statusMap.entrySet().iterator();
		Map.Entry<String,Message>entry;
		
		//Relate all users' message in users',for repost status, also include their original status
		while (messageIter.hasNext()) {
			entry=messageIter.next();
			Message status=entry.getValue();
			if(!network.containVertex(status.getUserID())){
				continue;
			}
			//if the user posting of this status is in the network
			Vertex v=network.getVertex(status.getUserID());
			Edge e=v.firstOutEdge;
			if(e==null)
				continue;
			String statusContent=status.getContent();
			if(MessageTypeCheck.isRetweetedStatus(status)){
				String originalStatusID=((RepostStatus)status).originalStatusID;
				statusContent+=statusMap.get(originalStatusID).getContent();
			}
			while(e!=null){
				e.addMessage(statusContent);
				e=e.nextOutEdge;
			}
		}
		
		
		//relate the comments to the network
		messageIter=commentMap.entrySet().iterator();
		while(messageIter.hasNext()){
			entry = messageIter.next();
			Comment comment=(Comment)entry.getValue();
			if (!network.containVertex(comment.getUserID()))
				continue;
			if (!includedStatusSet.contains(comment.originalStatusID))
				continue;
			Status status=(Status) statusMap.get(comment.originalStatusID);
			Vertex v=network.getVertex(status.userID);
			Edge e=v.firstOutEdge;
			while(e!=null & !e.getOutID().equals(comment.userID)){
				e=e.nextOutEdge;
			}
			if(e!=null){
				String content=comment.getContent()+status.getContent();
				if(MessageTypeCheck.isRetweetedStatus(status)){
					content+=statusMap.get(((RepostStatus)status).originalStatusID).getContent();
				}
				e.addMessage(content);
			}
		}
		
	}
	private static void writeArrayToFile(String dir,int[]array){
		
		ArrayList<String> fileLines=new ArrayList<>(array.length);
		for(int index=0;index<array.length;index++){
			String fileLine=""+index+" "+array[index];
			fileLines.add(fileLine);
		}
		FileOperator.writeFile(dir, fileLines);
	}
}
