package dispatchers;

import domain.ConnectedBy;
import domain.User;

import java.io.IOException;

public class MessageDispatcher implements Dispatcher {

    @Override
    public void process(User sender, User receipient, String message) throws IOException
    {
        if( receipient.getConnectionType().equals(ConnectedBy.BROWSER))
        {
            return;
        }
        receipient.say(sender.getName()+" : "+message);
    }
}
