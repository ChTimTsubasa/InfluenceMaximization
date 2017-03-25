package dataPreProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import dataAccess.FileOperator;
import dataStructure.RepostStatus;
import dataStructure.Status;
import dataStructure.Message;
import dataStructure.MessageListOperator;
import wordSplit.DocumentProcessor;
import wordSplit.NlpirTest;

public class DocumentPreprocess {
	public static void main(String args[]){
		//the source of sample
		String sourceID="1864252027";//1266321801Ò¦³¿  1727548672¹ù¸»³Ç  1623412013»ÆÓ¢çÛ¶ù
		int sampleNetworkSize=15;
		String dirRef="sampleTest/"+sourceID+"/"+sampleNetworkSize+"/";
		HashMap<String, Message> sampleStatusMapRef = null;
		HashMap<String, Message> sampleCommentMapRef = null;
		preProcess(dirRef,sampleStatusMapRef,sampleCommentMapRef);
		
//		//get from the previous result
//		for(sampleNetworkSize=100; sampleNetworkSize<=4700;sampleNetworkSize+=200){
//			System.out.println("processing sample with size"+sampleNetworkSize);
//			String dir="sampleTest/"+sourceID+"/"+sampleNetworkSize+"/";
//			getFromRef(dir,dirRef);
//		}
		
		
	}



	private static void preProcess(String dir,HashMap<String, Message> sampleStatusMap,HashMap<String, Message> sampleCommentMap) {
		//sample Status extract
		sampleStatusMap=MessageListOperator.readMessageFromFile(dir+"sample.status");
		ArrayList<String> sampleStatusList=MessageListOperator.writeContentToArrayList(sampleStatusMap);
		
		//sample comments extract
		sampleCommentMap=MessageListOperator.readMessageFromFile(dir+"sample.comment");
		ArrayList<String> sampleCommentList=MessageListOperator.writeContentToArrayList(sampleCommentMap);
		
		//preprocessdocument
		DocumentProcessor.splitWord(sampleStatusList);
		DocumentProcessor.excludeStopWord(sampleStatusList);
		DocumentProcessor.splitWord(sampleCommentList);
		DocumentProcessor.excludeStopWord(sampleCommentList);
		
		
		MessageListOperator.updateContentFromArrayList(sampleStatusMap, sampleStatusList);
		MessageListOperator.updateContentFromArrayList(sampleCommentMap, sampleCommentList);
		
//		StatusListOperator.updateContentFromFile(sampleStatusList, "sampleTest/DocumentPre/sampleStatus"+sourceID+".txt");
		MessageListOperator.printMessageToFile(sampleStatusMap, dir+"samplePreDoc.status");
		MessageListOperator.printMessageToFile(sampleCommentMap, dir+"samplePreDoc.comment");
//Status status;
				
	}
	private static void getFromRef(String dir, String dirRef) {
		//sample Status extract
		HashMap<String, Message> sampleStatusMapRef=MessageListOperator.readMessageFromFile(dirRef+"samplePreDoc.status");
		HashMap<String, Message> sampleCommentMapRef=MessageListOperator.readMessageFromFile(dirRef+"sample.comment");
		
		HashMap<String, Message> sampleStatusMap=MessageListOperator.readMessageFromFile(dir+"samplePreDoc.status");
		HashMap<String, Message> sampleCommentMap=MessageListOperator.readMessageFromFile(dir+"sample.comment");
		
		Iterator<Entry<String, Message>>iter=sampleStatusMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Message> en=iter.next();
			String messageID=en.getKey();
			Message message=en.getValue();
			Message messageRef=sampleStatusMapRef.get(messageID);
			message.setContent(messageRef.getContent());
			
		}
		
		iter=sampleCommentMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Message> en=iter.next();
			String messageID=en.getKey();
			Message message=en.getValue();
			Message messageRef=sampleCommentMapRef.get(messageID);
			message.setContent(messageRef.getContent());
			
		}
		MessageListOperator.printMessageToFile(sampleStatusMap, dir+"samplePreDoc.status");
		MessageListOperator.printMessageToFile(sampleCommentMap, dir+"samplePreDoc.comment");
	}
}
