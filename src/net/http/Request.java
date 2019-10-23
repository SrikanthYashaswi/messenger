package net.http;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private static final String QUERY_REGEX = "\\?(.*)";
    private Method method;
    private String url;
    private String version;
    private Map<String,String> queries = new HashMap<String,String>();

    private Map<String,String> headers = new HashMap<>();

    public void setRequestHead(String head){
        String[] blocks = head.split(" ");
        if(blocks.length < 3){
            method = Method.STREAM;
            return;
        }
        method = Method.valueOf(blocks[0]);
        url = blocks[1];
        version = blocks[2];
        evaluateUrlQueries();
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

    public String getQueryValue(String qName)
    {
        return queries.get(qName);
    }

    private void evaluateUrlQueries()
    {
        Pattern r = Pattern.compile(QUERY_REGEX);

        Matcher matcher = r.matcher(this.url);
        if(matcher.find())
        {
            String q[] = matcher.group(1).split("&");
            for(int i = 0 ; i < q.length ; i++)
            {
                String rq[] = q[i].split("=");
                if(rq.length == 2)
                {
                    queries.put(rq[0], rq[1]);
                }
            }
        }
    }
    
    public static Request parse(String body){
		String[] blocks = body.split("\n");
    	Request filler = new Request();
    	boolean first = true;
    	for(String block: blocks){
        	if(first){
                filler.setRequestHead(block);
                first = false;
            }
            String[] keyValue = block.split(":");
            if(keyValue.length == 2){
            	filler.updateHeader(keyValue[0],keyValue[1].trim());
            }
        }
        return filler;
    }
}
