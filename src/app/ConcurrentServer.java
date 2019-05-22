package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import domain.Shared;
import domain.User;
import processors.ChannelReader;
import processors.MessageProcessor;

public class ConcurrentServer {
	
	private static final int PROCESSOR_INITIAL_DELAY = 0;
	private static final int UPDATE_EVENT_FREQUENCY = 2;
	private static final int THREAD_POOL_SIZE = 1;
	private static final boolean ALWAYS_RUNNING = true;
	private static final ChannelReader channelReader = new ChannelReader();
	private static final ScheduledExecutorService ONLINE_PEOPLE_SERVICE = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		
	public static void main(String[] arg) throws IOException, InterruptedException
	{
		int port;
		if(arg.length == 0){
			port = 8080;
		}
		else{
			port = Integer.parseInt(arg[0]);
		}
		
		new ConcurrentServer().start(port);
	}
	
	public void start(int port) throws IOException, InterruptedException{
		
		try(ServerSocket server = new ServerSocket(port)){
			
			Thread channelReaderThread = new Thread(channelReader);
			channelReaderThread.start();
			
			ONLINE_PEOPLE_SERVICE.scheduleAtFixedRate(new Runnable(){
				@Override
				public void run() {
					try {
						MessageProcessor.updateOnlineUsers();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, PROCESSOR_INITIAL_DELAY, UPDATE_EVENT_FREQUENCY, TimeUnit.SECONDS);
			
			while(ALWAYS_RUNNING){
				User client = new User(server.accept());
				Shared.clients.add(client);
			}
		}
	}
}