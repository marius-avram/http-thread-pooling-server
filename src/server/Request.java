package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.SizeLimitExceededException;

public class Request {
	private String method;
	private String path;
	private Boolean keepAlive;
	private int contentLength;
	private List<String> methods;
	private String boundary;
	private byte[] content;
	
	public Request() {
		methods = new ArrayList<String>();
		methods.add("GET");
		methods.add("POST");
		methods.add("PUT");
		methods.add("DELETE");
		this.boundary = null;
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Boolean getKeepAlive() {
		return keepAlive;
	}
	public void setKeepAlive(Boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public String getBoundary() {
		return boundary;
	}
	
	public void parseHeaderLine(String line) {
		// Method line
		// Extract method and path from it
		for (String method : methods) {
			if (line.startsWith(method)) {
				this.method = method;
				StringTokenizer tokenizer = new StringTokenizer(line, " ");
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken().trim();
					if (token.equals(method)) {
						String path = tokenizer.nextToken().trim();
						this.path = path;
						if (path.equals("/")) {
							this.path = "index.html";
						}
						System.out.println(path);
					}
				}
				break;
			}
		}
		if (line.startsWith("Connection") && line.contains("keep-alive")) {
			this.keepAlive = true;
		}
		if (line.startsWith("Content-Length")) {
			StringTokenizer tokenizer = new StringTokenizer(line, ":");
			tokenizer.nextToken();
			String contentLengthStr = tokenizer.nextToken().trim();
			int contentLength = Integer.parseInt(contentLengthStr);
			this.contentLength = contentLength;
		}
		if (line.contains("boundary")) {
			String boundaryToken = line.substring(line.indexOf("boundary="));
			boundary = "--" + boundaryToken.replace("boundary=", "");
		}
		
	}
	
	private String readHeader(BufferedInputStream input) throws IOException {
		String s = "";
	    int i;
	    while((i = input.read()) != -1) {
	        char c = (char) i;
	        s += c;
	       
	        if (s.endsWith("\r\n\r\n")) {
	        	break;
	        }
	    }
	    return s.trim();
	}
	
	public void read(BufferedInputStream input) throws IOException {
		
		// Read header
		String header = readHeader(input);
		String[] lines = header.split("\r\n");
		// Parse each line
		for (int i=0; i<lines.length; i++) {
			parseHeaderLine(lines[i]);
		}
		
		
		// Read content (if any)
		int contentLengthLocal = contentLength;
		int chunk = 5000;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		boolean firstBoundary = true; 
		while (contentLengthLocal > 0) {
			byte[] contentBuffer = new byte[chunk];
			int result = input.read(contentBuffer, 0, chunk);
			
			// Be careful about boundary if multi-part data
			if (boundary != null) {
				String contentStr = new String(contentBuffer, StandardCharsets.ISO_8859_1);
				int boundaryIndex = contentStr.indexOf(boundary);
				if (boundaryIndex >= 0 && firstBoundary) {
					int newlineIndex = contentStr.indexOf("\r\n\r\n", boundaryIndex);
					int skip = newlineIndex + 4;
					byteStream.write(contentBuffer, skip, result - skip);
					firstBoundary = false;
				}
				else if (boundaryIndex >= 0 && !firstBoundary) {
					byteStream.write(contentBuffer, 0, boundaryIndex);
				}
				else {
					byteStream.write(contentBuffer, 0, result);
				}
			}
			else {
				byteStream.write(contentBuffer, 0, result);
			}
			contentLengthLocal -= result;
		}
		content = byteStream.toByteArray();
	}
	
	
	
}
