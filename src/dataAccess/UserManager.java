/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccess;

import dataAccess.DataBaseAccessor;
import dataStructure.*;

import java.util.*;

/**
 *
 * @author Tianyi Chen
 */
public class UserManager {
    private DataBaseAccessor dataBaseAccessor;
    private HashMap<String,String> userNameList;
    public Graph network;
    /**
     * constructor
     * @param dataBaseAccessor 
     */
    public UserManager(DataBaseAccessor dataBaseAccessor){
        System.out.println("Loading list of users");
        //this.userNameList=dataBaseAccessor.getDataInHashString("SELECT user_id, name FROM uniq_users ","name","user_id");
        //System.out.println("Loading user complete");
        
        ArrayList<String[]>userRecordList=dataBaseAccessor.getDataInArrayList("SELECT user_id FROM uniq_users");
        System.out.println("Loading users complete");

        addNodesFromRecord(userRecordList);
        userRecordList=null;

        System.out.println("Loading list of relations");
        ArrayList<String[]>relationRecordList=dataBaseAccessor.getDataInArrayList("SELECT source_user_id, target_user_id FROM uniq_user_relation");
        System.out.println("Loading relation complete");
        
        addRelation(relationRecordList);
        relationRecordList=null;

    }
    /**
     * 
     * @param userName
     * @return true if it has the userName
     */
    public boolean hasUserName(String userName) {
        return userNameList.containsKey(userName);
    }
    
    /**
     * 
     * @param userRecordList
     */
    public void addNodesFromRecord(ArrayList<String[]> userRecordList){
    	int userNo=userRecordList.size();
    	this.network=new Graph(userNo);
    	for(int i=0;i<userNo;i++){
    		network.addVertex(userRecordList.get(i)[0]);
    	}
    }
    
    public void addRelation(ArrayList<String[]> relationRecordList){
    	int relationNo=relationRecordList.size();
    	for(int i=0;i<relationNo;i++){
    		String source=relationRecordList.get(i)[0];
    		String target=relationRecordList.get(i)[1];
    		network.addDirectedEdge(source, target);
    	}
    }
}
