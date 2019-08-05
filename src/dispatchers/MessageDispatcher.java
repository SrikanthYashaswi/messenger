package dispatchers;

import domain.User;

import java.io.IOException;

public class MessageDispatcher implements Dispatcher {

    @Override
    public void process(User sender, User receipient, String message) throws IOException
    {
        receipient.say(sender.getName()+" : "+message);
    }
}
