package dispatchers;

import domain.ConnectedBy;
import domain.User;

import java.io.IOException;

public class TypingDispatcher implements Dispatcher {
    @Override
    public void process(User sender, User receipient, String message) throws IOException
    {
        if (receipient.getConnectionType().equals(ConnectedBy.CONSOLE) || receipient.getConnectionType().equals(ConnectedBy.BROWSER) )
        {
            return;
        }
        receipient.say(message+":"+sender.getName());
    }
}
