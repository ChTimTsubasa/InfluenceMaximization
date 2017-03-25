/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructure;

import java.util.Iterator;

/**
 *
 * @author Tianyi Chen
 */
public class GraphTest {
    public static void main(String args[]){
        Graph graph=new Graph();
        graph.addVertex("a");
        graph.addVertex("b");
        graph.addVertex("c");
        graph.addVertex("d");
        
        graph.addDirectedEdge("a", "b");
        graph.addDirectedEdge("b", "a");
        graph.addDirectedEdge("a", "d");
        graph.addDirectedEdge("d", "a");
        graph.addDirectedEdge("a", "c");
        graph.addDirectedEdge("c", "a");
        graph.addDirectedEdge("b", "c");
        graph.addDirectedEdge("c", "b");
        
        System.out.println(graph.getVertsNum());
        System.out.println(graph.getDirectedEdgeNum());
        
        Iterator<String> iter=graph.getVertexHashMap().keySet().iterator();
        while(iter.hasNext()){
        	String nodeID=iter.next();
        	Vertex v=graph.getVertex(nodeID);

        	System.out.println();
        	System.out.println("node "+nodeID);
        	
        	Edge outEdge=v.firstOutEdge;
        	while(outEdge!=null){
        		System.out.print(" "+outEdge.getOutID());
        		outEdge=outEdge.nextOutEdge;
        	}
        	
        	Edge inEdge=v.firstInEdge;
        	System.out.println();
        	while(inEdge!=null){
        		System.out.print(" "+inEdge.getInID());
        		inEdge=inEdge.nextInEdge;
        	}
        }
        graph.deleteDirectedEdge("c", "b");
        System.out.println();
        iter=graph.getVertexHashMap().keySet().iterator();
        while(iter.hasNext()){
        	String nodeID=iter.next();
        	Vertex v=graph.getVertex(nodeID);

        	System.out.println();
        	System.out.println("node "+nodeID);
        	
        	Edge outEdge=v.firstOutEdge;
        	while(outEdge!=null){
        		System.out.print(" "+outEdge.getOutID());
        		outEdge=outEdge.nextOutEdge;
        	}
        	
        	Edge inEdge=v.firstInEdge;
        	System.out.println();
        	while(inEdge!=null){
        		System.out.print(" "+inEdge.getInID());
        		inEdge=inEdge.nextInEdge;
        	}
        }
    }
}