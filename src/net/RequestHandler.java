package net;

import java.net.Socket;

import net.http.Request;


public interface RequestHandler {
	public void process(Request request,Socket client);
}
