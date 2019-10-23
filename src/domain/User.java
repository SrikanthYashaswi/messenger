package domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import exceptions.MalfunctionedFrame;
import net.http.Request;
import net.http.RequestReader;
import net.ws.WebSocket;

public class User extends ClientSocket
{
	private String name = null;
	private int uniqueId = -1;

	private String groupName = Constants.DEFAULT_GROUP;

    private boolean groupAssignmentByUrlQuery = false;

    public User()
    {
        super();
    }

	public User(Socket client) throws IOException, InterruptedException {
		super(client);
	}

	public String getGroupName(){
		return groupName;
	}

	public void setGroupName(String groupName){
		this.groupName = groupName;
	}
	public Integer getId()
    {
        return uniqueId;
    }

	public String getName()
	{
		return name;
	}

	public void sayEvent(String eventName, String message) throws IOException
	{
		say("@" + eventName + " : " + message);
	}

	public void systemSays(String message) throws IOException
    {
		say(Constants.SYSTEM_ICON + message);
	}

	public void say(String message) throws IOException
    {
		publishOut(message);
	}

	public String whatSaying() throws IOException, MalfunctionedFrame
	{
		String message = "";
		InputStream inputStream = client.getInputStream();

		if (connectionType == ConnectedBy.WEBSOCKET)
		{
			byte[] msg = RequestReader.readBytes(inputStream, inputStream.available());
			message = WebSocket.unMaskFrame(msg);
		}

		if (connectionType == ConnectedBy.CONSOLE)
		{
			message = RequestReader.readStream(inputStream, inputStream.available());
		}
		return message;
	}

	private void welcomeMessage() throws IOException
    {
		systemSays("Welcome your id: " + name + "\n");
		systemSays("Hi, To set name use eg. /set name yourname");
		systemSays("To set group use eg. /set group groupname");
	}

	public void doWebsocketHandshake(Request request) throws IOException, NoSuchAlgorithmException
    {
		beginWebsocketHandshake(request);
		assignDefaultUserId();
		attemptGroupAssignment(request);
		attemptUsernameAssignment(request);
		welcomeMessage();
	}

	private void attemptUsernameAssignment(Request request) throws IOException {
		String username = request.getQueryValue("n");

		if(username != null && !username.isEmpty())
		{
			this.setUsername(username);
		}
	}

	private void attemptGroupAssignment(Request request)
	{
		String groupName = request.getQueryValue("g");

		if(groupName == null)
		{
			return;
		}

		this.groupAssignmentByUrlQuery = true;
		setGroupName(groupName);
	}

	public void setUsername(String name) throws IOException{
		this.name = name;
	}

	public void print(){
		System.out.println(client.getInetAddress()+"\t"+connectionType+"\t "+handshakeDone+"\t"+this.name+"\t"+idleTime);
	}

	private void assignDefaultUserId()
	{
		this.uniqueId = nextId++;
		this.name = "user-"+this.uniqueId;
	}
}
