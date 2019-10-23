package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shared {

    public static List<User> clients = new ArrayList<>();

	public static String userListCsv = null;

	public static long LOOP_TIME = 0;

    public static SystemInfo sysinfo = new SystemInfo();

    public static int count= 0;

    private Shared(){
		
	}
}	
