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

public class User {
	public static final String SYSTEM_ICON = "admin : ";
	public static int nextId = 0;

	public Socket client;
	public String name = null;
	public int uniqueId = -1;
	private ConnectedBy connectionType = null;
	public int groupId;
	private int idleTime = 0;

	private boolean usernameAssignmentByUrlQuery = false;
	private boolean groupAssignmentByUrlQuery = false;

	public boolean handshakeDone = false;

	public User(Socket client) throws IOException, InterruptedException {
		this.client = client;
		this.uniqueId = nextId++;
		this.groupId = 0;
		determineConnectionType();
	}

	public void sayEvent(String eventName, String message) throws IOException {
		say("@" + eventName + " : " + message);
	}

	public void systemSays(String message) throws IOException {
		say(SYSTEM_ICON + message);
	}

	public void say(String message) throws IOException {
		PrintStream o = new PrintStream(client.getOutputStream());

		if (connectionType == ConnectedBy.WEBSOCKET) {
			byte[] rawData = message.getBytes();
			byte[] reply = WebSocket.toWSFrame(rawData);
			o.write(reply);
		} else {
			o.println(message);
		}
	}

	public String whatSaying() throws IOException, MalfunctionedFrame {
		String message = "";
		InputStream inputStream = client.getInputStream();
		if (connectionType == ConnectedBy.WEBSOCKET) {
			byte[] msg = RequestReader.readBytes(inputStream, inputStream.available());
			message = WebSocket.unMaskFrame(msg);
		}

		if (connectionType == ConnectedBy.CONSOLE) {
			message = RequestReader.readStream(inputStream, inputStream.available());
		}
		return message;
	}

	private void welcomeMessage() throws IOException {
		String welcome = "Welcome " + name + "\n";
		if (!groupAssignmentByUrlQuery) {
			welcome += "To join a particular group type /join <groupname>";
		}
		systemSays(welcome);
	}

	public void joinGroup(String groupName) {
		if (!Shared.groups.containsKey(groupName)) {
			Shared.addNewGroup(groupName);
		}
		this.groupId = Shared.groups.get(groupName);
	}

	public ConnectedBy getConnectionType() {
		return this.connectionType;
	}

	/**
	 * The thread sleep is required because it takes a fraction of second to get GET/POST kind of requests
	 * which will help determine the request type
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void determineConnectionType() throws IOException, InterruptedException {
		Thread.sleep(500);
		InputStream inputStream = client.getInputStream();
		if (inputStream.available() > 0) {
			this.connectionType = ConnectedBy.BROWSER;
		} else {
			this.connectionType = ConnectedBy.CONSOLE;
			systemSays("who are you?");
			doHandshake();
		}
	}

	public void doHandshake() {
		this.handshakeDone = true;
	}

	public void doWebsocketHandshake(Request request) throws IOException, NoSuchAlgorithmException {
		say("HTTP/1.1 101 Switching Protocols\r");
		say("Upgrade: websocket\r");
		say("Connection: Upgrade\r");
		say("Sec-WebSocket-Accept: " + WebSocket.getWebSocketAccept(request.getHeaders().get("Sec-WebSocket-Key")) + "\r");
		say("Content-Encoding: identity\r");
		say("\r");
		setConnectionType(ConnectedBy.WEBSOCKET);
		attemptGroupAssignment(request);
		attemptUsernameAssignment(request);
		doHandshake();
	}

	private void attemptUsernameAssignment(Request request) throws IOException {
		String username = request.getQueryValue("n");

		if(username == null || username.isEmpty())
		{
			return;
		}
		this.usernameAssignmentByUrlQuery = true;
		this.setUsername(username);
	}

	private void attemptGroupAssignment(Request request)
	{
		String groupName = request.getQueryValue("g");

		if(groupName!= null)
		{
			this.groupAssignmentByUrlQuery = true;
			joinGroup(groupName);
		}
		else
		{
			this.groupId = 0;
		}
	}
	
	public void sendOk() throws IOException
	{
		say("HTTP/1.1 200");
		say("Connection: close\r\n");
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
		System.out.println(client.getInetAddress()+"\t"+connectionType+"\t "+handshakeDone+"\t"+this.name+"\t"+this.idleTime);
	}
	
	public void increaseIdleTime(){
		idleTime++;
	}
	
	public int getIdleTime(){
		return idleTime;
	}
	
	public void resetIdleTime(){
		idleTime = 0;
	}
	
	public void bye(){
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
