package domain;

public class Message {
    public String text;
    public Message(String msgStr)
    {
        text = msgStr;
    }
    public String toString()
    {
        return text;
    }
}