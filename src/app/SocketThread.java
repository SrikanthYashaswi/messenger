package app;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;

import domain.ConnectedBy;
import domain.Message;
import domain.Notification;
import domain.UniversalData;
import exceptions.ConnectionTerminated;
import net.http.Method;
import net.http.Request;
import net.http.RequestReader;
import net.ws.WebSocket;

public class SocketThread implements Runnable
{
    private Socket client;
    private Thread t;
    private int ID;
    private PrintStream o;
    private String name;
    private boolean nameUpdated = false;
    private ConnectedBy ConnectionType;
    private boolean handshake = false;
    SocketThread(){

    }
    public SocketThread(Socket inpSk, String name, int d)
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


    void exec() throws IOException,ConnectionTerminated{
		InputStream reqStream;
		reqStream = client.getInputStream();
		byte ch[]= new byte[10000000];
		
		if(!handshake){
			int length = reqStream.available();	
			String body = RequestReader.readStream(reqStream, length);
			Request request = Request.parse(body);
        	doHandshake(request);
        }
		
		
		if(handshake && ConnectionType == ConnectedBy.BROWSER){
			
		}
		
		if(handshake && ConnectionType ==ConnectedBy.CONSOLE){
			int first ;
			while(true){
				
				first = reqStream.read();
				if(first == -1){
					throw new ConnectionTerminated();
				}
				
				String message = ((char)first + RequestReader.readStream(reqStream, reqStream.available())).replace("\r\n", "");
				
				if(nameUpdated){
					pushToAllUsers(name+" : "+message);
				}
				else{
					if(message.matches("")){
						pushToThisUser(new Notification("Blank names are not accepted, Pls enter name properly:"));
					}
					else{
						updateUserNameAs(message);
						pushToThisUser(new Notification("Connected Clients : " + getActiveUsersList()));	
					}
				}
			}
		}
	}    
    
    void doHandshake(Request request){
    	if(request.getMethod() == Method.GET){
            ConnectionType = ConnectedBy.BROWSER;
            send("HTTP/1.1 101");
            send("Upgrade: websocket");
            send("Connection: Upgrade");
            send("Sec-WebSocket-Accept:"+WebSocket.getWebSocketAccept(request.getHeaders().get("Sec-WebSocket-Key"))+"\r\n");
            handshake = true;
        }
    	if(request.getMethod() == Method.STREAM){
    		ConnectionType = ConnectedBy.CONSOLE;
    		pushToThisUser(new Notification("Who are you?"));
    		handshake = true;
    	}
    }

    public void run(){
    	try{
    		exec();
    	}
		catch(IOException | ConnectionTerminated c){
			c.printStackTrace();
		}
		finally{
			close();
			if(nameUpdated) {
				pushToAllUsers(new Notification(name + " left"));
			}
			removeUserName();
			UniversalData.usersPool.DisconnectUser(ID);
	     }
    }
    
    private void close(){
    	try{
    		client.close();
    	}
    	catch(IOException c){
    		
    	}
    }

    public void runs()
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
                        pushToAllUsers(new Notification(this.name + " is now connected!"));
                        pushToThisUser(new Notification("Connected Clients :"+ getActiveUsersList()));
                    }
                }
                else
                {
                    if(ConnectionType==ConnectedBy.CONSOLE) {
                        pushToAllUsers(new Message(name + " : " + input));
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
                        pushToAllUsers(new Notification(ms + " is now connected!"));
                        pushToThisUser(new Notification("Connected Clients : " + getActiveUsersList()));
                    } else {
                        if(ms.equals("*>/"))
                        {
                            pushToAllUsers(new Message("*>/"+name));
                        }
                        else {
                            pushToAllUsers(new Message(name+": " + ms));
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
                pushToAllUsers(new Notification(name + " left"));
            }
            removeUserName();
            UniversalData.usersPool.DisconnectUser(ID);
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
        int ListSize = UniversalData.usersPool.ActiveUser.size();
        for(int i=0;i<ListSize;i++) {
            int tm = UniversalData.usersPool.ActiveUser.get(i);
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
            for(int i = 0; i<UniversalData.usersPool.ActiveUser.size(); i++)
            {
                int j = UniversalData.usersPool.ActiveUser.get(i);
                if(UniversalData.connection[j].ID!= ID && UniversalData.connection[j].nameUpdated)
                {
                    UniversalData.connection[j].send(data.toString());
                }
            }
    }
}
