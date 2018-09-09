package processors;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import domain.Shared;
import domain.User;
import net.http.RequestReader;

public class MessageChecker implements Runnable{
	
	Thread thread;
	
	public MessageChecker(){
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		while(true){
			sleep();
			List<User> users = Shared.clients;
			for(User user: users){
				try {
					queryClient(user);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void queryClient(User user) throws IOException{
		InputStream inputStream = user.client.getInputStream();
		if(inputStream.available()>0){
			String message = RequestReader.readStream(inputStream, inputStream.available()).replaceAll("\r\n", "");
			System.out.println(message);
			processMessage(user, message);
		}
	}
	
	private void processMessage(User user, String message) throws IOException{
		
		if(user.name == null){
			user.setUsername(message);
			return;
		}
		
		if(message.startsWith("/join")){
			String blocks[] = message.split(" ");
			if(blocks.length  == 2){
				user.joinGroup(blocks[1]);
				user.serverSay("Joined "+blocks[1]);
			}
			else
			{
				user.serverSay("Hey Z0^^B!#.Man should nt give blank group names");
			}
		}
		else
		{
			for(User u: Shared.clients){
				if(u.uniqueId == user.uniqueId)
					continue;
				if(user.groupId == u.groupId){
					u.say(user.name+" : "+message);
				}
			}
		}
	}
	
	public void sleep(){
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}