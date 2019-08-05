package processors;

import java.io.IOException;

import dispatchers.Dispatcher;
import dispatchers.MessageDispatcher;
import dispatchers.TypingDispatcher;
import domain.User;
import exceptions.MalfunctionedFrame;

public class WebSocketMessageProcessor
{

	private WebSocketMessageProcessor(){
		
	}
	
	public static void processMessage(User user, String message) throws IOException, MalfunctionedFrame
    {
        Dispatcher dispatcher = new MessageDispatcher();

        if(message.startsWith("@ping"))
        {
            String time = message.split(":")[1];
            user.say("@pong:"+time);
            return;
        }

        if(CommandResolver.resolve(message, user))
        {
            return;
        }

        if(message.startsWith("@typing"))
        {
            dispatcher = new TypingDispatcher();

        }

        MessageDeliveryGuy.sendToUserGroup(user, message, dispatcher);
	}
}
