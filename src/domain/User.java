package domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import exceptions.MalfunctionedFrame;
import net.http.Request;
import net.http.RequestReader;
import net.ws.WebSocket;

public class User{
	public static final String SYSTEM_ICON = "¯\\_(ツ)_/¯ : ";
	public static int nextId = 0;
	
	public Socket client;
	public String name = null;
	public int uniqueId = -1;
	private ConnectedBy connectionType = null;
	public int groupId;
	
	public boolean handshakeDone = false;
	
	public User(Socket client) throws IOException, InterruptedException{
		this.client = client;
		this.groupId = Shared.harmony;
		this.uniqueId = nextId++;
		determineConnectionType();
		print();
	}
	
	public void sayEvent(String eventName, String message) throws IOException {
		say("@"+eventName+" : "+message);
	}
	
	public void systemSays(String message) throws IOException{
		say(SYSTEM_ICON+message);
	}
	public void say(String message) throws IOException{
		PrintStream o = new PrintStream(client.getOutputStream());
		
		if(connectionType == ConnectedBy.WEBSOCKET){
			byte[] rawData = message.getBytes();
            byte[] reply = WebSocket.toWSFrame(rawData);
            o.write(reply);
		}
		else
		{
			o.println(message);
		}
	}
	
	public String whatSaying() throws IOException, MalfunctionedFrame{
		String message = "";
		InputStream inputStream = client.getInputStream();
		if(connectionType == ConnectedBy.WEBSOCKET){
			byte[] msg = RequestReader.readBytes(inputStream, inputStream.available());
			message = WebSocket.unMaskFrame(msg);
		}
		
		if(connectionType == ConnectedBy.CONSOLE){
			message =  RequestReader.readStream(inputStream, inputStream.available());
		}
		return message;
	}
	
	private void welcomeMessage() throws IOException{
		String welcome = "Welcome "+name+"\n";
		welcome +=       "To join a particular group type /join <groupname>";
		systemSays(welcome);
	}

	public void joinGroup(String groupName) {
		if(!Shared.groups.containsKey(groupName)){
			Shared.addNewGroup(groupName);
		}
		this.groupId = Shared.groups.get(groupName);
	}
	
	public ConnectedBy getConnectionType(){
		return this.connectionType;
	}
	/**
	 * The thread sleep is required because it takes a fraction of second to get GET/POST kind of requests
	 * which will help determine the request type
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void determineConnectionType() throws IOException, InterruptedException{
		InputStream inputStream = client.getInputStream();
		Thread.sleep(500);
		if(inputStream.available() > 0){
			this.connectionType = ConnectedBy.BROWSER;
		}
		else{
			this.connectionType = ConnectedBy.CONSOLE;
			systemSays("who are you?");
			doHandshake();
		}
	}

	public void doHandshake() {
		this.handshakeDone = true;
	}
	
	public void doWebsocketHandshake(Request request) throws IOException, NoSuchAlgorithmException{
		say("HTTP/1.1 101");
		say("Upgrade: websocket");
		say("Connection: Upgrade");
		say("Sec-WebSocket-Accept: "+WebSocket.getWebSocketAccept(request.getHeaders().get("Sec-WebSocket-Key"))+"\r\n");
		setConnectionType(ConnectedBy.WEBSOCKET);
		doHandshake();
	}
	
	public void sendOk() throws IOException
	{
		say("HTTP/1.1 200");
		say("Connection: close");
	}
	
	public void setConnectionType(ConnectedBy conn) throws IOException{
		this.connectionType = conn;
		systemSays("Who are you");
	}
	
	public void setUsername(String name) throws IOException{
		this.name = name;
		welcomeMessage();
	}
	
	
	public void print(){
		System.out.println("ip:"+client.getInetAddress()+"connection type:"+connectionType+";handshake: "+handshakeDone+"; name:"+this.name);
	}
}
