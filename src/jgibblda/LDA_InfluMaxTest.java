package jgibblda;

import static org.junit.Assert.*;

import org.junit.Test;

public class LDA_InfluMaxTest {

	@Test
	public final void testEstimationFromScratch() {
		String dir="C:/project/fin_yr/prj/PreProcess/PreProcess_LDAMap/sampleTest/lda";
		String dFile="sampleStatusContent1727548672Iter.txt";
		LDA_InfluMax.estimationFromScratch(1,0.01,10,
				10000,1000,dir,dFile,50);
	}

//	@Test
//	public final void testEstimationFromPreviousModel() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testInferenceForPreData() {
//		fail("Not yet implemented"); // TODO
//	}

}
