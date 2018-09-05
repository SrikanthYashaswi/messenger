package domain;

import exceptions.LimitExceed;

import java.util.LinkedList;

public class UsersPool {

    public LinkedList<Integer> ActiveUser = new LinkedList<Integer> ();
    LinkedList<Integer> UnusedSlots = new LinkedList<Integer> ();

    public UsersPool()
    {
        for(int i = 0; i< UniversalData.maxUsers; i++)
        {
            UnusedSlots.add(i);
        }
    }
    public int NextFreeSlot() throws LimitExceed
    {
        if(ActiveUser.size()== UniversalData.maxUsers)
        {
            throw new LimitExceed();
        }

        int  slot= UnusedSlots.getFirst();
        ActiveUser.add(slot);
        UnusedSlots.removeFirst();

        return slot;
    }
    public void DisconnectUser(int slot)
    {
        Object s= slot;
        ActiveUser.remove(s);   //directly passing slot is assumed as index and removes value at that index
        UnusedSlots.add(slot);
    }
}