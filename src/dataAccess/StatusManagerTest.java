package dataAccess;

import static org.junit.Assert.*;

import org.junit.Test;

public class StatusManagerTest {

	@Test
	public final void test() {
		DataBaseAccessor access=new DataBaseAccessor("Sinawler", "localhost:1433","sa","1234");
		StatusManager sm=new StatusManager(access);
		System.out.println(sm.statusList.get("4688397455"));
		
	}

}
