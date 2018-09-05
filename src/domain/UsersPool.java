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
        if(UniversalData.DEBUG)
        {
            String c = "used : ";
            String d = "unused: ";
            for (Integer aActiveUser : ActiveUser) {
                c = c + aActiveUser + " , ";
            }
            for (Integer UnusedSlot : UnusedSlots) {
                d = d + UnusedSlot + " , ";
            }
            //System.out.println("on connect ");
            //System.out.println(c);
            //System.out.println(d);
        }
        return slot;
    }
    public void DisconnectUser(int slot)
    {
        Object s= slot;
        ActiveUser.remove(s);   //directly passing slot is assumed as index and removes value at that index
        UnusedSlots.add(slot);
        if(UniversalData.DEBUG)
        {
            String c = "used : ";
            String d = "unused: ";
            for (int i = 0; i < ActiveUser.size(); i++) {
                c = c + ActiveUser.get(i) + " , ";
            }
            for (int j = 0; j < UnusedSlots.size(); j++) {
                d = d + UnusedSlots.get(j) + " , ";
            }
            //System.out.println("on disconnect ");
            //System.out.println(c);
            //System.out.println(d);
        }
    }
}