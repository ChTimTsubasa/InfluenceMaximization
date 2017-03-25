/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataPreProcess;

import dataAccess.DataBaseAccessor;
import dataAccess.StatusManager;
import dataAccess.UserManager;
import java.util.*;

/**
 * delete the bad status
 * @author Tianyi Chen
 */
public class DataBasePreprocess {
    public static void main(String args[]){
    	
    	//link to the database
        System.out.println("Start");
        DataBaseAccessor access=new DataBaseAccessor("Sinawler", "localhost:1433","sa","1234");
        
        StatusManager statusManager=new StatusManager(access);
        UserManager userManager=new UserManager(access);
        
        LinkedList<String> badStatuses=statusManager.checkRetweetedStatusList(userManager);
       
        //access.deleteKey("uniq_end_statuses_backup", "status_id",badStatuses);
        
        System.out.println("complete£¡ ");
       
        
    }
}
