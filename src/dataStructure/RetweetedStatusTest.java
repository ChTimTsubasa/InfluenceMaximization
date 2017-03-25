package dataStructure;

import static org.junit.Assert.*;

import org.junit.Test;

public class RetweetedStatusTest {

	@Test
	public final void test() {
		Status s=new RepostStatus(" "," ","f");
		System.out.println(s.getClass());
		System.out.println(s.getClass().equals(Status.class));
		Status s2= new Status("","");
		System.out.println(s2.getClass().equals(RepostStatus.class));
		System.out.println(s.getClass().asSubclass(Status.class));
	}

}
