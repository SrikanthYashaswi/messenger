package domain;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import exceptions.MalfunctionedFrame;
import net.http.Request;
import net.http.RequestReader;
import processors.MessageProcessor;

public enum ConnectedBy{
	
	CONSOLE {
		@Override
		public void handle(User user) throws IOException, MalfunctionedFrame {
			String userSays = user.whatSaying();
			MessageProcessor.processMessage(user,userSays);
		}
	},
	
	WEBSOCKET {
		@Override
		public void handle(User user) throws IOException, MalfunctionedFrame {
			String userSays = user.whatSaying();
			MessageProcessor.processMessage(user,userSays);
		}
	},
	
	BROWSER {
		@Override
		public void handle(User user) throws IOException, NoSuchAlgorithmException {
			
			InputStream inputStream = user.client.getInputStream();
			
			if(!user.handshakeDone){
				
				String message = RequestReader.readStream(inputStream, inputStream.available());
				Request request = Request.parse(message);
				
				if(request.getHeaders().containsKey("Upgrade")){
					user.doWebsocketHandshake(request);
				}
				else{
					inputStream.close();
					throw new IOException("Channel closed");
				}
			}
		}
	};

	public abstract void handle(User user) throws IOException,NoSuchAlgorithmException,MalfunctionedFrame;
}
