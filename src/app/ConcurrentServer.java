package app;

import java.io.IOException;
import java.net.ServerSocket;

import domain.Shared;
import domain.User;
import processors.MessageChecker;

public class ConcurrentServer {
	
	public static void main(String arg[]) throws IOException
	{
		new ConcurrentServer().start(8080);
	}
	
	public void start(int port) throws IOException{
		ServerSocket server = new ServerSocket(port);
		new MessageChecker();
		while(true){
			try{
				User client = new User(server.accept());
				Shared.clients.add(client);
			}
			catch(IOException c){
				c.printStackTrace();
			}
		}
	}
}









