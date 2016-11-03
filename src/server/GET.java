package server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class GET extends Response {
	private Request request;
	private String root;
	private FileLocks fileLocks;
	
	public GET(Request request, FileLocks fileLocks, String root) {
		super();
		this.request = request;
		this.fileLocks = fileLocks;
		this.root = root;
	}
	
	public byte[] createResponse() {
		byte[] response = null;
		int statusCode = 200;
		int responseLength = 0;
		String mimeType = "text/html; charset=iso-8859-1";
		byte[] content = null;
		
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
					content = Files.readAllBytes(pathVal);
					mimeType = Files.probeContentType(pathVal);
					responseLength = content.length;
				} catch (IOException e) {
					System.err.println("Error while reading file. " + e.getMessage());
					statusCode = 500;
				} catch (InterruptedException e) {
					System.err.println("Error while acquiring file ownership. " + e.getMessage());
					statusCode = 500;
				} finally {
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
		response = header.getBytes(StandardCharsets.ISO_8859_1);
		if (responseLength > 0) {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream( );

			try {
				byteStream.write(response);
				byteStream.write(content);
				response = byteStream.toByteArray();
			} catch (IOException e) {
				System.err.println("Error while concatenating header and message. " + e.getMessage());
			}

			
		}
		return response;
	}
}
