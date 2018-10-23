package processors;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Deque;

import domain.ConnectedBy;
import domain.Shared;
import domain.User;
import exceptions.MalfunctionedFrame;

public class ChannelReader implements Runnable{	

	private static Deque<Integer> flushUser = new ArrayDeque<>();
	
	@Override
	public void run() {
		int maxSize = Shared.clients.size();
		for(int i=0 ; i < maxSize ; i++){
			queryClient(Shared.clients.get(i), i);
		}
		
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
		}
	}
}