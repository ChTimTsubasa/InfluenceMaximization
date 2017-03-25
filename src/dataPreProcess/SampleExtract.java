package dataPreProcess;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import dataAccess.*;
import dataStructure.*;

public class SampleExtract {
	public static void main(String args[]){
		String sourceID="1864252027";//1266321801Ò¦³¿  1727548672¹ù¸»³Ç  1623412013»ÆÓ¢çÛ¶ù 1195230310ºÎêÁ
	    int subNetworkSize=1100;
		//link to the database
	    DataBaseAccessor access=new DataBaseAccessor("Sinawler", "localhost:1433","sa","1234");
	    
	    //extract all data
	    StatusManager statusManager=new StatusManager(access);
	    UserManager userManager=new UserManager(access);
	    CommentManager commentManager=new CommentManager(access);
	    
	    Graph network=userManager.network;
	    HashMap<String, Status> statusList=statusManager.statusList;
	    HashMap<String, Comment> commentList=commentManager.getCommentMap();
	    
	    sampleExtract(sourceID,subNetworkSize,network,statusList, commentList);}
//    
//	    for(subNetworkSize=100; subNetworkSize<=5000;subNetworkSize+=200){
//	    	sampleExtract(sourceID,subNetworkSize,network,statusList, commentList);}
//	    }

	private static void sampleExtract(String sourceID,int subNetworkSize, Graph network, HashMap<String, Status> statusList,
		HashMap<String, Comment> commentList) {//extract a subset of network;
	    Graph sampleNetwork=network.getSubGraph(sourceID, subNetworkSize);
	    int subNetWorkRealSize=sampleNetwork.getVertsNum();
	    String dir="sampleTest/"+sourceID+"/"+subNetWorkRealSize+"/";
	    

	    int repostNum=0;
	    //extract a subset of statuses
	    HashMap<String, Message> sampleStatusList=new LinkedHashMap<>();
	    //go through the dataList once to put statuses whose user id is in sampleNetwork
	    Iterator<Entry<String, Status>> iter=statusList.entrySet().iterator();
	    Map.Entry<String,Status> entry;
	   
	    HashMap<String,Integer> originQuoteByRetweeted = new LinkedHashMap<>();
	    while(iter.hasNext()){
	        entry = (Entry<String, Status>)iter.next();
	        String statusID=""+entry.getKey();
	        Status status=entry.getValue();
	        String userID= status.userID;
	        if( sampleNetwork.containVertex(userID)){
	        	sampleStatusList.put(statusID, status);
	        	if(MessageTypeCheck.isRetweetedStatus(status)){
	        		repostNum++;
	        	}
	        }
	    }
//	    
	    
	    //go through again to put the original statuses into the sampleList,who:
	    //1. itself not in the sampleStatusList
	    //2. its retweetedStatus in the sampleStatusLis
	    iter=statusList.entrySet().iterator();
		while(iter.hasNext()){
			entry = (Entry<String, Status>)iter.next();
	        String statusID=""+entry.getKey();
	        Status status=entry.getValue();
	        if(!MessageTypeCheck.isRetweetedStatus(status))
	        	continue;
	        RepostStatus reStatus=(RepostStatus)status;
	        if(sampleStatusList.containsKey(reStatus.originalStatusID))
	        	continue;
	        String originID=reStatus.originalStatusID;
	        sampleStatusList.put(originID, statusList.get(originID));
		}
		
		
	    //extract a subset of comments
	    HashMap<String, Message> sampleCommentList=new LinkedHashMap<>();
	    //go through the dataList using an iterator
	    Iterator<Entry<String, Comment>> iterCom=commentList.entrySet().iterator();
	    Map.Entry<String,Comment> entryCom;
	    while(iter.hasNext()){
	    	entryCom = (Entry<String, Comment>)iterCom.next();
	        String commentID=""+entryCom.getKey();
	        Comment comment=entryCom.getValue();
	        String userID= comment.userID;
	        String statusID=comment.originalStatusID;
	        if(sampleStatusList.containsKey(statusID)&sampleNetwork.containVertex(userID))
	        	sampleCommentList.put(commentID, comment);
	    }
	    
	    
 	    //print some of the informations
	    System.out.println("vertex number "+sampleNetwork.getVertsNum());
	    System.out.println("edge number "+sampleNetwork.getDirectedEdgeNum());
	    System.out.println("sample status number "+sampleStatusList.size());
	    System.out.println("sample comments number "+sampleCommentList.size());
	    
	    //print samples to file
	    GraphOperator.printToFile(sampleNetwork, dir+"sample.network");
 
	    MessageListOperator.printMessageToFile(sampleStatusList,dir+"sample.status");

	    MessageListOperator.printMessageToFile(sampleCommentList,dir+"sample.comment");

		//print other info to file
	    ArrayList<String>infoList=new ArrayList<>();
	    infoList.add("Vertex number "+sampleNetwork.getVertsNum());
	    infoList.add("edge number "+sampleNetwork.getDirectedEdgeNum());
	    infoList.add("sample status number "+sampleStatusList.size());
	    infoList.add("sample repost number "+repostNum);
	    infoList.add("sample comments number "+sampleCommentList.size());
	    FileOperator.writeFile(dir+"sample.info", infoList);
	    				
	}
	
}
