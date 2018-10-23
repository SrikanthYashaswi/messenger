package net.ws;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import exceptions.MalfunctionedFrame;

public class WebSocket{
	
	private WebSocket(){
		
	}
    public static byte[] toWSFrame(byte[] rawData)
    {
        int frameCount=0;
        byte[] frame= new byte[10];
        frame[0] =(byte)129;
        if(rawData.length<=125)
        {
            frame[1] = (byte) rawData.length;
            frameCount=2;
        }
        else if(rawData.length>=126&&rawData.length<=65535)
        {
            frame[1] =(byte)126;
            frame[2] =(byte) ((rawData.length >>8 ) & 255);
            frame[3]= (byte) ((rawData.length ) & 255);
            frameCount=4;
        }
        int bLength = frameCount+rawData.length;
        byte[] reply = new byte[bLength];
        for(int i=0;i<frameCount;i++)
        {
            reply[i]=frame[i];
        }
        for(int i=0;i<rawData.length;i++) {
            reply[i + frameCount] = rawData[i];
        }
        return reply;
    }
    
    /**
     * Algorithm to unmask a websocket frame
     * @param ch
     * @return
     * @throws MalfunctionedFrame
     */
    public static String unMaskFrame(byte[] ch) throws MalfunctionedFrame
    {
        byte[] mask = new byte[4];
        int len = ch.length;
        byte[] msg = null;
        if (len != -1) {
            len = (byte) (ch[1] & 127);
            int firstMask=2;
            if (len > 0) {
                if(len==126)
                {
                    len =(ch[3]&255)+(ch[2]&255)*256;
                    firstMask = 4;
                }
                else if(len==127)
                {
                    firstMask = 10;
                }
                for (int i = 0; i <  4; i++)
                {
                    mask[i] = ch[i+firstMask];
                }
                msg = new byte[len];
                for (int i = 0; i < len; i++)
                {
                    msg[i] = (byte) (ch[i+firstMask+4] ^ mask[i % 4]);
                }
                if (msg[0] == 3 && msg[1] == -23)  // socket closed by browser (strange no meaning..!)
                {
                    throw new MalfunctionedFrame();
                }
            }
            return (msg != null) ? byteToString(msg) : null;
        }
        throw new MalfunctionedFrame();
    }
    
    public static String getWebSocketAccept(String secWebSocketKey) throws NoSuchAlgorithmException
    {
        final String webSocketMagicNumber= "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        String webSocketAccept = secWebSocketKey + webSocketMagicNumber;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        byte[] digested = messageDigest.digest(webSocketAccept.getBytes());
        return Base64.encode(digested);
    }
    
    public static String byteToString(byte[] inp) {
        char[] sd = new char[inp.length];
        for (int i = 0; i < inp.length; i++) {
            sd[i] = (char) inp[i];
        }
        return String.copyValueOf(sd);
    }
}
