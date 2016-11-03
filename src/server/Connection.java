package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import org.omg.CORBA.portable.OutputStream;

public class Connection extends Thread {
	private int portNumber = 8000;
	private int executorNumber;
	private HttpSocket httpSocket;
	private Boolean error = false;
	private String root;
	private FileLocks fileLocks;
	
	
	public Connection(ServerProperties properties, FileLocks fileLocks, int executorNumber) {
		this.portNumber = properties.getPort();
		this.executorNumber = executorNumber;
		this.fileLocks = fileLocks;
		this.root = properties.getRoot();
		this.httpSocket = HttpSocket.getInstance(portNumber, properties.getMaxConnections());
	}
	
	public void run() {
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		// Accept a connection once it's free
		while (true) {
			try {
				if (error) {
					return;
				}
				System.out.println("#" + executorNumber + ": Waiting for connection..");
				Socket clientSocket = httpSocket.getServerSocket().accept();
				System.out.print("Accepted on #" + executorNumber);
				input = new BufferedInputStream(clientSocket.getInputStream());
				output = new BufferedOutputStream(clientSocket.getOutputStream());
				// Read request
				while (true) {
					Request request = new Request();
					try {
						request.read(input);
						clientSocket.setSoTimeout(15000);
						
						// Write response
						Response response = ResponseFactory.create(request, fileLocks, root);
						byte[] responseArr = response.createResponse();

						
						output.write(responseArr);
						output.flush();
					}
					catch (SocketTimeoutException e) {
						System.err.println("Timeout on port " + portNumber);
						System.err.println(e.getMessage());
						break;
					}
				}
	
				input.close();
				output.close();
			}
			catch (IOException e) {
				System.err.println("#" + executorNumber + ": Error while reading data on port " + portNumber);
				System.err.println(e.getMessage());
				try {
					input.close();
					output.close();
				}
				catch (Exception _) {
					// Nothing to do here
				}
			}
		}
	}
}
