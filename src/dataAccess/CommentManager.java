package dataAccess;

import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.Comment;
import dataStructure.Graph;

public class CommentManager {
	private DataBaseAccessor dataBaseAccessor;
	public CommentManager(DataBaseAccessor dataBaseAccessor){
        this.dataBaseAccessor=dataBaseAccessor;
    }
	
	public HashMap<String,Comment>getCommentMap(){
		ArrayList<String[]> list=dataBaseAccessor.getDataInArrayList("USE Sinawler;"
				+ "Select comment_id,content,user_id,status_id from comments Where +"
				+ "status_id in(Select status_id from uniq_end_statuses_backup) "
				+ "and "
				+ "user_id in (Select user_id from uniq_users);");
		HashMap<String,Comment>CommentMap=new HashMap<String,Comment>(list.size());
		
		for(String[]data:list){
			String commentID=data[0];
			String content=data[1];
			String userID=data[2];
			String statusID=data[3];
			CommentMap.put(commentID, new Comment(content,userID,statusID));
		}
		return CommentMap;
	}
}
