package app;

import java.util.HashMap;
import java.util.Map;

public class Debug {
	public static void main(String argp[]){
		Map<String,String> s = new HashMap<String, String>();
		s.put("srikanth", "yashaswi");
		s.put("yali", "ramesh");
		System.out.println(s.keySet().stream().findAny().get());
	}
}
