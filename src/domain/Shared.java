package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shared {

	private static int currentGroupid = 0;
	public static List<User> clients = new ArrayList<>();
	public static Map<String,Integer> groups = new HashMap<>();
	static int harmony = 0;
	
	static{
		addNewGroup("harmony");
	}
	
	public static void addNewGroup(String groupName){
		groups.put(groupName, currentGroupid++);
	}

	private Shared(){
		
	}
	
}	