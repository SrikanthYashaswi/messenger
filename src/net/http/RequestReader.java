package net.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.RequestHandler;
import util.Util;

public class RequestReader {

    public RequestReader(Socket requestConnection) throws IOException {
        String requestBody = getBody(requestConnection);

        new ResponseHandler(requestConnection);
    }

    public static String getBody(Socket requestConnection) throws IOException {
        InputStream reqStream = requestConnection.getInputStream();
        int length;
        
        while((length = reqStream.available())!= 0){
        	readStream(reqStream, length);
        }
    }

	public static String readStream(InputStream reqStream, int length) throws IOException {
		List<Byte> rawBody = new ArrayList<Byte>();
		byte[] requestBody;
		requestBody = new byte[length];
		reqStream.read(requestBody);
		for (byte b : requestBody) {
		    rawBody.add(b);
		}
		return new String(Util.toArray(rawBody));
	}

    public static Request getRequest(Socket requestConnection) throws IOException{
    	return Request.parse(getBody(requestConnection));
    }
    
    
}
