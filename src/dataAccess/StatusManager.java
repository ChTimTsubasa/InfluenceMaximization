/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccess;

import dataAccess.DataBaseAccessor;
import dataStructure.*;
import java.util.*;
import java.util.regex.*;
/**
 * get the data structure of retweeted statuses
 * @author Tianyi Chen
 * @version 2016-3-20;
 */
public class StatusManager{
    /**
     * constructor of this class
     * used only for Sinawler Database to modify the database
     */
    private DataBaseAccessor dataBaseAccessor;
    private LinkedHashMap<String,String> retweetedStatusList=new LinkedHashMap<String, String>();
    public  LinkedHashMap<String,Status> statusList=new LinkedHashMap<>();
    private final static int STATUS_ID_COL_NUM=0;
    private final static int CONTENT_COL_NUM=1;
    private final static int USER_ID_COL_NUM=2;
    private final static int RETWEETED_STATUS_ID_COL_NUM=3;
    /**
     * constructor of the RetweetedStatusManager,will load the data into
     * a hash table with status_id as key and status content as value
     * @param dataBaseAccessor
     */
    public StatusManager(DataBaseAccessor dataBaseAccessor) {
    	this.dataBaseAccessor=dataBaseAccessor;
    	System.out.println("Loading retweeted status...");
    	//this.retweetedStatusList=dataBaseAccessor.getDataInHashString("SELECT status_id,content FROM uniq_end_statuses WHERE retweeted_status_id!=0","status_id","content");
    	System.out.println("Retweeted status loading complete");
       

    	System.out.println("Loading record...");
    	ArrayList<String[]>recordList=dataBaseAccessor.getDataInArrayList("SELECT status_id,content,user_id,retweeted_status_id FROM uniq_end_statuses_backup");
    	System.out.println("record loading complete");
    	
    	System.out.println("Converting record format...");
    	this.statusList=this.recordToStatus(recordList);
    	System.out.println("Converting complete!");
    	recordList=null;
    	
    }
    
    protected LinkedHashMap<String,Status> recordToStatus(ArrayList<String[]> recordList){
    	int recordNo=recordList.size();
    	LinkedHashMap<String,Status> statusList=new LinkedHashMap<>(recordNo);
    	
    	for(int i=0;i<recordNo;i++){
    		String[] record=recordList.get(i);
    		Status status;
    		
    		String statusID 	 	= record[STATUS_ID_COL_NUM];
    		String content 	 	 	= record[CONTENT_COL_NUM];
    		String userID		 		= record[USER_ID_COL_NUM];
    		String retweetedStatusID	= record[RETWEETED_STATUS_ID_COL_NUM];
    		
    		if (retweetedStatusID.equals("0"))
    			status = new Status(content, userID);
    		else
    			status = new RepostStatus(content, userID, retweetedStatusID);
    		
    		statusList.put(statusID, status);
    	}
    	
    	return statusList;
    }
    
    
    /**
     * check and return the bad statuses
     * @param userManager
     * @return bad statuses array
     */
    public LinkedList<String> checkRetweetedStatusList(UserManager userManager){
        
        LinkedList<String> availableStatuses = new LinkedList<String>();//
        Iterator iter=retweetedStatusList.entrySet().iterator();
        Map.Entry<String,String> entry;
        long i=0,j=0;
        System.out.println("Checking...");
        //go through the dataList using an iterator
        while(iter.hasNext()){
            entry = (Map.Entry)iter.next();
            String statusID=""+entry.getKey();
            String content=entry.getValue();
            if(!retweetedStatusAvailable(content,userManager)){
                availableStatuses.add(statusID);
                //System.out.print("Bad Status: ");
                i++;
            }
            //else System.out.print("Good Status: ");
            j++;
        }
        System.out.println("total bad "+i);
        System.out.println("bad Statuses percentage:"+ 100*(double)(i)/(double)(j)+"%");
       
        return availableStatuses;
    }
    
    /**
     * check if the status is available, i.e.it is a direct retweeted data
     * or the data are all traceble in usermanager
     * @param content
     * @param userManager
     * @return true if retweeted status is qualified
     */
    private boolean retweetedStatusAvailable(String content, UserManager userManager) {
        LinkedList<String> usersInContent=this.userNameContentExtract(content);
        if(usersInContent.isEmpty())
            return true;
        else{
            Iterator userIter= usersInContent.iterator();
            while(userIter.hasNext()){
                String userName=(String)userIter.next();
                if (!userManager.hasUserName(userName))
                    return false;
            }
            return true;
        }
    }
    
    /**
     * extract the user name out of the retweeted statuses' content
     * example: "//:@username1 //:@username2" will be come username1 and username2 stored in a list
     * @param s
     * @return 
     */
    private LinkedList<String> userNameContentExtract(String s){
        //RegEX to split the content
        Pattern pattern=Pattern.compile("[@].*[:]");
        String[] segments=s.split("//");
        LinkedList<String> userList=new LinkedList<String>();
        for(int i=0;i<segments.length;i++){
            Matcher matcher=pattern.matcher(segments[i]);
            while(matcher.find()){
                String userName=segments[i].substring(matcher.start()+1, matcher.end()-1);
                userList.add(userName);
            }
        }
        return userList;
    }
}