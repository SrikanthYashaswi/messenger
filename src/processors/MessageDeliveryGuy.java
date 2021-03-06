package processors;

import dispatchers.Dispatcher;
import domain.ConnectedBy;
import domain.Shared;
import domain.User;

import java.io.IOException;

public class MessageDeliveryGuy
{

    /**
     * This Method will send all messages to the group the sending user belongs to.
     * @param user
     * @param message
     * @param dispatcher
     * @throws IOException
     */

    public static void sendToUserGroup(User user, String message, Dispatcher dispatcher) throws IOException
    {
        for(User u: Shared.clients)
        {
            if( u.getId().equals(user.getId()) )
                continue;
            if(user.getGroupName().equals(u.getGroupName()))
            {
                dispatcher.process(user,u,message);
            }
        }
    }

    public static void sendToGroup(String group, String message) throws IOException
    {
        for(User u: Shared.clients)
        {
            if( u.getConnectionType().equals(ConnectedBy.BROWSER))
            {
                return;
            }

            if(group.equals(u.getGroupName()))
            {
                u.say(message);
            }
        }
    }

    public static void sendToEveryone(String message) throws IOException
    {
        for(User u: Shared.clients)
        {
            if(u.getConnectionType().equals(ConnectedBy.BROWSER))
            {
                return;
            }
            u.say(message);
        }
    }
}
