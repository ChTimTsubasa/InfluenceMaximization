/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructure;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import dataAccess.FileOperator;

/**
 *
 * @author Tianyi Chen
 */
public class Graph {
    private final int MAX_VERTS=2000;
    private HashMap<String,Vertex> vertexHashMap;//=new LinkedHashMap<>();
    private int directedEdgeNum;
    
    /**
     * constructor
     */
    public Graph(){
        vertexHashMap=new LinkedHashMap<>(MAX_VERTS);
        directedEdgeNum=0;
    }
    
    public Graph(int num){
        vertexHashMap=new LinkedHashMap<>(num);
        directedEdgeNum=0;
    }
    /**
     * check if the graph contain key
     * @param ID
     * @return
     */
    
    public HashMap getVertexHashMap(){
    	return this.vertexHashMap;
    }
    public boolean containVertex(String ID){
    	return vertexHashMap.containsKey(ID);
    }
    /**
     * add vertex to the graph
     * @param vertex to add
     */
    public boolean addVertex(String ID){
        if(vertexHashMap.containsKey(ID))
            return false;
        vertexHashMap.put(ID,new Vertex());
        return false;
    }
    
    /**
     * link two vertices from id1 to id2
     * @param ID1
     * @param ID2 
     */
    public void addDirectedEdge(String ID1,String ID2){
        Vertex v1=vertexHashMap.get(ID1);
        Vertex v2=vertexHashMap.get(ID2);
        //v1 link to v2
        
        Edge e=v1.linkTo(ID2);
        
        v2.beLinkedTo(ID1,e);
        directedEdgeNum++;
    }
    
    /**
     * link an undirected link between two vertices
     * @param ID1
     * @param ID2 
     */
    public void addUndirectedEdge(String ID1,String ID2){
        this.addDirectedEdge(ID1, ID2);
        this.addDirectedEdge(ID2, ID1);
        directedEdgeNum+=2;
    }
    
    /**
     * delete edge between id1 to id2
     * @param ID1
     * @param ID2 
     */
    public void deleteDirectedEdge(String ID1, String ID2){
        Vertex v=vertexHashMap.get(ID1);
        v.deleteLinkTo(ID2);
        v=vertexHashMap.get(ID2);
        v.beDeletedLinkFrom(ID1);
    }
    
    /**
     * delete edge from id1 to id2
     * @param ID1
     * @param ID2 
     */
    public void deleteUndirectedEdge(String ID1, String ID2){
        this.deleteDirectedEdge(ID1, ID2);
        this.deleteDirectedEdge(ID2, ID1);
        directedEdgeNum-=2;
    }
    /**
     * 
     * @param ID
     * @return the Vertex with name ID
     */
    public Vertex getVertex(String ID){
    	return vertexHashMap.get(ID);
    }
    
    public int getVertsNum(){
		return this.vertexHashMap.size();
    }
    public int getDirectedEdgeNum(){
    	return this.directedEdgeNum;
    }
    
    public Edge[] getAllEdge(){
    	Edge[] edges=new Edge[this.getDirectedEdgeNum()];
	    Iterator<Entry<String, Vertex>> iter=this.vertexHashMap.entrySet().iterator();
	 	Map.Entry<String,Vertex> entry;
	 	Edge e;
	 	Vertex v;
	 	int edgeIndex=0;
	 	while(iter.hasNext()){
	 		entry= (Entry<String, Vertex>) iter.next();
	 	   	v=entry.getValue();
	 	    e=v.firstOutEdge;
	 	    while(e!=null){
	 	    	edges[edgeIndex]=e;
	 	    	e=e.nextOutEdge;
	 	    	edgeIndex++;
	 	    }
	 	   
	 	}   
    	return edges;
    }
    
    /**
     * get a sub graph from the graph using sourceID tag Vertex as root
     * @param sourceID
     * @param verNum
     * @return a new sub graph 
     */
    public Graph getSubGraph(String sourceID,int verNum){
    	
    	//BFS the Graph
    	HashSet<String> visitedID=new HashSet<String>();
    	Queue<String> queue = new LinkedList<>();
    	
    	queue.add(sourceID);
		visitedID.add(sourceID);
		boolean reachMax=false;
    	while (!queue.isEmpty()) {
    		String verID = queue.poll();
    		//find all links of this vertex
    		Vertex v=this.getVertex(verID);
    		Edge e=v.firstOutEdge;
    		while(e!=null){
    			if(!visitedID.contains(e.getOutID())){
    				visitedID.add(e.getOutID());
    				queue.add(e.getOutID());
    				if(visitedID.size()==verNum){
    					reachMax=true;
    					break;
    				}
    			}
    			e=e.nextOutEdge;
    		}
    		//only explore approximately the first verNum nodes
    		if (visitedID.size()>=verNum||reachMax)
    			break;
    	}
    	
    	//add vertices to new sub graph
    	Graph subGraph=new Graph(visitedID.size());
    	Iterator<String> it=visitedID.iterator();
    	while(it.hasNext()){
    		String id=it.next();
    		subGraph.addVertex(id);
    	}
    	
    	
    	//add edges to new sub graph
    	it=visitedID.iterator();
    	//go through all vertices of the new graph
    	while(it.hasNext()){
    		String verID=it.next();
    		Vertex v=this.getVertex(verID);
    		
    		if(v.firstOutEdge==null)
    			continue;
    		Edge e=v.firstOutEdge;
    		while(e.nextOutEdge!=null){
    			if(visitedID.contains(e.getOutID()))
    				subGraph.addDirectedEdge(verID, e.getOutID());
    			e=e.nextOutEdge;
    		}
    		
    	}
		return subGraph;
    }
    
    
    /**
     * set the probability[4] to a contant value
     * @param pValue
     */
    public void setConstantProbablity(double pValue){
    	Iterator<Entry<String,Vertex>> iterIN=this.getVertexHashMap().entrySet().iterator();
    	Entry<String,Vertex>entry;
		
    	//first find the maximum and minimun value of the probability
    	while(iterIN.hasNext()){
			entry=iterIN.next();
			Vertex vertex	=entry.getValue();
			Edge edge		=vertex.firstOutEdge;
			if(edge==null){
				continue;
			}
			while(edge!=null){
				edge.probability[4]=pValue;
				edge=edge.nextOutEdge;
			}
		}
    }
    
    /**
     * print all the nodes id to the file
     * @param dir
     * @param name
     */
    public void writeFileWithNodesOnly(String dir,String name){
    	ArrayList<String>fileLines=new ArrayList<>(vertexHashMap.size());
    	Iterator<String> iterG=vertexHashMap.keySet().iterator();
    	while(iterG.hasNext()){
    		String userID=iterG.next();
    		fileLines.add(userID);
    	}
    	FileOperator.writeFile(dir+name+".nodes", fileLines);
    }
    /**
     * write the all probability to file
     * @param dir
     * @param name
     */
    public void writeFileWithProbabilityAll(String dir,String name){
    	for(int type=0;type<4;type++){
    		writeFileWithProbability(dir,name+type,type);
    	}
    }
    
    /**
     * write the probability to file
     * @param dir
     * @param name
     */
    public void writeFileWithProbability(String dir,String name,int type){

			Iterator<Entry<String,Vertex>> iterIN=this.getVertexHashMap().entrySet().iterator();
			ArrayList<String>stringLine=new ArrayList<>();
			Entry<String,Vertex>entry;
			stringLine.add("# "+this.getVertsNum()+" "+this.getDirectedEdgeNum());
			
			while(iterIN.hasNext()){
				entry=iterIN.next();
				String line;
				String userID	=entry.getKey();
				Vertex vertex	=entry.getValue();
				Edge edge		=vertex.firstOutEdge;
				if(edge==null){
					continue;
				}
				while(edge!=null){
					line=userID+" "+edge.getOutID()+" "+edge.probability[type]+" ";
					edge=edge.nextOutEdge;
					stringLine.add(line);
				}
			}
			//System.out.println("writting graph "+i);
			FileOperator.writeFile(dir+"target"+name+".inf", stringLine);
    }
    
    /**
     * normalize the propagation probability of the network
     */
    public void normalizeProbability(double speedUp){
    	double maxValue[]=new double[5];
    	double minValue[]=new double[5];;
    	Iterator<Entry<String,Vertex>> iterIN=this.getVertexHashMap().entrySet().iterator();
    	Entry<String,Vertex>entry;
		
    	//first find the maximum and minimun value of the probability
    	while(iterIN.hasNext()){
			entry=iterIN.next();
			Vertex vertex	=entry.getValue();
			Edge edge		=vertex.firstOutEdge;
			if(edge==null){
				continue;
			}
			while(edge!=null){
				for(int type=0;type<5;type++){
					if(maxValue[type]<edge.probability[type])
						maxValue[type]=edge.probability[type];
					if(minValue[type]>edge.probability[type])
						minValue[type]=edge.probability[type];
				}
				edge=edge.nextOutEdge;
			}
		}
    	
    	//then normalize the probability
    	iterIN=this.getVertexHashMap().entrySet().iterator();
    	while(iterIN.hasNext()){
			entry=iterIN.next();
			Vertex vertex	=entry.getValue();
			Edge edge		=vertex.firstOutEdge;
			if(edge==null){
				continue;
			}
			while(edge!=null){
				for(int type=0;type<5;type++){
					edge.probability[type]=speedUp*(edge.probability[type]-minValue[type])/(maxValue[type]-minValue[type]);
				}
				edge=edge.nextOutEdge;
			}
		}
    }
}
    