package exceptions;

public class ConnectionTerminated extends Throwable {
	public ConnectionTerminated(){
		super("Remote Connection Closed");
	}
}
