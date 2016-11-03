package server;

import java.util.HashMap;

public abstract class Response {
	
	HashMap<Integer, String> statuses;
	
	public Response() {
		statuses = new HashMap<Integer, String>();
		statuses.put(200, "OK");
		statuses.put(404, "Not Found");
		statuses.put(400, "Bad Request");
		statuses.put(500, "Internal server error");
	}
	
	public abstract byte[] createResponse();
	
	
}
