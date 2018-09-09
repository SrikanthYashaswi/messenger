package net.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    PrintWriter responseWriter;

    public ResponseHandler(Socket requestConnection) throws IOException{
        OutputStream response = requestConnection.getOutputStream();
        responseWriter = new PrintWriter(response);
        responseWriter.println("HTTP/1.1 200 OK");
        setHeaders(basicHeader());
        putHtml();
        responseWriter.close();
    }

    private void setHeaders(Map<String,String> headers){
        for(String key: headers.keySet()){
            responseWriter.println(key+": "+headers.get(key));
        }
    }

    private void putHtml(){
        responseWriter.println("\r");
        responseWriter.print("<html><h1>Hello</h1></html>");
        responseWriter.print("\r\n");
    }

    private Map<String,String> basicHeader(){
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Server","Who The Fuck are you!");
        headers.put("Connection", "Closed");
        headers.put("Content-Type" ,"text/html; charset=iso-8859-1");
        return headers;
    }
}
