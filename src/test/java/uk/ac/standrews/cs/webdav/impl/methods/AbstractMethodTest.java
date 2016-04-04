package uk.ac.standrews.cs.webdav.impl.methods;

import org.junit.Before;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.webdav.entrypoints.WebDAV_FileBased_Launcher;
import uk.ac.standrews.cs.webdav.impl.HTTP;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class AbstractMethodTest {

	protected static final String LF = String.valueOf((char) 10);
	protected static final String CRLF = new String(HTTP.CRLF);

    protected static final String TEST_HOST = "localhost";
    protected static final int TEST_PORT = 9093;
    protected static final String TEST_LABEL = "ASA";

    // TODO - better naming + consider windows and linux platforms
    protected static final String TEST_PATH = "test";

	@Before
	public void setUp() throws InterruptedException {

		System.out.println("starting server (terrible!)");
		String[] args = {"-r73e057624a5b5005ab0e35ca45f6fb48ddfa8d5e",  "-p"+TEST_PORT ,"-d/Users/sic2/webdav", "-stest" + TEST_PATH, "-D"};

		Thread t = new Thread() {
			public void run() {
				WebDAV_FileBased_Launcher.main(args);
			}
		};
		t.start();
		System.out.println("server started");

		Thread t2 = new Thread() {
			public void run() {

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t2.start();
	}

	public void showResponse(String host, int port, String label, String request_name, String request_string) {
		
		String response = processRequest(request_string, host, port);
		
		System.out.println("Response from port: " + port + " (" + label + ") for request: " + request_name);
		System.out.println("---------------------");
		System.out.println(response);
	}

	public void showResponse(String response, int port, String label, String request_name) {
		System.out.println("Response from port: " + port + " (" + label + ") for request: " + request_name);
		System.out.println("---------------------");
		System.out.println(response);
	}

	public String processRequest(String request, String host, int port) {
		
		try {
			Socket socket = new Socket(host, port);
	
			InputStream input_stream = socket.getInputStream();
			OutputStream output_stream = socket.getOutputStream();
	        Writer writer = new OutputStreamWriter(output_stream);
			StringBuffer reply = new StringBuffer();
			
			writer.write(request);
			writer.flush();
		
			while (input_stream.available() == 0) { /* Busy wait until result ready. */ }   
			
			while (input_stream.available() > 0) {
				int b = input_stream.read();
				reply.append((char) b);
			}
			
			output_stream.close();
			
			return reply.toString();
		} catch (UnknownHostException e) {
			Error.exceptionError("connecting to host", e); return "";
		} catch (IOException e) {
			Error.exceptionError("connecting to host", e); return "";
		}
	}
}
