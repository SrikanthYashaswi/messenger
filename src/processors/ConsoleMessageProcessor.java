package processors;

import dispatchers.Dispatcher;
import dispatchers.MessageDispatcher;
import dispatchers.TypingDispatcher;
import domain.User;
import exceptions.MalfunctionedFrame;

import java.io.IOException;

public class ConsoleMessageProcessor
{
    private ConsoleMessageProcessor()
    {

    }

    public static void processMessage(User user, String message) throws IOException, MalfunctionedFrame
    {
        if(CommandResolver.resolve(message, user))
        {
            return;
        }

        MessageDeliveryGuy.sendToUserGroup(user, message, new MessageDispatcher());
    }
}
