package dispatchers;

import domain.User;

import java.io.IOException;

public interface Dispatcher {
    void process(User sender, User receipient, String message) throws IOException;
}
