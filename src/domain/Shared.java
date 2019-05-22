package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shared {

	private static int currentGroupid = 0;
	public static List<User> clients = new ArrayList<>();
	public static Map<String,Integer> groups = new HashMap<>();
	static int defaultGroupId = 0;
	static String defaultGroupName = "open";
	public static String userListCsv = null; 
	
	static{
		addNewGroup(defaultGroupName);
	}
	
	public static void addNewGroup(String groupName)
	{
		if(groups.get(groupName) == null)
		{
			groups.put(groupName, currentGroupid++);
		}
	}

	private Shared(){
		
	}
	
}	
