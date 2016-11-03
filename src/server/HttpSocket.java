package server;

import java.io.IOException;
import java.net.ServerSocket;

// Only a single instance of HttpSocket can exist
public class HttpSocket {
	private int portNumber;
	private int maxConnections;
	private ServerSocket serverSocket = null;
	private static HttpSocket instance = null; 
	
	
	private HttpSocket(int portNumber, int maxConnections) {
		this.portNumber = portNumber;
		this.maxConnections = maxConnections;
		try {
			serverSocket = new ServerSocket(this.portNumber, this.maxConnections);
		} catch (IOException e) {
			System.err.println("Error while opening socket on port " + portNumber);
			System.err.println(e.getMessage());
		}
	}
	
	public static HttpSocket getInstance(int portNumber, int maxConnections) {
		if (instance == null) {
			instance = new HttpSocket(portNumber, maxConnections);
		}
		return instance;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
}
