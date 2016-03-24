package uk.ac.standrews.cs.webdav.impl.methods.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.storage.webdav.impl.HTTP;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class AbstractMethodTest extends TestCase {

	protected static final String LF = String.valueOf((char) 10);
	protected static final String CRLF = new String(HTTP.CRLF);
	
	/**
	 * Construct new test instance
	 *
	 * @param name the test name
	 */
	public AbstractMethodTest(String name) {
		super(name);
	}

	public void showResponse(String host, int port, String label, String request_name, String request_string) {
		
		String response = processRequest(request_string, host, port);
		
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
		}
		catch (UnknownHostException e) { Error.exceptionError("connecting to host", e); return ""; }
		catch (IOException e) { Error.exceptionError("connecting to host", e); return ""; }
	}
}
