package dataAccess;

import static org.junit.Assert.*;

import org.junit.Test;

import dataStructure.*;

public class UserManagerTest {

	@Test
	public final void test() {
		DataBaseAccessor access=new DataBaseAccessor("Sinawler", "localhost:1433","sa","1234");
		UserManager um=new UserManager(access);
		System.out.println(um.network.getVertsNum());
		System.out.println(um.network.getDirectedEdgeNum());	
		
		Graph network=um.network;
		
		Graph sampleNetwork=network.getSubGraph("1266321801", 300);
		System.out.println(sampleNetwork.getVertsNum());
		System.out.println(sampleNetwork.getDirectedEdgeNum());
	}

}
