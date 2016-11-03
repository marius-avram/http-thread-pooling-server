package server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class DELETE extends Response {

	private Request request;
	private FileLocks fileLocks;
	private String root;
	
	public DELETE(Request request, FileLocks fileLocks, String root) {
		super();
		this.request = request;
		this.fileLocks = fileLocks;
		this.root = root;
	}
	
	public byte[] createResponse() {
		String response = new String();
		int statusCode = 200;
		int responseLength = 0;
		String mimeType = "text/html; charset=iso-8859-1";
		
		if (!request.getPath().isEmpty()) {
			String path = request.getPath();
			String completePath = root + File.separator + path;
			Path pathVal = Paths.get(completePath);
			if (!Files.exists(pathVal)) {
				statusCode = 404;
			}
			else {
				Semaphore lock = fileLocks.getLock(completePath);
				try {
					lock.acquire();
					Files.delete(pathVal);
				} catch (IOException e) {
					System.err.println("Error while deleting file. " + e.getMessage());
					statusCode = 500;
				} catch (InterruptedException e) {
					System.err.println("Error while acquiring file ownership. " + e.getMessage());
					statusCode = 500;
				} finally {
					fileLocks.removeLock(completePath);
					lock.release();
				}
			}
		}
		else {
			// Internal server error
			statusCode = 500;
		}
		
		String statusValue = statuses.getOrDefault(statusCode, "Internal server error").toString();
		Date date = new Date();

		String header = "HTTP/1.1 " + statusCode + " " + statusValue + "\r\n" +
						"Date: " + date.toString() + "\r\n" +
						"Server: Server" + "\r\n" + 
						"Content-Length: " + responseLength + "\r\n" +
						"Connection: Closed \r\n" +
						"Content-Type: " + mimeType + "\r\n" +
						"\r\n";
		response = header;
		return response.getBytes(StandardCharsets.ISO_8859_1);
	}
	
}
