package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import domain.Shared;
import domain.User;
import processors.ChannelReader;

public class ConcurrentServer {
	
	private static final int PROCESSOR_INITIAL_DELAY = 0;
	private static final int PROCESSOR_FREQUENCY = 1;
	private static final int THREAD_POOL_SIZE = 1;
	private static final boolean ALWAYS_RUNNING = true;
	private static final ScheduledExecutorService PROCESSOR_SERVICE = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		
	public static void main(String[] arg) throws IOException, InterruptedException
	{
		new ConcurrentServer().start(80);
	}
	
	public void start(int port) throws IOException, InterruptedException{
		
		try(ServerSocket server = new ServerSocket(port)){
			
			PROCESSOR_SERVICE.scheduleAtFixedRate(new ChannelReader(), PROCESSOR_INITIAL_DELAY, PROCESSOR_FREQUENCY, TimeUnit.MILLISECONDS);
			
			while(ALWAYS_RUNNING){
				User client = new User(server.accept());
				Shared.clients.add(client);
			}
		}
	}
}