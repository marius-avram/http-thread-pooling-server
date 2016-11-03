package server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class FileLocks {
	private Map<String, Semaphore> locks;

	public FileLocks() {
		locks = new HashMap<String, Semaphore>();
	}
	
	
	public synchronized Semaphore getLock(String filename) {
		Semaphore lock = locks.get(filename);
		if (lock == null) {
			lock = new Semaphore(1);
			locks.put(filename, lock);
		}
		return lock;
	}
	
	public synchronized void removeLock(String filename) {
		locks.remove(filename);
	}
	
	
}
