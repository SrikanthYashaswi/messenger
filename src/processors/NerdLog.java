package processors;

import domain.Index;
import domain.Shared;

public class NerdLog {

    public static String[] accumulate(){
        String feed[] = new String[Index.SIZE];
        feed[Index.ONLINE] = String.valueOf(Shared.clients.size()) ;
        return feed;
    }
}
