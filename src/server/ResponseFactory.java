package server;

public class ResponseFactory {
	
	public static Response create(Request request, FileLocks fileLocks, String root) {
		Response response = null;
		if (request.getMethod().equals("GET")) {
			return new GET(request, fileLocks, root);
		}
		else if (request.getMethod().equals("POST")) {
			return new POST(request, fileLocks, root);
		}
		else if (request.getMethod().equals("PUT")) {
			return new PUT(request, fileLocks, root);
		}
		else if (request.getMethod().equals("DELETE")) {
			return new DELETE(request, fileLocks, root);
		}
		return response;
	}
}
