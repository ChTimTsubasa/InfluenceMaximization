package dataStructure;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import InfluMax.*;
import dataAccess.FileOperator;

public class GraphOperator {
	public static final String SPLIT_WORD=" ";
	
	 /**
     * print the Graph to the file in dir
     * @param dir
     */
    public static void printToFile(Graph graph,String dir){
    	ArrayList<String> fileLines=new ArrayList<>(graph.getVertsNum());
		String userID;
	    String fanIDs;
	    HashMap<String,Vertex> gv=graph.getVertexHashMap();
	    Iterator<Entry<String, Vertex>> iter=gv.entrySet().iterator();
	 	Map.Entry<String,Vertex> entry;
	 	Edge e;
	 	Vertex v;
	 	while(iter.hasNext()){
	 		entry= (Entry<String, Vertex>) iter.next();
	 		userID=""+entry.getKey()+SPLIT_WORD;
		    fanIDs="";
	 	   	v=entry.getValue();
	 	    e=v.firstOutEdge;
	 	    while(e!=null){
	 	    	fanIDs+=e.getOutID()+SPLIT_WORD;
	 	    	e=e.nextOutEdge;
	 	    }
	 	    fileLines.add(userID+fanIDs);
	 	}
	 	FileOperator.writeFile(dir, fileLines);
	 	    
	}
    
    /**
     * print the Graph to the file in dir
     * @param dir
     */
    public static Graph getFromFile(String dir){
    	Graph graph;
		ArrayList<String> fileLines=FileOperator.readFile(dir);
		String line;
		//Construct the graph
    	graph=new Graph(fileLines.size());
		
		for(int i=0;i<fileLines.size();i++){
			line=fileLines.get(i);
			String[] subString=line.split(SPLIT_WORD);
			String userID=subString[0];
			graph.addVertex(userID);
		}
		
		for(int i=0;i<fileLines.size();i++){
			line=fileLines.get(i);
			String[] subString=line.split(SPLIT_WORD);
			if(subString.length==1)
				continue;
			String userID=subString[0];
			for(int j=1;j<subString.length;j++){
				graph.addDirectedEdge(userID, subString[j]);
			}
		}
		return graph;
	}
    
    
    
    public static boolean updateTheta(Graph network,double [][]theta){
    	int edgeNum=network.getDirectedEdgeNum();
		int docNum=theta.length;
		int topicNum=theta[0].length;
		if(edgeNum!=docNum){
			System.out.println("edge number and theta not match");
			return false;
		}
		Edge[] edgeArray=network.getAllEdge();
		for(int edgeIndex=0; edgeIndex<edgeNum;edgeIndex++){
			edgeArray[edgeIndex].theta=theta[edgeIndex];
		}
		
		Iterator<String> iter=network.getVertexHashMap().keySet().iterator();
		while(iter.hasNext()){
			String userID=iter.next();
			Vertex v=network.getVertex(userID);
			v.calculateThetaAverage(topicNum);
		}
		return true;
    }
    
    public static void updateVitality(Graph network,Message[]messages){
    	double messageNum=0;
    	double userNum=network.getVertsNum();
    	for(Message message:messages){
    		String userID=message.getUserID();
    		if(!network.containVertex(userID))
    			continue;
    		messageNum++;
    		network.getVertex(userID).vitality++;
    	}
    	
    	Iterator<Entry<String,Vertex>> iter=network.getVertexHashMap().entrySet().iterator();
    	while(iter.hasNext()){
    		Map.Entry<String,Vertex> entry=iter.next();
    		Vertex v=entry.getValue();
    		
    		v.vitality=1/(1+Math.exp(-(v.vitality-messageNum/userNum)));
    		//System.out.println(entry.getKey()+" "+v.activity);
    	}
    	
    }

	
}
