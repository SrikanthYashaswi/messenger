package processors;

import domain.User;

import java.io.IOException;

class CommandResolver {

    public static boolean resolve(String message, User user) throws IOException
    {
        if(message.startsWith("/set"))
        {
            String blocks[] = message.split(" ");

            if(blocks.length == 3)
            {
                update(blocks[1], blocks[2],  user);
            }
        }
        return message.startsWith("/set");
    }

    private static void update(String key, String value, User user) throws IOException
    {
        switch(key)
        {
            case "name": {
                user.setUsername(value.trim());
                break;
            }

            case "group":{
                user.setGroupName(value.trim());
                break;
            }
            default:{

            }
        }
    }
}
