package InfluMax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dataAccess.FileOperator;

public class Evaluate {
	public static void main(String args[]){
		
		String resultFolder	=	"result/";
		
		String 	rootdir		=	"sampleTest/";
		String 	sourceID	=	"1864252027/";
		String networkSize	=	"1100/";
		
		String modelDir		=	rootdir+sourceID+networkSize;

		String resultDir	=	modelDir+resultFolder;
		NodeFileOperator nfo=	new NodeFileOperator(modelDir,0.2,0.05,2);

		ArrayList<String> fileLines=new ArrayList<>();
		//get the target Message Map
		Iterator<String>iterM=nfo.targetMessageMap.keySet().iterator();
		HashMap<String ,List<SelectedSeedUser>> selectedSeedUser=nfo.getSelectSeedUsers();
		HashMap<String ,List<ActualSeedUser>> actualSeedUserSet=nfo.getActualSeedUsers();
		
		
		System.out.println("start");
		while(iterM.hasNext()){
			String messageID=iterM.next();
			//System.out.println(messageID);
			List<SelectedSeedUser>selectList=selectedSeedUser.get(messageID);
			List<ActualSeedUser>actualList=actualSeedUserSet.get(messageID);
			for(SelectedSeedUser select:selectList){
				for(ActualSeedUser actual:actualList){
					ResultSet rs=new ResultSet(select, actual);
					fileLines.add(rs.toString());
				}
			}
		}
		
		
		System.out.println("constant");
		iterM=nfo.targetMessageMap.keySet().iterator();
		while(iterM.hasNext()){
			String messageID=iterM.next();
			String constant="constant";
			List<SelectedSeedUser>selectList=selectedSeedUser.get(constant);
			List<ActualSeedUser>actualList=actualSeedUserSet.get(messageID);
			for(SelectedSeedUser select:selectList){
				//int actualCon=0;
				for(ActualSeedUser actual:actualList){
					ResultSet rs=new ResultSet(select, actual);
					fileLines.add(rs.toString());
					
				}
			}
		}

		FileOperator.writeFile(resultDir+"prf.result", fileLines);
		System.out.println("end");
	}
}
