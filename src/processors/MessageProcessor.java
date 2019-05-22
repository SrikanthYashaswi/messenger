package processors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import domain.ConnectedBy;
import domain.Shared;
import domain.User;
import exceptions.MalfunctionedFrame;

public class MessageProcessor {

	public static final List<String> reservedWords = Arrays.asList(new String[]{User.SYSTEM_ICON, "@typing", "@pong"});
	
	private MessageProcessor(){
		
	}
	
	public static void processMessage(User user, String message) throws IOException, MalfunctionedFrame{
		message = message.trim();

		if(message.equals("")){
			return;
		}

		if(message.startsWith("@pong")){
			/**
			 * Pong message is sinked here.
			 */
			return;
		}
		
		if(user.name == null)
		{
			user.setUsername(message);
			return;
		}
		
		if(message.startsWith("/join")){
			String[] blocks = message.split(" ");
			if(blocks.length  == 2){
				user.joinGroup(blocks[1]);
				user.systemSays("Joined "+blocks[1]);
			}
			else
			{
				user.systemSays("Hey Z0^^B!#.Man should nt give blank group names");
			}
		}
		else if(message.startsWith("@typing")){
			for(User u: Shared.clients){
				if(u.uniqueId == user.uniqueId || u.getConnectionType().equals(ConnectedBy.CONSOLE) || u.name == null)
					continue;
				if(user.groupId == u.groupId){
					u.say(message);
				}
			}
		}
		else
		{
			for(User u: Shared.clients){
				if(u.uniqueId == user.uniqueId || u.name == null)
					continue;
				if(user.groupId == u.groupId){
					u.say(user.name+" : "+message);
				}
			}
		}
	}
	
	public static void updateOnlineUsers() throws IOException{
		for(User u : Shared.clients){
			if(!u.getConnectionType().equals(ConnectedBy.CONSOLE))
				u.sayEvent("online", Shared.userListCsv);
		}	
	}
}
