package processors;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringJoiner;

import domain.ConnectedBy;
import domain.Shared;
import domain.User;
import exceptions.MalfunctionedFrame;

public class ChannelReader implements Runnable{

	private static Deque<Integer> flushUser = new ArrayDeque<>();
	
	@Override
	public void run() 
	{
		while(true)
		{
			try
			{
				Thread.sleep(100);
				process();
			} catch (InterruptedException e)
			{

			}
		}
	}
	
	private void process(){
		int maxSize = Shared.clients.size();
		
		StringJoiner joiner = new StringJoiner(",");
		
		for(int i = 0 ; i < maxSize ; i++)
		{
			User user = Shared.clients.get(i);

			queryClient(user, i);
			
			if(user.getName()!=null)
			{
				joiner.add(user.getName());
			}
				
			if(user.getIdleTime() >= 1800000)
			{
				flushUser.push(i);
			}
		}
		
		Shared.userListCsv = joiner.toString();
		
		while(!flushUser.isEmpty())
		{
			int id = flushUser.pop();
			Shared.clients.get(id).bye();
			Shared.clients.remove(id);
		}
	}
	
	private void queryClient(User user,int index)
	{
		try {
			InputStream inputStream = user.getInputStream();

			if( inputStream.available() <= 0 )
			{
				user.increaseIdleTime();
				return;
			}

			ConnectedBy connection = user.getConnectionType();

			connection.handle(user);

			user.resetIdleTime();
		}
		catch (IOException | MalfunctionedFrame | NoSuchAlgorithmException | NullPointerException c)
		{
			flushUser.push(index);
			c.printStackTrace();
			System.out.println(user.getName() + " flushed.");
		}
	}
}