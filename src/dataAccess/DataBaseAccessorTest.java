package dataAccess;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class DataBaseAccessorTest {

	@Test
	public void test() {
		DataBaseAccessor access=new DataBaseAccessor("Sinawler", "localhost:1433","sa","1234");
		
		System.out.println("getting data...");
		ArrayList<String[]>statusList=access.getDataInArrayList("SELECT * FROM uniq_end_statuses_backup");
		System.out.println("data got");
		
		System.out.println("totoal number of statuses: "+statusList.size());
		for(int i=0;i<18;i++){
			System.out.print(" "+statusList.get(0)[i]);
		}
		System.out.println(" ");
		for(int i=0;i<18;i++){
			System.out.print(" "+statusList.get(1)[i]);
		}
		
//    	ArrayList<String[]> recordList=new ArrayList<String[]>();
//    	String[] record=new String[5];
//		recordList.add(record);
//		String[] record2=new String[5];
//		recordList.add(record2);
//		System.out.println(recordList.size());

	}

}
