package server;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private List<Thread> threads;
	private ServerProperties properties;
	private FileLocks fileLocks;

	
	public Server() {
		threads = new ArrayList<Thread>();
		properties = new ServerProperties();
		fileLocks = new FileLocks();
	}
	
	public void run() {
		for (int i=0; i<properties.getNumThreads(); i++) {
			Connection connection = new Connection(properties, fileLocks, i);
			connection.start();
			threads.add(connection);
		}

		// Fail-safe mechanism
		// If a thread is not alive anymore create a new one
		while(true) {
			try {
				Thread.sleep(2000);
				for (int i=threads.size()-1; i>=0; i--) {
					if (!threads.get(i).isAlive()) {
						threads.remove(i);
						Connection connection = new Connection(properties, fileLocks, i);
						connection.start();
						threads.add(connection);
					}
				}
			} catch (InterruptedException e) {
				// Error on sleep
			}
		}
	}
	
	public static void main(String args[]) {
		Server server = new Server();
		server.run();
	}
}
