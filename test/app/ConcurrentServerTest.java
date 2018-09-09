package app;

import java.io.IOException;
import java.net.Socket;

import net.http.RequestReader;


class Properties{
	public static final int PORT = 8080;
}

class SimpleThread implements Runnable{

	Thread thread;
	
	public SimpleThread(){
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		try{
			ConcurrentServer server = new ConcurrentServer();
			server.start(Properties.PORT);
		}
		catch(Exception c){
			c.printStackTrace();
		}
	}
	
}

class g{
	public static void assertTrue(String subject,Object actual, Object expected){
		String status = actual.equals(expected) ? subject+" passes": subject+" failed";   
		System.out.println(status);
	}
}

public class ConcurrentServerTest {
	
	
	public static void main() throws IOException{
		new SimpleThread();
		testReply();
		
	}
	
	public static void testReply() throws IOException{
		Socket client = new Socket("localhost",Properties.PORT);
		String initialMessage = RequestReader.readStream(client.getInputStream(), client.getInputStream().available());
		g.assertTrue("first message", "Who are you", initialMessage);
	}
	
	
}
