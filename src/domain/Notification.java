package domain;


public class Notification {
    public String text;
    public Notification(String notifStr)
    {
        text = ">> "+ notifStr;
    }
    public String toString()
    {
        return text;
    }
}
