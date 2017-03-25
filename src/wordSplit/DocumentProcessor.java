package wordSplit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.*;

import utils.SystemParas;

import com.sun.jna.Library;
import com.sun.jna.Native;

import dataAccess.FileOperator;

public class DocumentProcessor { 

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				"C:\\project\\fin_yr\\prj\\PreProcess\\PreProcess_LDAMap\\ICTCLAS2015\\lib\\win64\\NLPIR", CLibrary.class);
				 
		public int NLPIR_Init(String sDataPath, int encoding,
				String sLicenceCode);
				
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
		public byte[] NLPIR_ParagraphProcess(byte[] sSrc,int bPOSTagged);
		
		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);
		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);
		public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
		public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
		public String NLPIR_GetLastErrorMsg();
		public void NLPIR_Exit();
	}

	public static String transString(String aidString, String ori_encoding,
			String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void process(String rFileName,String wFileName){

		ArrayList<String> fileLines=FileOperator.readFile(rFileName);
		splitWord(fileLines);
		excludeStopWord(fileLines);
		FileOperator.writeFile(wFileName, fileLines);
	}
    public static void splitWord(ArrayList<String> lineList){
	//public static void main(String args[]){	
		System.out.println("Splitting words...");
		String system_charset = "GBK";//GBK----0
		//String system_charset = "UTF-8";
		int charset_type = 1;
		
		String argu = "C:\\project\\fin_yr\\prj\\PreProcess\\PreProcess_LDAMap\\ICTCLAS2015";
		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;

		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is "+nativeBytes);
			return;
		}
		
		//new code
		//start splitting word
		for(int i=0;i<lineList.size();i++){
			String readLine=lineList.get(i);
			String newLine="";
			try {
				newLine = CLibrary.Instance.NLPIR_ParagraphProcess(readLine, 1);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}
			lineList.set(i, newLine);
		}
		System.out.println("end spliting");
	}
    
    public static void excludeStopWord(ArrayList<String> lineList){
    	String dirStopWordTable="sampleTest/DocumentPre/中文停用词表.txt";
    	String dirstopNatureTable="sampleTest/DocumentPre/停用符.txt";
    	
    	Set stopWordSet=new HashSet<String>(FileOperator.readFile(dirStopWordTable));
    	Set stopNatureSet=new HashSet<String>(FileOperator.readFile(dirstopNatureTable));
    	
    	System.out.println("start extracting");
    	
    	for(int i=0;i<lineList.size();i++){
    		String line=lineList.get(i);
    		String[] wordArray=line.split(" ");
    		int wordNum=wordArray.length;
    		String newLine="";
    		
    		for(int j=0;j<wordNum;j++){
    			String[] segment=wordArray[j].split("/");
        		String nature=segment[segment.length-1];
        		if (stopNatureSet.contains(nature))
        			continue;
        		String word=segment[0];
        		if (stopWordSet.contains(word)){
        			continue;
        		}
        		if(word.length()<=1){
        			continue;
        		}
        		newLine+=wordArray[j]+" ";
    		}
    		
        	lineList.set(i, newLine);
    	}		

		System.out.println("end extracting");
    	
    	
    }
}
