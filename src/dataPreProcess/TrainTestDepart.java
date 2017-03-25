package dataPreProcess;

import java.util.HashMap;

import java.util.*;

import dataStructure.Graph;
import dataStructure.GraphOperator;
import dataStructure.Message;
import dataStructure.MessageListOperator;
import dataStructure.MessageTypeCheck;
import dataStructure.RepostStatus;

public class TrainTestDepart {
	public static void main(String args[]){
		String sourceID="1864252027";
		int subNetworkSize=15;
		int threshold=20;
		//String dir="sampleTest/"+sourceID+"/"+subNetworkSize+"/";
		//depart(dir,threshold);
		String dir="sampleTest/"+sourceID+"/"+subNetworkSize+"/";
		depart(dir,threshold);
//		for(subNetworkSize=100;subNetworkSize<=4900;subNetworkSize+=200){
//			String dir="sampleTest/"+sourceID+"/"+subNetworkSize+"/";
//			depart(dir,threshold);
//			}
		}
	public static void depart(String dir,int threshold){
		//prepare the data
		
		//read sample data from file
		Graph network=GraphOperator.getFromFile(dir+"sample.network");
		HashMap<String,Message>statusMap=MessageListOperator.readMessageFromFile(dir+"samplePreDoc.status");
		HashMap<String,Message>commentMap=MessageListOperator.readMessageFromFile(dir+"/samplePreDoc.comment");
		
		//departed data set
		HashMap<String,Message>trainingSet=new LinkedHashMap<>();
		HashMap<String,Message>testSet=new LinkedHashMap<>();
		HashMap<String,Message>targetSet=new LinkedHashMap<>();
		
		//go through all status and extract the training set
		//first extract all retweeted status
		Iterator<String> iter= statusMap.keySet().iterator();
		int total=0,original=0,retweeted=0,totalInNetwork=0,originalInNetwork=0,retweetedInNetwork=0;
		HashMap<String,Integer> originQuoteByRetweeted = new LinkedHashMap<>();
		while(iter.hasNext()){
			String keyID=iter.next();
			Message m=statusMap.get(keyID);
			if(MessageTypeCheck.isRetweetedStatus(m)){
				retweeted++;
				if(network.containVertex(m.getUserID())){
					retweetedInNetwork++;
					totalInNetwork++;
				}

				RepostStatus rs=(RepostStatus)m;
				if(originQuoteByRetweeted.containsKey(rs.originalStatusID)){
					int reTime=originQuoteByRetweeted.get(rs.originalStatusID);
					reTime++;
					originQuoteByRetweeted.put(rs.originalStatusID,reTime);
				}
				else{
					originQuoteByRetweeted.put(rs.originalStatusID,1);
				}
			}
			else{
				original++;
				if(network.containVertex(m.getUserID())){
					originalInNetwork++;
					totalInNetwork++;
				}
			}
			total++;
		}
		
		System.out.println("total "+total+"; original "+original+"; retweeted "+retweeted);
		System.out.println("in user");
		System.out.println("total "+totalInNetwork+"; original "+originalInNetwork+"; retweeted "+retweetedInNetwork);
		System.out.println(originQuoteByRetweeted.size());
		
		//select the status with the highest retweeted number as source status of test set
		iter= originQuoteByRetweeted.keySet().iterator();

		while(iter.hasNext()){
			String id=iter.next();
			int retweetedNum=originQuoteByRetweeted.get(id);
			if(retweetedNum>=threshold){
				testSet.put(id, statusMap.get(id));
				targetSet.put(id, statusMap.get(id));
				//System.out.println(id+" "+ retweetedNum);
				}
		}
		
		iter= statusMap.keySet().iterator();
		while(iter.hasNext()){
			String keyID=iter.next();
			Message m=statusMap.get(keyID);
			if( testSet.containsKey(keyID))
				continue;
			
			if(MessageTypeCheck.isRetweetedStatus(m)){
				RepostStatus rs=(RepostStatus)m;
				if(testSet.containsKey(rs.originalStatusID))
					testSet.put(keyID, m);
				else
					trainingSet.put(keyID, m);
			}
			else{//original status
				trainingSet.put(keyID, m);
			}	
		}
		
		System.out.println(testSet.size());

		System.out.println(trainingSet.size());

		MessageListOperator.printMessageToFile(targetSet,dir+"targetMessages.status");
		MessageListOperator.printMessageToFile(testSet, dir+"testSet.status");
		MessageListOperator.printMessageToFile(trainingSet, dir+"trainingSet.status");

	}
}
