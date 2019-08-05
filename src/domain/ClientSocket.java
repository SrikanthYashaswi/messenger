package domain;

import net.http.Request;
import net.ws.WebSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class ClientSocket
{
    static int nextId = 0;

    Socket client;

    ConnectedBy connectionType = null;

    int idleTime = 0;

    boolean handshakeDone = false;

    public ClientSocket()
    {

    }

    public ClientSocket(Socket client) throws IOException, InterruptedException {
        this.client = client;
        determineConnectionType();
    }

    /**
     * The thread sleep is required because it takes a fraction of second to get GET/POST kind of requests
     * which will help determine the request type
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void determineConnectionType() throws IOException, InterruptedException
    {
        Thread.sleep(1);
        InputStream inputStream = client.getInputStream();
        if (inputStream.available() > 0) {
            this.connectionType = ConnectedBy.BROWSER;
        } else {
            this.connectionType = ConnectedBy.CONSOLE;
            doHandshake();
        }
    }

    public void publishOut(String message) throws IOException
    {
        PrintStream o = new PrintStream(client.getOutputStream());

        if (connectionType == ConnectedBy.WEBSOCKET) {
            byte[] rawData = message.getBytes();
            byte[] reply = WebSocket.toWSFrame(rawData);
            o.write(reply);
        } else {
            o.println(message);
        }
    }

    private void doHandshake() {
        this.handshakeDone = true;
    }

    public void sendOk() throws IOException
    {
        publishOut("HTTP/1.1 200");
        publishOut("Connection: close\r\n");
    }

    private void setConnectionType(ConnectedBy conn) throws IOException{
        this.connectionType = conn;
    }

    public void beginWebsocketHandshake(Request request) throws IOException, NoSuchAlgorithmException
    {
        publishOut("HTTP/1.1 101 Switching Protocols\r");
        publishOut("Upgrade: websocket\r");
        publishOut("Connection: Upgrade\r");
        publishOut("Sec-WebSocket-Accept: " + WebSocket.getWebSocketAccept(request.getHeaders().get("Sec-WebSocket-Key")) + "\r");
        publishOut("Content-Encoding: identity\r");
        publishOut("\r");
        setConnectionType(ConnectedBy.WEBSOCKET);
        doHandshake();
    }

    public void increaseIdleTime(){
        idleTime++;
    }

    public int getIdleTime(){
        return idleTime;
    }

    public void resetIdleTime(){
        idleTime = 0;
    }

    public ConnectedBy getConnectionType() {
        return this.connectionType;
    }

    public void bye(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream() throws IOException
    {
        return this.client.getInputStream();
    }
}
