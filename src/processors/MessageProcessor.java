package processors;

import java.io.IOException;

import domain.Shared;
import domain.User;
import exceptions.MalfunctionedFrame;

public class MessageProcessor {
	
	private MessageProcessor(){
		
	}
	
	public static void processMessage(User user, String message) throws IOException, MalfunctionedFrame{
		message = message.trim();
		if(message.equals("")){
			return;
		}
		if(user.name == null){
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
		else
		{
			for(User u: Shared.clients){
				if(u.uniqueId == user.uniqueId)
					continue;
				if(user.groupId == u.groupId){
					u.say(user.name+" : "+message);
				}
			}
		}
	}
}
