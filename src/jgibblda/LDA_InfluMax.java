/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgibblda;

import org.kohsuke.args4j.CmdLineParser;

import dataAccess.FileOperator;

import java.util.*;
import dataStructure.*;
/**
 *This is the LDA tool for Influence Maximization algorithm
 * @author Tianyi Chen
 */
public class LDA_InfluMax {
    
	
	public static void lda(Graph g, HashMap<String,Message>statusMap,HashMap<String,Message>commentMap){
		
	}
    public static Model estimationFromScratch(
    		double alpha,double beta,int nTopics,
    		int nIters,int saveStep,String dir,String dFile,int twords){
        //initialize the option
        LDACmdOption ldaOption=new LDACmdOption();
        ldaOption.inf=false;
        ldaOption.est=true;
        ldaOption.estc=false;
        ldaOption.K=nTopics;
        ldaOption.alpha=alpha;
        ldaOption.beta=beta;
        ldaOption.savestep=saveStep;
        ldaOption.twords=twords;
        ldaOption.niters=nIters;
        ldaOption.dir=dir;
        ldaOption.dfile=dFile;
		
        Estimator estimator = new Estimator();
		if(!estimator.init(ldaOption)){
			return null;
		}
		estimator.estimate();  
		return estimator.getModel();
    }
    public static Model getModel(String dir,String modelName){
    	//initialize the option
        LDACmdOption ldaOption=new LDACmdOption();
        Model model;
        int topicNum,wordNum,docNum;
        ldaOption.inf=false;
        ldaOption.est=false;
        ldaOption.estc=true;
        ldaOption.dir=dir;
        ldaOption.modelName=modelName;
        
        Estimator estimator = new Estimator();
		estimator.init(ldaOption);
		model=estimator.getModel();
		topicNum=model.K;
		docNum=model.M;
		wordNum=model.V;
		
		//read theta and phi from the model
		ArrayList<String> thetaFile=FileOperator.readFile(dir+"/"+modelName+".theta");
		ArrayList<String> phiFile=FileOperator.readFile(dir+"/"+modelName+".phi");
		int i=0,j=0;
		for(String thetaLine:thetaFile){
			String[] thetaStrings=thetaLine.split(" ");
			j=0;
			for(String thetaString:thetaStrings){
				model.theta[i][j]=Double.parseDouble(thetaString);
				j++;
			}
			i++;
		}
		
		i=0;j=0;
		for(String phiLine:phiFile){
			String[] phiStrings=phiLine.split(" ");
			j=0;
			for(String phiString:phiStrings){
				model.phi[i][j]=Double.parseDouble(phiString);
				j++;
			}
			i++;
		}
		
		return model;
        
    }
    public static void estimationFromPreviousModel(
    		 int nIters,int saveStep,
    		String dir,String model,int twords){
        //initialize the option
        LDACmdOption ldaOption=new LDACmdOption();
        ldaOption.inf=false;
        ldaOption.est=false;
        ldaOption.estc=true;
        
        ldaOption.savestep=saveStep;
        ldaOption.twords=twords;
        ldaOption.niters=nIters;
        ldaOption.dir=dir;
        ldaOption.modelName=model;
        
		
        Estimator estimator = new Estimator();
		estimator.init(ldaOption);
		estimator.estimate();     
    }
    public static void inferenceForPreData( 
    		int nIters,String dir,String model,String dFile,int twords){
    	//initialize the option
        LDACmdOption ldaOption=new LDACmdOption();
        ldaOption.inf=true;
        ldaOption.est=false;
        ldaOption.estc=false;
        
        ldaOption.twords=twords;
        ldaOption.niters=nIters;
        ldaOption.dir=dir;
        ldaOption.modelName=model;
        ldaOption.dfile=dFile;
        
        Inferencer inferencer = new Inferencer();
		inferencer.init(ldaOption);
		
		Model newModel = inferencer.inference();
	
		for (int i = 0; i < newModel.phi.length; ++i){
			//phi: K * V
			System.out.println("-----------------------\ntopic" + i  + " : ");
			for (int j = 0; j < 10; ++j){
				System.out.println(inferencer.globalDict.id2word.get(j) + "\t" + newModel.phi[i][j]);
			}
		}
    }
}
