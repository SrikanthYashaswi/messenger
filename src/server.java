


import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.org.apache.xerces.internal.parsers.CachingParserPool;

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

class notification{
    public String text;
    public notification(String notifStr)
    {
        text = ">> "+ notifStr;
    }
    public String toString()
    {
        return text;
    }
}
class message{
    public String text;
    public message(String msgStr)
    {
        text = msgStr;
    }
    public String toString()
    {
        return text;
    }
}
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
    static public final int maxUsers=70000;
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
    ConnectedBy ConnectionType;
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
            if(!handshake)
            {
                o.println(message);
            }
            else {
                if (ConnectionType == ConnectedBy.CONSOLE) {
                    o.println(message);
                }
                if (ConnectionType == ConnectedBy.BROWSER) {
                    byte rawData[] = message.getBytes();
                    int frameCount =0;
                    byte frame[]= new byte[10];
                    frame[0] =(byte)129;
                    if(rawData.length<=125)
                    {
                        frame[1] = (byte) rawData.length;
                    }
                    frameCount=2;
                    int bLength = frameCount+rawData.length;
                    byte[] reply = new byte[bLength];
                    for(int i=0;i<frameCount;i++)
                    {
                        reply[i]=frame[i];
                    }
                    for(int i=0;i<rawData.length;i++)
                    {
                        reply[i+frameCount]=rawData[i];
                    }
                    o.write(reply);
                }
            }
        }
        catch(Exception c){

        }
    }
    public String toString(byte[] inp)
    {
        char sd[]= new char[inp.length];
        int i=0;
        for(i=0;i<inp.length;i++)
        {
            sd[i]=(char)inp[i];
        }
        String ret = String.copyValueOf(sd);
        return ret;
    }
    public void run()
    {
        try
        {
            String[][] hshkMeta = new String[30][2];
            String hmeta[] = new String [30];
            String input;
            boolean handShake = false;
            int temp = 0;
            System.out.println("NEW (" + ID +")"+ client.getInetAddress()+":"+client.getPort());
            DataInputStream d = new DataInputStream(client.getInputStream());
            InputStream in = client.getInputStream();
            byte ch[]= new byte[1000];
            while((input=d.readLine())!=null)
            {
                if(!handShake) {
                    if (input.contains("GET / HTTP/1.1"))
                    {
                        ConnectionType = ConnectedBy.BROWSER;
                        //System.out.println("Browser Connected..");
                        hshkMeta[0][0] = input;
                        temp++;
                        while((input=d.readLine()).equals("")!=true && temp<14)
                        {
                            hshkMeta[temp][0] = input;
                            temp++;
                        }
                        for(int i=1;i<temp;i++)
                        {
                            hshkMeta[i] = hshkMeta[i][0].split(": ");
                        }
                        //System.out.println("-----------------------");
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
                                    send("HTTP/1.1 101");
                                    send("Upgrade: websocket");
                                    send("Connection: Upgrade");
                                    send("Sec-WebSocket-Accept:"+encoded+"\r\n");
                                    handshake = true;
                                    //System.out.println("Browser Handshake Complete");
                                }
                                catch(NoSuchAlgorithmException c){
                                }
                            }
                        }
                        handshake=true;
                        break;
                    }
                    else
                    {
                        ConnectionType = ConnectedBy.CONSOLE;
                        name = input;
                        System.out.println(name+" connected!");
                        handShake = true;
                        pushToAllUsers(new notification(this.name + " is now connected!"));
                        pushToThisUser(new notification("Connected Clients :"+ getActiveUsersList()));
                    }
                }
                else
                {
                    if(ConnectionType==ConnectedBy.CONSOLE) {
                        pushToAllUsers(new message(name + " : " + input));
                    }
                }
            }
            if(ConnectionType==ConnectedBy.BROWSER)
            {
                byte mask[] = new byte[4];
                byte msg[];
                while(in.read(ch)!=-1)
                {
                    int len = ch.length;
                    if(len!=-1)
                    {
                        len = (byte) (ch[1] & 127);
                        msg = new byte[len];
                        int ind = 2;
                        for(int i=2;i<2+4;i++)
                        {
                            mask[i-2] = ch[i];
                        }
                        for(int i=6,j=0;i<6+len;i++,j++)
                        {
                            msg[i-6]= (byte) (ch[i] ^ mask[j%4]);
                        }
                        if(msg[0]==3&&msg[1]==-23)  // socket closed by browser (strange no meaning..!)
                        {
                            break;
                        }
                        String ms = toString(msg);
                        if(name.equals(""))
                        {
                            name = ms;
                            System.out.println(name+" connected!");
                            pushToAllUsers(new notification(ms + " is now connected!"));
                            pushToThisUser(new notification("Connected Clients : "+getActiveUsersList()));
                        }
                        else
                        {
                            pushToAllUsers(new message(name+" : "+ms));
                        }
                    }
                }
            }
            client.close();
        }
        catch(Exception c){
            System.out.println("error "+c.getMessage());
        }
        finally{
            if(!name.equals("")) {
                pushToAllUsers(new notification(name + " left"));
            }
            name="";
            UniversalData.UsersPool.DisconnectUser(ID);
            System.out.println("Connection"+ ID +" Terminated!");
        }
    }
    public String getActiveUsersList()
    {
        String List="";
        int ListSize = UniversalData.UsersPool.ActiveUser.size();
        for(int i=0;i<ListSize;i++) {
            int tm = UniversalData.UsersPool.ActiveUser.get(i);
            if (UniversalData.connection[tm].ID != this.ID) {
                List = List + UniversalData.connection[tm].name;
                List = List + ",";
            }
        }
        List = List + ".";
        return List;
    }
    public void pushToThisUser(Object data)
    {
        this.send(data.toString());
    }
    public void pushToAllUsers(Object data)
    {
        if(!data.toString().equals(""))
        for(int i=0;i<UniversalData.UsersPool.ActiveUser.size();i++)
        {
            int j = UniversalData.UsersPool.ActiveUser.get(i);
            if(UniversalData.connection[j].ID!= ID)
            {
                UniversalData.connection[j].send(data.toString());
            }
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
                Socket client = server.accept();
                int FreeSlot = UniversalData.UsersPool.NextFreeSlot();
                UniversalData.connection[FreeSlot] = new socketThread(client, "ConnectionThread "+FreeSlot, FreeSlot);
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