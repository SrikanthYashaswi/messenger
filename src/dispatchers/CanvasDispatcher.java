package dispatchers;

import domain.ConnectedBy;
import domain.User;

import java.io.IOException;

/**
 * Created by shrk on 22/08/19.
 */
public class CanvasDispatcher implements  Dispatcher {
    @Override
    public void process(User sender, User receipient, String message) throws IOException
    {
        if (receipient.getConnectionType().equals(ConnectedBy.CONSOLE))
        {
            return;
        }
        receipient.say(message);
    }
}
