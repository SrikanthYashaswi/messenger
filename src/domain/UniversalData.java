package domain;

import app.SocketThread;

public class UniversalData{
    static public final int maxUsers=60000;
    static public SocketThread connection[] = new SocketThread[UniversalData.maxUsers];
    static public UsersPool usersPool = new UsersPool();
}
