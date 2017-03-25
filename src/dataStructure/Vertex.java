/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructure;

/**
 *
 * @author Tianyi Chen
 */
public class Vertex {
    public Edge firstOutEdge;
    public Edge firstInEdge;
    
    public int inDegree;
    public int outDegree;
    public double averageOutTheta[];
    public double vitality;
    
    /**
     * constructor
     * @param ID 
     */
    public Vertex(){
        firstOutEdge=null;
        vitality=0;
    }
    
    /**
     * link to some edges
     * @param ID 
     */
    public Edge linkTo(String ID2){
    	Edge e=new Edge(ID2);
    	e.nextOutEdge=firstOutEdge;
        firstOutEdge=e;
        outDegree++;
        return e;
    }
    
    public void beLinkedTo(String ID1,Edge e) {
    	e.setInID(ID1);
    	e.nextInEdge=firstInEdge;
    	firstInEdge=e;
        inDegree++;
	}
    
    /**
     * delete the link to some edge
     * @param ID 
     */
    public void beDeletedLinkFrom(String ID){
    	Edge currentInEdge;
        Edge previousInEdge;
        if(firstInEdge==null) return;
        if(firstInEdge.getInID().equals(ID)){
        	currentInEdge=firstInEdge;
        	firstInEdge=firstInEdge.nextInEdge;
            inDegree--;
            return;
        }
        
        if(firstInEdge.nextInEdge==null) return;
        
        currentInEdge=firstInEdge.nextInEdge;
        previousInEdge=firstInEdge;
        
        while(currentInEdge!=null){
            if(currentInEdge.getInID().equals(ID)){
                inDegree--;
                previousInEdge.nextInEdge=currentInEdge.nextInEdge;
                return;
            }
            else{
            	previousInEdge.nextInEdge=currentInEdge;
                currentInEdge=currentInEdge.nextInEdge;
            }
        }
        return;

    }
    
    /**
     * delete the link to some edge
     * @param ID 
     */
    public void deleteLinkTo(String ID){
        Edge currentOutEdge;
        Edge previousOutEdge;
        if(firstOutEdge==null) return;
        if(firstOutEdge.getOutID().equals(ID)){
        	currentOutEdge=firstOutEdge;
            firstOutEdge=firstOutEdge.nextOutEdge;
            outDegree--;
            return;
        }
        
        if(firstOutEdge.nextOutEdge==null) return;
        
        currentOutEdge=firstOutEdge.nextOutEdge;
        previousOutEdge=firstOutEdge;
        
        while(currentOutEdge!=null){
            if(currentOutEdge.getOutID().equals(ID)){
            	outDegree--;
                previousOutEdge.nextOutEdge=currentOutEdge.nextOutEdge;
                return;
            }
            else{
                previousOutEdge.nextOutEdge=currentOutEdge;
                currentOutEdge=currentOutEdge.nextOutEdge;
            }
        }
        return;
    }
    
    public void calculateThetaAverage(int topicNum){
    	Edge e=this.firstOutEdge;
    	this.averageOutTheta=new double[topicNum];
    	int outEdgeNum=0;
    	for(int i=0;i<topicNum;i++)
			averageOutTheta[i]=0;
    	if(e==null){
    		return;
    	}
    	while(e!=null){
    		for(int i=0;i<topicNum;i++){
        		averageOutTheta[i]+=e.theta[i];
				}
    		outEdgeNum++;
    		e=e.nextOutEdge;
    	}
    	for(int i=0;i<topicNum;i++){
    		averageOutTheta[i]=averageOutTheta[i]/outEdgeNum;
		}
    }
}