import javax.sound.sampled.*;
import java.net.Socket;
import java.io.*;
import java.net.SocketAddress;

class oThread implements Runnable{
    PrintStream ascnop;
    BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
    Socket con;
    public Thread ioT;
    public oThread(){
        ioT = new Thread(this);
        ioT.start();
    }
    public void run(){
        while(true) {
            try {
                String portR = inp.readLine();
                int port = Integer.parseInt(portR);
               // String msg = inp.readLine();
                //con = new Socket("localhost", port);
                System.out.println("127.0.0.1:" + port + " " );
                //ascnop = new PrintStream(con.getOutputStream());
                //ascnop.println(msg + "\n");
            } catch (Exception c) {
                System.out.println(c.getMessage());
            }
        }
    }
}

public class http{
    public static void main(String arg[]) throws Exception{
        byte x[] = new byte[100];
        TargetDataLine line;
        float sampleRate = 16000;
        int sampleSizeinBits = 8;
        int channel = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate,sampleSizeinBits,channel,signed,bigEndian);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
        line =(TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
        Thread.sleep(30);

    }
}