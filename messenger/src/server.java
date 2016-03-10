


import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.naming.LimitExceededException;
import java.security.*;

import java.io.*;
import java.net.*;
import java.util.*;

class limitExceed extends Throwable{
    public limitExceed(){
        System.out.println("no more connections!");
    }
}
enum ConnectedBy{CONSOLE,BROWSER};

class _UsersPool{
    public LinkedList<Integer> ActiveUser = new LinkedList<Integer> ();
    LinkedList<Integer> UnusedSlots = new LinkedList<Integer> ();
    public _UsersPool()
    {
        for(int i=0;i<UniversalData.maxUsers;i++)
        {
            UnusedSlots.add(i);
        }
    }
    public int NextFreeSlot() throws limitExceed
    {
        if(ActiveUser.size()==UniversalData.maxUsers)
        {
            throw new limitExceed();
        }
        int  slot= UnusedSlots.getFirst();
        ActiveUser.add(slot);
        UnusedSlots.removeFirst();
        if(UniversalData.DEBUG)
        {
            String c = "used : ";
            String d = "unused: ";
            for (Integer aActiveUser : ActiveUser) {
                c = c + aActiveUser + " , ";
            }
            for (Integer UnusedSlot : UnusedSlots) {
                d = d + UnusedSlot + " , ";
            }
            System.out.println("on connect ");
            System.out.println(c);
            System.out.println(d);
        }
        return slot;
    }
    public void DisconnectUser(int slot)
    {
        Object s= slot;
        ActiveUser.remove(s);   //directly passing slot is assumed as index and removes value at that index
        UnusedSlots.add(slot);
        if(UniversalData.DEBUG)
        {
            String c = "used : ";
            String d = "unused: ";
            for (int i = 0; i < ActiveUser.size(); i++) {
                c = c + ActiveUser.get(i) + " , ";
            }
            for (int j = 0; j < UnusedSlots.size(); j++) {
                d = d + UnusedSlots.get(j) + " , ";
            }
            System.out.println("on disconnect ");
            System.out.println(c);
            System.out.println(d);
        }
    }
}
class UniversalData{
    static public final int maxUsers=3;
    static public socketThread connection[] = new socketThread[UniversalData.maxUsers];
    static public _UsersPool UsersPool = new _UsersPool();
    static public boolean DEBUG = false;
}
class inputThread implements Runnable{
    public Thread ioT;
    DataInputStream ascnInp;
    Socket Socketdetail;
    //String
    //boolean handshake = false;
    public inputThread(DataInputStream inp,Socket det){
        ascnInp = inp;
        Socketdetail= det;
        ioT = new Thread(this,"inputThread");
        ioT.start();
    }
    public void run(){
        try {
         /*   if(handshake == false)      // do handshake first
            {

            }
            else {*/
            System.out.println(Socketdetail.getInetAddress() + ":" + Socketdetail.getPort() + " > " + ascnInp.readLine());
            //}
        }
        catch(Exception d){
            System.out.println(d.getMessage());
        }
    }
}
class outputThread implements Runnable{

    private BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
    public Thread ioT;

    public outputThread(){
        ioT = new Thread(this);
        ioT.start();
    }
    public void run(){
        while(true) {
            try {
                String msg = inp.readLine();
                for(int i=0;i<UniversalData.UsersPool.ActiveUser.size();i++)
                {
                    UniversalData.connection[UniversalData.UsersPool.ActiveUser.get(i)].send(msg);
                }
                System.out.println( "\n>>ADMIN: " + msg);
            } catch (Exception c) {
                System.out.println(c.getMessage());
            }
        }
    }
}
class socketThread implements Runnable
{
    Socket client;
    public Thread t;
    public int ID;
    PrintStream o;
    String name="";
    boolean handshake = false;
    public socketThread(){

    }
    public socketThread(Socket inpSk,String name,int d)
    {
        client = inpSk;
        ID = d;
        t= new Thread(this,name);
        t.start();
    }
    public void send(String message)
    {
        try {
            o = new PrintStream(client.getOutputStream());
            if(message.length()>5) {
                o.println("\n\r" + message);
            }
        }
        catch(Exception c){

        }
    }
    public void run()
    {
        try
        {
            boolean emt =true;
            String hshkMeta[][] = new String[30][2];
            String tr;
            String input;
            boolean handShake = false;
            int temp = 0;
            System.out.println("new Connection " + client.getInetAddress()+":"+client.getPort());
            DataInputStream d = new DataInputStream(client.getInputStream());
            //  PrintStream o = new PrintStream(client.getOutputStream());
            if(!handShake){
                send("Enter Your Name :");
            }
            while((input=d.readLine())!=null)
            {
                if(!handShake) {
                    name=input;
                    handShake=true;
                    String list="";
                    for(int i=0;i<UniversalData.UsersPool.ActiveUser.size();i++)
                    {
                        int j = UniversalData.UsersPool.ActiveUser.get(i);
                        if(UniversalData.connection[j].ID!=this.ID) {
                            UniversalData.connection[j].send(">>new user connected " + " : " + input);
                            list = list + UniversalData.connection[j].name + " , ";
                        }
                    }
                    this.send("Connected Clients :" + list);
                }
                else {
                    System.out.println(name +" : "+input);
                    for(int i=0;i<UniversalData.UsersPool.ActiveUser.size();i++)
                    {
                        int j = UniversalData.UsersPool.ActiveUser.get(i);
                        if(UniversalData.connection[j].ID!= this.ID)
                            UniversalData.connection[j].send(name+" : "+input);
                    }
                }
              /*  new inputThread(d,client);
                new outputThread(o);
                if(handshake == false ) // do handshake
                {
                    if((tr = d.readLine()).equals("")!=true && temp<14) {
                        hshkMeta[temp][0] = tr;
                        temp++;
                    }
                    else{
                        for(int i=1;i<temp;i++)
                        {
                                hshkMeta[i] = hshkMeta[i][0].split(": ");
                        }
                        for(int i=1;i<temp;i++)
                        {
                            if(hshkMeta[i][0].equals("Sec-WebSocket-Key"))
                            {
                                try {
                                    String rr = hshkMeta[i][1];
                                    rr = rr + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
                                    MessageDigest md = MessageDigest.getInstance("SHA1");
                                    byte x[] = rr.getBytes();
                                    byte op[] = md.digest(x);
                                    String encoded = Base64.encode(op);
                                    send("HTTP/1.1 101 Switching Protocols");
                                    send("Upgrade: websocket");
                                    send("Connection: Upgrade");
                                    send("Sec-WebSocket-Accept: "+encoded+"\r\n");
                                    handshake = true;
                                }
                                catch(NoSuchAlgorithmException c){
                                }
                            }
                        }
                        handshake=true;
                    }
                }
                else {}*/
            }
            client.close();
        }
        catch(Exception c){
            System.out.println(c.getMessage());
        }
        finally{
            for(int i=0;i<UniversalData.UsersPool.ActiveUser.size();i++)
            {
                int j = UniversalData.UsersPool.ActiveUser.get(i);
                if(UniversalData.connection[j].ID!= ID)
                {
                    UniversalData.connection[j].send(">> "+name+" left .");
                }
            }
            name="";
            UniversalData.UsersPool.DisconnectUser(ID);
            System.out.println("Connection"+ ID +" Terminated!");
        }
    }
}
public class server {
    public static void main(String arg[]) throws IOException
    {
        ServerSocket server = new ServerSocket(4444);
        boolean firstRun = true;
        System.out.println("Server Started");

        for(int i=0;i<UniversalData.maxUsers;i++){
            UniversalData.connection[i] = new socketThread();
        }
        while(true)
        {
            try {
                System.out.println("Listening for new Connection...");
                Socket client = server.accept();
                int FreeSlot = UniversalData.UsersPool.NextFreeSlot();
                System.out.println("Available at " + FreeSlot);
                UniversalData.connection[FreeSlot] = new socketThread(client, "ConnectionThread", FreeSlot);
                if (firstRun) {
                    new outputThread();
                    firstRun = false;
                }
            }
            catch (limitExceed c)
            {
                System.out.println(c.toString());
            }
            catch (IOException d)
            {
                System.out.println(d.toString());
            }

        }
    }
}