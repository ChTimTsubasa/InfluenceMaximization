package InfluMax;

import java.util.*;
import java.util.Map.Entry;

import dataAccess.FileOperator;
import dataStructure.*;
import jgibblda.LDA_InfluMax;
import jgibblda.Model;

public class RepostExtractor {
	String[]actualSeedUsers;
	Message targetMessage;
	Graph network;
	HashMap<String,HashSet<Message>> allMessageAdjSet;
	/**
	 * 
	 * @param calSeedUsers
	 * @param targetMessage
	 * @param allMessageAdjSet
	 * @param network
	 */
	public RepostExtractor(Message targetMessage,HashMap<String,HashSet<Message>> allMessageAdjSet,Graph network){
		this.targetMessage=targetMessage;
		this.network=network;
		this.allMessageAdjSet=allMessageAdjSet;
	}
	
/**
 * get the a user network where users has repost the target message
 * @return the graph form by the users that repost the exact target message
 */
	public Graph getExactRepostUserNetwork(){
		Graph repostNetwork=new Graph();
		
		Iterator<Entry<String,HashSet<Message>>> iterM=allMessageAdjSet.entrySet().iterator();
		Entry<String,HashSet<Message>>entry;
		while(iterM.hasNext()){
			entry=iterM.next();
			String userID=entry.getKey();
			HashSet<Message>messageSet=entry.getValue();
			if(containExactMessage(messageSet,targetMessage))
				repostNetwork.addVertex(userID);
		}
		
		Iterator<String> iterG=repostNetwork.getVertexHashMap().keySet().iterator();
		while(iterG.hasNext()){
			String repostUser=iterG.next();
			Vertex user=network.getVertex(repostUser);
			Edge adjUser=user.firstOutEdge;
			while(adjUser!=null){
				if(repostNetwork.containVertex(adjUser.getOutID()))
					repostNetwork.addDirectedEdge(repostUser, adjUser.getOutID());
				adjUser=adjUser.nextOutEdge;
			}
		}
		return repostNetwork;
	}
	/**
	 * see if the message set of a user contain a target message
	 * @param messageSet
	 * @param targetMessage
	 * @return
	 */
	private boolean containExactMessage(HashSet<Message> messageSet,Message targetMessage){
		for(Message message:messageSet){
			if(message.getContent().equals(targetMessage.getContent()))
				return true;
		}
		return false;
	}

	/**
	 * get the a user network where users has repost messages similar to the target message
	 * @param threshold
	 * @return
	 */
	public Graph getSimilarRepostUserNetwork(double threshold){
		Graph repostNetwork=new Graph();
		
		Iterator<Entry<String,HashSet<Message>>> iterM=allMessageAdjSet.entrySet().iterator();
		Entry<String,HashSet<Message>>entry;
		while(iterM.hasNext()){
			entry=iterM.next();
			String userID=entry.getKey();
			HashSet<Message>messageSet=entry.getValue();
			if(containSimilarMessage(messageSet,targetMessage,threshold))
				repostNetwork.addVertex(userID);
		}
		
		Iterator<String> iterG=repostNetwork.getVertexHashMap().keySet().iterator();
		while(iterG.hasNext()){
			String repostUser=iterG.next();
			Vertex user=network.getVertex(repostUser);
			Edge adjUser=user.firstOutEdge;
			while(adjUser!=null){
				if(repostNetwork.containVertex(adjUser.getOutID()))
					repostNetwork.addDirectedEdge(repostUser, adjUser.getOutID());
				adjUser=adjUser.nextOutEdge;
			}
		}
		return repostNetwork;
	}

	/**
	 * see if the message set of a user contain a message similar to target message
	 * @param messageSet
	 * @param targetMessage
	 * @param threshold
	 * @return
	 */
	private boolean containSimilarMessage(HashSet<Message> messageSet, Message targetMessage, double threshold) {
		for(Message message:messageSet){
			if(message.similarityTo(targetMessage)>threshold)
				return true;
		}
		return false;
	}
	
	


	
}