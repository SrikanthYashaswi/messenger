package net.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import util.Util;

public class RequestReader {
	
	private RequestReader(){
		
	}
	
    public static String getBody(Socket requestConnection) throws IOException {
        InputStream reqStream = requestConnection.getInputStream();
        return readStream(reqStream,reqStream.available());
    }

	public static String readStream(InputStream reqStream, int length) throws IOException {
		List<Byte> rawBody = new ArrayList<>();
		byte[] requestBody;
		requestBody = new byte[length];
		int read = reqStream.read(requestBody);
		
		if (read == 0){
			return "";
		}
		
		for (byte b : requestBody) {
		    rawBody.add(b);
		}
		return new String(Util.toArray(rawBody));
	}
	public static byte[] readBytes(InputStream reqStream, int length) throws IOException{
		byte[] requestBody;
		requestBody = new byte[length];
		reqStream.read(requestBody);
		return requestBody;
	}

    public static Request getRequest(Socket requestConnection) throws IOException{
    	return Request.parse(getBody(requestConnection));
    }
    
    
}
