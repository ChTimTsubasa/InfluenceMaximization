/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructure;

import java.util.ArrayList;

/**
 *
 * @author DELL-PC
 */
public class Edge {
    private String outID;		//outID of the linked node
    private String inID;		//inID of the linked node
    ArrayList<String> messageList;
    public double[] theta;
    public double[] probability=new double[5];
    public Edge nextOutEdge;
    public Edge nextInEdge;
    public Edge(String ID) {
        this.setOutID(ID);
        messageList=new ArrayList<>();
        probability[0]=0;
        probability[1]=0;
        probability[2]=0;
        probability[3]=0;
        probability[4]=0;
    }
    
    public void addMessage(String message){
    	messageList.add(message);
    }
    public ArrayList<String> getMessageList(){
    	return this.messageList;
    	
    }

	public String getOutID() {
		return outID;
	}

	public void setOutID(String iD) {
		outID = iD;
	}
	
	public String getInID() {
		return inID;
	}

	public void setInID(String iD) {
		inID = iD;
	}
		
}
