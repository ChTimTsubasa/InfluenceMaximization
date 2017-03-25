

package dataAccess;
import java.sql.*;
import java.util.*;

import dataStructure.Status;
/**
 * a JDBC accessing to the database storing data 
 * @author Tianyi Chen
 * @Time 2016-3-2
 */
public class DataBaseAccessor {
	protected Connection connect;
	protected Statement statement;
	protected ResultSet resultSet;
        
	private String hostSocket;
	private String dataBaseName;
	private String username;
	private String password;
	private String url;
	private String sql;
	
	/**
	 * Constructor of the DataAccess
	 * @param dataBaseName
	 * @param hostSocket
	 * @param username
	 * @param password
	 */
	public DataBaseAccessor(String dataBaseName, String hostSocket, String username, String password){

		this.dataBaseName=dataBaseName;
		this.hostSocket=hostSocket;
		this.username=username;
		this.password=password;
		
		//Register JDBC driver
		try{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}catch (ClassNotFoundException e) {
			   System.out.println("Driver error");
			   e.printStackTrace();
			   return;
		}
	}
	
	/**
	 * connect to the server
	 * @return true if connect successfully
	 */
	private boolean connect(){
		//connect to the database
		try{
			url = "jdbc:sqlserver://" + hostSocket + ";databaseName=" + dataBaseName;
			this.connect = DriverManager.getConnection(url,username,password);
			this.statement=connect.createStatement();
			return true;
		}catch(SQLException e){
			System.out.println("Cannot connect to databse "+ this.dataBaseName);
			e.printStackTrace();
			return false;
		}
		
	}
	/**
	 * disconnect the database and release the resources
	 */
	private void disconnect(){
		if(resultSet != null){   // close result set   
			try{
		    	resultSet.close() ;   
		    }catch(SQLException e){   
		        e.printStackTrace() ;   
		    }   
		}//if(resultSet != null)
		
		if(statement != null){   // close statement
			try{
				statement.close() ;   
		    }catch(SQLException e){
		    	e.printStackTrace() ;   
		    }   
		} //if(statement != null
		
		if(connect != null){  // close connection   
			try{   
				connect.close();
			}catch(SQLException e){   
				e.printStackTrace() ;   
		    }   
		 }  
	}
	
	
	/**
	 * execute some query
	 * @param query
	 * @return true if the query execution is successful
	 */
	public boolean execute(String query){
		
		if(!this.connect()){
			return false;
		}
		try{
			this.connect();
			return statement.execute(query);
		}catch (SQLException e) {
			System.out.println("Link Error!");
			e.printStackTrace();
			return false;
		}finally{
			this.disconnect();
		}
	}
        
	
	
	/**
	 * get the data and stored in hash table using string as key
	 * @param query
	 * @param keyColumnName
	 * @param valueColumnName
	 * @return hash table with keyColumnName as key and valueColunmName as value
	 */
	public LinkedHashMap<String,String> getDataInHashString(String query,String keyColumnName,String valueColumnName){
		String key;
		String value;
            LinkedHashMap<String,String> hash=new LinkedHashMap<String, String>();

            this.connect();

            try{
                this.resultSet=statement.executeQuery(query);
                while(resultSet.next()){
                    key=resultSet.getString(keyColumnName);
                    value=resultSet.getString(valueColumnName);
                    hash.put(key,value);
                }
                this.disconnect();
                return hash;
            }catch(SQLException e){
                e.printStackTrace();
                return null;
            }
            finally{
                this.disconnect();
            }
        }
        
        /**
         * delete the key Column in table where equals keyList
         * @param table
         * @param keyColumnName
         * @param keyList
         * @return 
         */
        public boolean deleteKey(String table, String keyColumnName, LinkedList<String> keyList){
            Iterator keyIter= keyList.iterator();
            String key;
            this.connect();
            try{
                statement.execute("USE "+this.dataBaseName+";");
            
                System.out.println("Started Excecution...");
                while(keyIter.hasNext()){
                    key=(String)keyIter.next();
                    boolean result=statement.execute("DELETE FROM "+ table +" WHERE "+ keyColumnName +"="+ key);
                    //if(!result) System.out.println(key);
                
            }
                System.out.println("finish deleting");
                return true;    
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
            finally{
                this.disconnect();
            }
        }
        
        /**
         * get a database record list from the database
         * @param query
         * @return
         */
		public ArrayList<String[]> getDataInArrayList(String query) {

        	ArrayList<String[]> recordList=new ArrayList<String[]>(1617140);
        	int colNum;
        	
            this.connect();
            try{
            	statement.execute("USE "+this.dataBaseName+";");
                resultSet=statement.executeQuery(query);
            	ResultSetMetaData rsmd = resultSet.getMetaData();
            	
            	colNum=rsmd.getColumnCount();
            	//System.out.println(colNum);
            	
                while(resultSet.next()){
                	
    				String[] record=new String[colNum];
    				for(int i=0;i<colNum;i++){
    					record[i]=resultSet.getString(i+1);//first column is column 1
    					//System.out.print(" "+record[i]);
    					
    				}
    				recordList.add(record);
    				
                }

                return recordList;
            }catch(SQLException e){
                e.printStackTrace();
                return null;
            }
            finally{
            	this.disconnect();
            }
        }
            
}
