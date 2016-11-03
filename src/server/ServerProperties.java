package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {
	private String root;
	private int numThreads;
	private int port;
	private int maxConnections;
	
	public ServerProperties() {
		
		Properties prop = new Properties();
		InputStream inputproperties = null;
		
		try {
			inputproperties = new FileInputStream("config/server.properties");
			prop.load(inputproperties);
			
			this.root = prop.getProperty("root", "www");
			String numThreadsStr = prop.getProperty("numThreads", "10");
			this.numThreads = Integer.parseInt(numThreadsStr);
			String portStr = prop.getProperty("port", "8000");
			this.port = Integer.parseInt(portStr);
			String backlogStr = prop.getProperty("maxConnections", "65536");
			this.maxConnections = Integer.parseInt(backlogStr);
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Properties file not found. " + e.getMessage());
		}
		catch (IOException e) {
			System.err.println("Error while parsing properties file " + e.getMessage());
		}
		
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public int getNumThreads() {
		return numThreads;
	}
	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getMaxConnections() {
		return maxConnections;
	}
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	
	
}
