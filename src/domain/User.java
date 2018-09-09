package domain;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class User{
	public static final String SYSTEM_ICON = "¯\\_(ツ)_/¯ : ";
	public static int nextId = 0;
	
	public Socket client;
	public String name = null;
	public int uniqueId = -1;
	ConnectedBy connectionType;
	public int groupId;
	
	public User(Socket client) throws IOException{
		this.client = client;
		this.groupId = Shared.harmony;
		this.uniqueId = nextId++;
		serverSay("Who are you?");
	}
	
	public void serverSay(String message) throws IOException{
		say(SYSTEM_ICON+message);
	}
	public void say(String message) throws IOException{
		PrintStream o = new PrintStream(client.getOutputStream());
		o.println(message);
	}
	
	public void setUsername(String name) throws IOException{
		this.name = name;
		welcomeMessage();
	}
	
	private void welcomeMessage() throws IOException{
		String x = "Welcome "+name+"\n";
		x +=       "To join a particular group type /join <groupname>";
		serverSay(x);
	}

	public void joinGroup(String groupName) {
		if(!Shared.groups.containsKey(groupName)){
			Shared.addNewGroup(groupName);
		}
		this.groupId = Shared.groups.get(groupName);
	}
}
