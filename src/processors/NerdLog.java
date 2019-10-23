package processors;

import domain.Index;
import domain.Shared;

public class NerdLog {

    public static String[] accumulate(){
        String feed[] = new String[Index.SIZE];
        feed[Index.ONLINE] = String.valueOf(Shared.clients.size()) ;
        feed[Index.LOOP_TIME] = String.valueOf(Shared.LOOP_TIME);
        feed[Index.USED_MEM] = String.valueOf(Shared.sysinfo.usedMemInKB());
        return feed;
    }
}
