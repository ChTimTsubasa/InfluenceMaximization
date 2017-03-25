package InfluMax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import dataAccess.FileOperator;
import dataStructure.Message;
import dataStructure.MessageListOperator;

public class NodeFileOperator {
	String modelDir;
	String repostDir;
	String repostOutDir;
	String celfOutDir;
	String repostFolder	=	"repost/";
	String repostOutput =	"repostOutput/";
	String CELFOutFolder=	"output/";
	String dataName		=	"IC_CelfPlus_Greedy.txt";
	
	HashMap<String,Message>targetMessageMap;
	List<String>	repostEnd	=	new LinkedList<>();
	List<String>	selectedEnd	=	new LinkedList<>();
	
	public 	NodeFileOperator(String modelDir,double thresholdMax,double thresholdMin,double thresholdStep){
		this.modelDir=modelDir;
		this.repostDir	=	modelDir+repostFolder;
		this.repostOutDir	=	repostDir+repostOutput;
		this.celfOutDir	=	modelDir+CELFOutFolder;
 		this.targetMessageMap=MessageListOperator.readMessageFromFile(modelDir+"targetMessages.status");

		this.repostEnd.add("Exact");
		for (double threshold=thresholdMax; threshold>=thresholdMin;threshold=threshold/thresholdStep){
			this.repostEnd.add("Sim"+threshold);}
		for (int i=0;i<4;i++){
			this.selectedEnd.add("pGraph"+i);
			this.selectedEnd.add("pGraphNormal"+i);
		}
	}
	
	public static void main(String args[]){
		String 	rootdir		=	"sampleTest/";
		String 	sourceID	=	"1864252027/";
		String networkSize	=	"1100/";
		
		String modelDir		=	rootdir+sourceID+networkSize;
		NodeFileOperator nfo=	new NodeFileOperator(modelDir,0.2,0.05,2);
		
		//get the target Message Map
		
		Iterator<String>iterM=nfo.targetMessageMap.keySet().iterator();
		
		System.out.println("read and reprint the actual seed user");
		//read and reprint the actual seed to the repost Folder
		while(iterM.hasNext()){
			String targetMessageID=iterM.next();
			for(String end:nfo.repostEnd){
				String[] usersOfThisMessage=nfo.getSeedUserFromFile(nfo.repostOutDir+"targetrepostNetwork"+targetMessageID+end+".inf/"+nfo.dataName);
				if(usersOfThisMessage==null)
					continue;
				nfo.writeSeedUserToFile(nfo.repostDir,"repostUser"+targetMessageID+end+".nodes",usersOfThisMessage);
			}
			
		}
		
		System.out.println("read and reprint the selected seed user");
		
		//read and reprint the selected seed to the repostFolder
		iterM=nfo.targetMessageMap.keySet().iterator();
		while(iterM.hasNext()){
			String targetMessageID=iterM.next();
			for(String end:nfo.selectedEnd){
				String[] usersOfThisMessage=nfo.getSeedUserFromFile(nfo.celfOutDir+"target"+targetMessageID+end+".inf/"+nfo.dataName);
				if(usersOfThisMessage==null)
					continue;
				nfo.writeSeedUserToFile(nfo.celfOutDir,"selectedUser"+targetMessageID+end+".nodes",usersOfThisMessage);
			}
			
		}
		//constant
		String[] usersOfThisMessage=nfo.getSeedUserFromFile(nfo.celfOutDir+"targetconstant4.inf/"+nfo.dataName);
		nfo.writeSeedUserToFile(nfo.celfOutDir,"selectedUserconstant.nodes",usersOfThisMessage);
	
		
	}

	public void writeSeedUserToFile(String dir,String name, String[] userIDs){
		ArrayList<String>fileLines=new ArrayList<>(userIDs.length);
		for(String userID:userIDs){
			fileLines.add(userID);
		}
		FileOperator.writeFile(dir+name, fileLines);
	}
	
	public  String[] getSeedUserFromFile(String dir) {

		File file=new File(dir);
		if (!file.exists())
			return null;
		ArrayList<String>fileLines=FileOperator.readFile(dir);
		String[] userID=new String[fileLines.size()];
		int index=0;
		for(String line:fileLines){
			userID[index]=line.split(" ")[0];
			index++;
		}
		return userID;
	}
	
	public HashMap<String ,List<SelectedSeedUser> > getSelectSeedUsers(){
		HashMap<String ,List<SelectedSeedUser>> selectSeedUserSet=new LinkedHashMap<>();
		//read and reprint the selected seed to the repostFolder
		Iterator<String> iterM=this.targetMessageMap.keySet().iterator();
		while(iterM.hasNext()){
			String targetMessageID=iterM.next();
			for(String end:this.selectedEnd){
				String[] usersOfThisMessage=this.getSeedUserFromFile(this.celfOutDir+"selectedUser"+targetMessageID+end+".nodes");
				List<SelectedSeedUser> listForMessage;
				SelectedSeedUser ssuser;
				switch(end){
				case "pGraph0":
					ssuser=new SelectedSeedUser(targetMessageID, usersOfThisMessage, false, 0);
					break;
				case "pGraph1":
					ssuser=new SelectedSeedUser(targetMessageID, usersOfThisMessage, false, 1);
					break;
				case "pGraph2":
					ssuser=new SelectedSeedUser(targetMessageID, usersOfThisMessage, false, 2);
					break;
				case "pGraph3":
					ssuser=new SelectedSeedUser(targetMessageID, usersOfThisMessage, false, 3);
					break;
				case "pGraphNormal0":
					ssuser=new SelectedSeedUser(targetMessageID, usersOfThisMessage, true, 0);
					break;
				case "pGraphNormal1":
					ssuser=new SelectedSeedUser(targetMessageID, usersOfThisMessage, true, 1);
					break;
				case "pGraphNormal2":
					ssuser=new SelectedSeedUser(targetMessageID, usersOfThisMessage, true, 2);
					break;
				default://case "pGraphNormal3":
					ssuser=new SelectedSeedUser(targetMessageID, usersOfThisMessage, true, 3);
					
				}
				if(!selectSeedUserSet.containsKey(targetMessageID)){
					listForMessage=new LinkedList<>();
					selectSeedUserSet.put(targetMessageID, listForMessage);
				}
				else{
					listForMessage=selectSeedUserSet.get(targetMessageID);
				}
				listForMessage.add(ssuser);
			}
		}
		//constant
		String[] usersOfThisMessage=this.getSeedUserFromFile(this.celfOutDir+"selectedUserconstant.nodes");
		List<SelectedSeedUser> listForConstant=new LinkedList<>();
		SelectedSeedUser ssuser=new SelectedSeedUser("constant", usersOfThisMessage, false, 4);
		listForConstant.add(ssuser);
		selectSeedUserSet.put("constant", listForConstant);
		
		return selectSeedUserSet;
	}
	
	public HashMap<String ,List<ActualSeedUser> > getActualSeedUsers(){
		String[]exactOrSim={"Exact","Sim"};
		HashMap<String ,List<ActualSeedUser>> actualSeedUserSet=new LinkedHashMap<>();
		//read and reprint the selected seed to the repostFolder
		Iterator<String> iterM=this.targetMessageMap.keySet().iterator();
		String end;
		while(iterM.hasNext()){
			String targetMessageID=iterM.next();
			List<ActualSeedUser> listForMessage;
			ActualSeedUser asuser;
			for(String eOs :exactOrSim){
				if(eOs.equals("Exact")){
					end="Exact";
					String[] usersOfThisMessage=this.getSeedUserFromFile(this.repostDir+"repostUser"+targetMessageID+end+".nodes");
					if(usersOfThisMessage!=null){
						asuser=new ActualSeedUser(targetMessageID, usersOfThisMessage, true, 1);
						
						if(!actualSeedUserSet.containsKey(targetMessageID)){
							listForMessage=new LinkedList<>();
							actualSeedUserSet.put(targetMessageID, listForMessage);
						}
						else{
							listForMessage=actualSeedUserSet.get(targetMessageID);
						}
						listForMessage.add(asuser);
						
					}
				}
				else{
					for(double threshold=0.2; threshold>=0.05;threshold=threshold/2){
						end="Sim"+threshold;
						String[] usersOfThisMessage=this.getSeedUserFromFile(this.repostDir+"repostUser"+targetMessageID+end+".nodes");
						if(usersOfThisMessage!=null){
							asuser=new ActualSeedUser(targetMessageID, usersOfThisMessage, false, threshold);
							
							if(!actualSeedUserSet.containsKey(targetMessageID)){
								listForMessage=new LinkedList<>();
								actualSeedUserSet.put(targetMessageID, listForMessage);
							}
							else{
								listForMessage=actualSeedUserSet.get(targetMessageID);
							}
							listForMessage.add(asuser);
						}
					}
				}
				
				
			}
		}
		
		return actualSeedUserSet;
	}
}
