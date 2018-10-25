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
	public void run() {
		int maxSize = Shared.clients.size();
		StringJoiner joiner = new StringJoiner(",");
		for(int i=0 ; i < maxSize ; i++){
			
			User user = Shared.clients.get(i);
			
			queryClient(user, i);
			
			if(user.name!=null)
				joiner.add(user.name);
		}
		
		Shared.userListCsv = joiner.toString();
		
		while(!flushUser.isEmpty()){
			int id = flushUser.pop();
			Shared.clients.remove(id);
		}
	}
	
	private void queryClient(User user,int index){
		try {
			InputStream inputStream = user.client.getInputStream();
			
			if( inputStream.available() <= 0 ){
				return;
			}
			
			ConnectedBy connection = user.getConnectionType();
			
			connection.handle(user);

			user.print();
		}
		catch(IOException | MalfunctionedFrame | NoSuchAlgorithmException c){
			flushUser.push(index);
			System.out.println(user.name +" flushed.");
		}
	}
}