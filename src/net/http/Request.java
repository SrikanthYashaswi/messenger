package net.http;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Method method;
    private String url;
    private String version;

    private Map<String,String> headers = new HashMap<String, String>();

    public void setRequestHead(String head){
        String blocks[] = head.split(" ");
        System.out.println(blocks.length);
        if(blocks.length < 3){
            method = Method.STREAM;
            return;
        }
        method = Method.valueOf(blocks[0]);
        url = blocks[1];
        version = blocks[2];
    }

    public void updateHeader(String key, String value){
        headers.put(key,value);
    }

    public Method getMethod(){
        return method;
    }
    public String getUrl(){
        return url;
    }
    public String getVersion(){
        return version;
    }

    public Map<String,String> getHeaders(){
        return headers;
    }
    
    public static Request parse(String body){
		String blocks[] = body.split("\n");
    	Request filler = new Request();
    	boolean first = true;
    	for(String block: blocks){
        	if(first){
                filler.setRequestHead(block);
                first = false;
            }
            String keyValue[] = block.split(":");
            if(keyValue.length == 2){
            	filler.updateHeader(keyValue[0],keyValue[1]);
            }
        }
        return filler;
    }
}
