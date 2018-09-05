package app;


import domain.UniversalData;
import exceptions.LimitExceed;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class server {
    public static void main(String arg[]) throws IOException
    {
        ServerSocket server = new ServerSocket(80);
        boolean firstRun = true;

        for(int i = 0; i< UniversalData.maxUsers; i++){
            UniversalData.connection[i] = new SocketThread();
        }
        while(true)
        {
            try {
                Socket client = server.accept();
                int FreeSlot = UniversalData.usersPool.NextFreeSlot();
                UniversalData.connection[FreeSlot] = new SocketThread(client, "ConnectionThread "+FreeSlot, FreeSlot);
            }
            catch (LimitExceed c)
            {
                //System.out.println(c.toString());
            }
            catch (IOException d)
            {
                //System.out.println(d.toString());
            }
        }
    }
}