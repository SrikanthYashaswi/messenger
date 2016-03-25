import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.Format;
import java.util.LinkedList;

/**
 * Created by srika on 03-02-2016.
 */
class ui{
    public int vx;
    public int zx;

}
 class Sem{
   static Object v ;

}
public class test {
    public static void main(String arg[]) throws IOException {
        InputStream c = System.in;
        ServerSocket we = new ServerSocket(33);
        Socket clo = we.accept();
        InputStream qwqw = clo.getInputStream();
        byte x[]=new byte[100];
        char t[] = new char[100];
        System.out.println(qwqw.read(x));
        for(int i=0;i<x.length;i++)
        {
            t[i] = (char)x[i];
        }
        String cqw = String.copyValueOf(t);
        System.out.println(cqw);
    }

}
