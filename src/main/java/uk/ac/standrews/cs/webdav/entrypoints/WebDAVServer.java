package uk.ac.standrews.cs.webdav.entrypoints;

import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.locking.impl.LockManager;
import uk.ac.standrews.cs.locking.interfaces.ILockManager;
import uk.ac.standrews.cs.util.Action;
import uk.ac.standrews.cs.util.ActionQueue;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.webdav.impl.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * HTTP server.
 * 
 * @author al, stuart, graham
 */
public class WebDAVServer {
	
	private final static int DEFAULT_PORT =        9090;     // Default port to listen on.
	private static final int QUEUE_LENGTH =        20;       // Maximum number of outstanding requests.
	private static final int MAX_THREADS =         10;       // Maximum number of threads.
	private static final int THREAD_IDLE_TIMEOUT = 20000;    // Timeout in milliseconds after which an idle thread will die.
	
	private IFileSystem file_system;
	private ILockManager lock_manager;
	private int port;
	private ActionQueue request_queue;
	
	/*********************** Constructors ************************/
	
	public WebDAVServer(IFileSystem file_system) {
		this(file_system, DEFAULT_PORT);
	}
	
	public WebDAVServer(IFileSystem file_system, int port) {
		this.file_system = file_system;
		this.port = port;
		lock_manager = new LockManager();
		request_queue = new ActionQueue(QUEUE_LENGTH, MAX_THREADS, THREAD_IDLE_TIMEOUT);
	}
	
	/*********************** Methods ************************/
	
	public void run() throws IOException {
		
		ServerSocket server_socket = new ServerSocket(port);
		
		// Show startup confirmation whatever the run level...
		Diagnostic.trace("ASA WebDav Server started on port " + port);
		
		while (true) {
			Socket client = server_socket.accept();
			Diagnostic.trace("Accepted connection",Diagnostic.RUN);
			final RequestHandler handler = new RequestHandler(client, file_system, lock_manager);
			
			if (request_queue.freeSpace() == 0) Diagnostic.trace("request queue full", Diagnostic.INIT);

			request_queue.enqueue(new Action() {

				public void performAction() {
					handler.run();
				}
			});
		}
	}
}
