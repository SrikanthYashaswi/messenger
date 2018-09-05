


import com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.org.apache.xerces.internal.parsers.CachingParserPool;

import javax.naming.LimitExceededException;
import java.nio.ByteBuffer;
import java.security.*;

import java.io.*;
import java.net.*;
import java.util.*;


class limitExceed extends Throwable{
    public limitExceed(){
        //System.out.println("no more connections!");
    }
}
enum ConnectedBy{CONSOLE,BROWSER}

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
class WebSocket{
    public static byte[] ParseToWebSocketFrame(byte[] rawData)
    {
        int frameCount=0;
        byte frame[]= new byte[10];
        frame[0] =(byte)129;
        if(rawData.length<=125)
        {
            frame[1] = (byte) rawData.length;
            frameCount=2;
        }
        else if(rawData.length>=126&&rawData.length<=65535)
        {
            frame[1] =(byte)126;
            frame[2] =(byte) ((rawData.length >>8 ) & 255);
            frame[3]= (byte) ((rawData.length ) & 255);
            frameCount=4;
        }
        int bLength = frameCount+rawData.length;
        byte[] reply = new byte[bLength];
        for(int i=0;i<frameCount;i++)
        {
            reply[i]=frame[i];
        }
        for(int i=0;i<rawData.length;i++) {
            reply[i + frameCount] = rawData[i];
        }
        return reply;
    }
    public static byte[] UnMaskFrame(byte[] ch) throws Exception
    {
        byte mask[] = new byte[4];
        int len = ch.length;
        byte msg[] =null;
        if (len != -1) {
            len = (byte) (ch[1] & 127);
            int ind = 2;
            int firstMask=2;
            if (len > 0) {
                if(len==126)
                {
                    len =(ch[3]&255)+(ch[2]&255)*256;
                    firstMask = 4;
                }
                else if(len==127)
                {
                    firstMask = 10;
                }
                for (int i = 0; i <  4; i++)
                {
                    mask[i] = ch[i+firstMask];
                }
                msg = new byte[len];
                for (int i = 0; i < len; i++)
                {
                    msg[i] = (byte) (ch[i+firstMask+4] ^ mask[i % 4]);
                }
                if (msg[0] == 3 && msg[1] == -23)  // socket closed by browser (strange no meaning..!)
                {
                    throw new Exception("Exception at Frame Unmasking");
                }
            }
            return msg;
        }
        throw new Exception("Malfunctioned Frame");
    }
    public static String getWebSocketAccept(String secWebSocketKey)
    {
        final String webSocketMagicNumber= "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        try {
            String webSocketAccept = secWebSocketKey + webSocketMagicNumber;
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            byte digested[] = messageDigest.digest(webSocketAccept.getBytes());
            return Base64.encode(digested);
        }
        catch (Exception c)
        {
            //System.out.println(">>>>>>>>>>>>>>>>>>Exception at getWebSocketAccept");
        }
        return null;
    }
    public static String toString(byte[] inp) {
        char sd[] = new char[inp.length];
        for (int i = 0; i < inp.length; i++) {
            sd[i] = (char) inp[i];
        }
        return String.copyValueOf(sd);
    }
}
class WebSocket_old{
    public static byte[] ParseToWebSocketFrame(byte[] rawData)
    {
        int frameCount;
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
        return reply;
    }
    public static byte[] UnMaskFrame(byte[] ch) throws Exception
    {
        byte mask[] = new byte[4];
        int len = ch.length;
        byte msg[];
        if (len != -1) {
            len = (byte) (ch[1] & 127);
            msg = new byte[len];
            int ind = 2;
            if (len > 0) {
                for (int i = 2; i < 2 + 4; i++) {
                    mask[i - 2] = ch[i];
                }
                for (int i = 6, j = 0; i < 6 + len; i++, j++) {
                    msg[i - 6] = (byte) (ch[i] ^ mask[j % 4]);
                }
                if (msg[0] == 3 && msg[1] == -23)  // socket closed by browser (strange no meaning..!)
                {
                    throw new Exception("Exception at Frame Unmasking");
                }
            }
            return msg;
        }
        throw new Exception("Malfunctioned Frame");
    }
    public static String getWebSocketAccept(String secWebSocketKey)
    {
        final String webSocketMagicNumber= "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        try {
            String webSocketAccept = secWebSocketKey + webSocketMagicNumber;
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            byte digested[] = messageDigest.digest(webSocketAccept.getBytes());
            return Base64.encode(digested);
        }
        catch (Exception c)
        {
            //System.out.println(">>>>>>>>>>>>>>>>>>Exception at getWebSocketAccept");
        }
        return null;
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
            //System.out.println("on connect ");
            //System.out.println(c);
            //System.out.println(d);
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
            //System.out.println("on disconnect ");
            //System.out.println(c);
            //System.out.println(d);
        }
    }
}
class UniversalData{
    static public final int maxUsers=60000;
    static public socketThread connection[] = new socketThread[UniversalData.maxUsers];
    static public _UsersPool UsersPool = new _UsersPool();
    static public boolean DEBUG = false;
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
                //System.out.println( "\n>>ADMIN: " + msg);
            } catch (Exception c) {
                //System.out.println(c.getMessage());
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
    String name;
    boolean nameUpdated = false;
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
                    byte reply[] = WebSocket.ParseToWebSocketFrame(rawData);
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
        for(int i=0;i<inp.length;i++)
        {
            sd[i]=(char)inp[i];
        }
        return String.copyValueOf(sd);
    }
    public void run()
    {
        try
        {
            String token[];
            HashMap<String,String> header= new HashMap<String, String>();
            String input;
            boolean handShake = false;
            int temp = 0;
            //System.out.println("NEW (" + ID +")"+ client.getInetAddress()+":"+client.getPort());
            DataInputStream d = new DataInputStream(client.getInputStream());
            InputStream in = client.getInputStream();
            byte ch[]= new byte[10000000];
            while((input=d.readLine())!=null)
            {
                if(!handShake) {
                    if (input.contains("GET / HTTP/1.1"))
                    {
                        ConnectionType = ConnectedBy.BROWSER;
                        //System.out.println("Browser Connected..");
                        temp++;
                        while((input=d.readLine()).equals("")!=true && temp<14)
                        {
                            token = input.split(": ");
                            header.put(token[0],token[1]);
                            temp++;
                        }
                        send("HTTP/1.1 101");
                        send("Upgrade: websocket");
                        send("Connection: Upgrade");
                        send("Sec-WebSocket-Accept:"+WebSocket.getWebSocketAccept(header.get("Sec-WebSocket-Key"))+"\r\n");
                        handshake = true;
                        //System.out.println("Browser Handshake Complete");
                        handshake=true;
                        break;
                    }
                    else
                    {
                        ConnectionType = ConnectedBy.CONSOLE;
                        updateUserNameAs(input);
                        //System.out.println(name+" connected!");
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
                while(in.read(ch)!=-1) {
                    String ms = toString(WebSocket.UnMaskFrame(ch));
                    if (!nameUpdated && !ms.equals("*>/")) {
                        updateUserNameAs(ms);
                        //System.out.println(name + " connected!");
                        pushToAllUsers(new notification(ms + " is now connected!"));
                        pushToThisUser(new notification("Connected Clients : " + getActiveUsersList()));
                    } else {
                        if(ms.equals("*>/"))
                        {
                            pushToAllUsers(new message("*>/"+name));
                        }
                        else {
                            pushToAllUsers(new message(name+": " + ms));
                        }
                    }
                }
            }
            client.close();
        }
        catch(Exception c){
            //System.out.println("error "+c.getMessage()+" "+c.getCause() +" "+c.getClass());
            c.printStackTrace();
        }
        finally{
            if(nameUpdated) {
                pushToAllUsers(new notification(name + " left"));
            }
            removeUserName();
            UniversalData.UsersPool.DisconnectUser(ID);
            //System.out.println("Connection"+ ID +" Terminated!");
        }
    }
    public void updateUserNameAs(String nm)
    {
        this.name = nm;
        nameUpdated = true;
    }
    public void removeUserName()
    {
        this.name=null;
        nameUpdated = false;
    }
    public String getActiveUsersList()
    {
        String List="";
        int ListSize = UniversalData.UsersPool.ActiveUser.size();
        for(int i=0;i<ListSize;i++) {
            int tm = UniversalData.UsersPool.ActiveUser.get(i);
            if (UniversalData.connection[tm].ID != this.ID && UniversalData.connection[tm].nameUpdated) {
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
            if(UniversalData.connection[j].ID!= ID && UniversalData.connection[j].nameUpdated)
            {
                UniversalData.connection[j].send(data.toString());
            }
        }
    }
}
public class server {
    public static void main(String arg[]) throws IOException
    {
        ServerSocket server = new ServerSocket(5000);
        boolean firstRun = true;
        //System.out.println("Server Started");

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
                //System.out.println(c.toString());
            }
            catch (IOException d)
            {
                //System.out.println(d.toString());
            }
        }
    }
}