package dataAccess;

import java.io.*;
import java.util.ArrayList;


public class FileOperator {
	
	/**
	 * 
	 * @param dir
	 * @param fileLines
	 */
	public static void writeFile(String dir,ArrayList<String> fileLines){
		try {
			
	    	File file=new File(dir);
	    	if(!file.getParentFile().exists()){
	    		file.getParentFile().mkdirs();
	    		
	    	}
	    	FileWriter fileWriter = new FileWriter(file);
	    	BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

	    	for (int i=0;i<fileLines.size();i++){
	    		bufferedWriter.write(fileLines.get(i));
	 	    	bufferedWriter.newLine();
	    	}
	    	bufferedWriter.close();
	    	fileWriter.close();
	    }catch (IOException e) {
	    	System.out.println("Errors occured");
	    	System.exit(1);
	    }
	}
	
	public static ArrayList<String> readFile(String dir){
		ArrayList<String> fileLines=new ArrayList<>();
		String line=null;
		try {
			
	    	File file=new File(dir);
	    	if(!file.exists()){
	    		System.out.println("file not exit");
	    		return null;
	    	}
	    	FileReader fileReader = new FileReader(file);
	    	BufferedReader bufferedReader = new BufferedReader(fileReader);

	    	line=bufferedReader.readLine();
	    	while(line!=null) {
	    		fileLines.add(line);
		    	line=bufferedReader.readLine();
			}
	    	
	    	bufferedReader.close();
	    	fileReader.close();
	    }catch (IOException e) {
	    	System.out.println("Errors occured");
    		return null;
	    }
		return fileLines;
		
	}
	public static int[] readArrayFromFile(String dir) {
		ArrayList<String>fileLines=FileOperator.readFile(dir);
		int[]array=new int[fileLines.size()];
		for(String fileLine:fileLines){
			String[] edgeNoToDocNo=fileLine.split(" ");
			int indexEdge	=Integer.parseInt(edgeNoToDocNo[0]);
			int indexDoc	=Integer.parseInt(edgeNoToDocNo[1]);
			array[indexEdge]=indexDoc;
		}
		return array;
	}
}
