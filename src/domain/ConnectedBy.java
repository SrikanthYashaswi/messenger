package domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.lang3.StringEscapeUtils;

import exceptions.MalfunctionedFrame;
import net.http.Request;
import net.http.RequestReader;
import processors.ConsoleMessageProcessor;
import processors.MessageDeliveryGuy;
import processors.WebSocketMessageProcessor;

public enum ConnectedBy{
	
	CONSOLE {
		@Override
		public void handle(User user) throws IOException, MalfunctionedFrame
        {
			String userSays = user.whatSaying();
            if(userSays.trim().equals(""))
            {
                return;
            }
			ConsoleMessageProcessor.processMessage(user,userSays);
		}
	},
	
	WEBSOCKET {
		@Override
		public void handle(User user) throws IOException, MalfunctionedFrame
        {
			String userSays = user.whatSaying();
            if(userSays.trim().equals(""))
            {
                return;
            }
			WebSocketMessageProcessor.processMessage(user,userSays);
		}
	},
	
	BROWSER {
		@Override
		public void handle(User user) throws IOException, NoSuchAlgorithmException {
			InputStream inputStream = user.client.getInputStream();

			String message = RequestReader.readStream(inputStream, inputStream.available());

			Request request = Request.parse(message);

			if(request.getHeaders().containsKey("Upgrade")){
				user.doWebsocketHandshake(request);
			}
			else
			{
				try{
					String groupName= request.getQueryValue("group");
					String urlEncodedData = request.getQueryValue("data");
					String data = java.net.URLDecoder.decode(urlEncodedData, StandardCharsets.UTF_8.name());
					MessageDeliveryGuy.sendToGroup(groupName, StringEscapeUtils.unescapeJava(data));
				}
				catch(Exception c)
				{
					c.printStackTrace();
				}

				user.sendOk();
				inputStream.close();
			}
		}
	};

	public abstract void handle(User user) throws IOException, NoSuchAlgorithmException, MalfunctionedFrame;
}
