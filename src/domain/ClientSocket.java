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
    public Socket client;

    private ConnectedBy connectionType = null;

    private int idleTime = 0;

    public boolean handshakeDone = false;

    public ClientSocket(Socket client)
    {
        this.client = client;
    }

    private void determineConnectionType() throws IOException, InterruptedException
    {
        Thread.sleep(500);
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

    public void doHandshake() {
        this.handshakeDone = true;
    }

    public void doWebsocketHandshake(Request request) throws IOException, NoSuchAlgorithmException
    {
        publishOut("HTTP/1.1 101 Switching Protocols\r");
        publishOut("Upgrade: websocket\r");
        publishOut("Connection: Upgrade\r");
        publishOut("Sec-WebSocket-Accept: " + WebSocket.getWebSocketAccept(request.getHeaders().get("Sec-WebSocket-Key")) + "\r");
        publishOut("Content-Encoding: identity\r");
        publishOut("\r");
    }
}
