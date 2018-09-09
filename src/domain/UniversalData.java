package domain;

import java.util.LinkedList;
import java.util.List;

import app.SocketThread;

public class UniversalData{
    static public final int maxUsers=1;
    static public SocketThread connection[] = new SocketThread[UniversalData.maxUsers];
    static public UsersPool usersPool = new UsersPool();
    public static LinkedList<User> clients = new LinkedList<User>();
    
    public static List<User> getClient(){
    	return clients;
    }
}
