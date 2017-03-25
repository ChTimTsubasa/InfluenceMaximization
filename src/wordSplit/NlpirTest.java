package wordSplit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import utils.SystemParas;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class NlpirTest { 

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				"C:\\project\\fin_yr\\prj\\PreProcess\\PreProcess_LDAMap\\ICTCLAS2015\\lib\\win64\\NLPIR", CLibrary.class);
				 
		public int NLPIR_Init(String sDataPath, int encoding,
				String sLicenceCode);
				
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

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

	//public void depart(String a, String b){
    public static void splitWord(String rFileName,String wFileName){
	//public static void main(String args[]){	
		System.out.println("Start");
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

		String filePath =rFileName;//"C:\\project\\fin_yr\\prj\\PreProcess\\PreProcess_LDAMap\\ICTCLAS2015\\test\\用户词典.txt";
		File file_write = new File(wFileName);
		try {
		       String encoding="GBK";
		        File file=new File(filePath);
		        if(file.isFile() && file.exists()){ //判断文件是否存在
		            InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
		            BufferedReader bufferedReader = new BufferedReader(read);
		            String lineTxt = null;
		            while((lineTxt = bufferedReader.readLine()) != null){
		            	String sInput =	lineTxt;
		            
						try {
							nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);
							   try {
								   if (!file_write.exists()) {
									   file_write.createNewFile();
								   }
						           RandomAccessFile randomFile = new RandomAccessFile(file_write, "rw");   
						           long fileLength = randomFile.length();  
						         //将写文件指针移到文件尾。  
						           randomFile.seek(fileLength);  
						           
						           String newstr=nativeBytes+"\r\n";
						           randomFile.write(newstr.getBytes("GB2312"));   
						           
						       	   randomFile.close(); 
								  } catch (IOException e) {
								   e.printStackTrace();
								  }
							//System.out.println("分词结果为： " + nativeBytes);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
		            }
		            read.close();  
		        }else{
		            System.out.println("找不到指定的文件");
		        }            
			}catch (Exception e) {
			            System.out.println("读取文件内容出错");
			            e.printStackTrace();
		    }
			CLibrary.Instance.NLPIR_Exit(); 
			System.out.println("end");
	}
}
