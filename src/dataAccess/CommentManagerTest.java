package dataAccess;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import dataStructure.Comment;

public class CommentManagerTest {

	@Test
	public final void test() {
		DataBaseAccessor access=new DataBaseAccessor("Sinawler", "localhost:1433","sa","1234");
		CommentManager cm=new CommentManager(access);
		HashMap<String, Comment> cmMap=cm.getCommentMap();
		System.out.println(cmMap.size());
	}

}
