package server;

public class PUT extends POST {
	
	public PUT(Request request, FileLocks fileLocks, String root) {
		super(request, fileLocks, root);
		this.allowCreation = true;
	}
	
}
