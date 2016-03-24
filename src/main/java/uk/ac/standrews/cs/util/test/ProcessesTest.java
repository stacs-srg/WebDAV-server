/*
 * Created on 16-Feb-2005
 */
package uk.ac.standrews.cs.util.test;

import com.mindbright.ssh2.SSH2SimpleClient;
import uk.ac.stand.dcs.asa.util.Processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author graham
 */
public class ProcessesTest {
	
	public static void main(String[] args) {
		
		testRunRemoteProcess();
	}

    public static void testRunProcess() {
		
		try {
            Processes.runProcess("uname -a");
        }
		catch (IOException e) { Error.exceptionError("error executing local command", e); }
	}
    
    public static void testRunRemoteProcess() {
    	
    	String command = "sleep 10000";
    	InetAddress server = null;
		try {
			server = InetAddress.getByName("cordelia.dcs.st-and.ac.uk");
		} catch (UnknownHostException e) { Error.exceptionError("error creating InetAddress", e); }
		
    	String user = "graham";
    	File privateKeyFilePath = new File("/Users/graham/.ssh/identity");
    	
    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("enter ssh passphrase :");
    	
    	try {
        	String passPhrase = reader.readLine();

        	SSH2SimpleClient client = Processes.makeClient(server, user, privateKeyFilePath, passPhrase);
        	Process p = Processes.runProcess(command, client);
    	    Thread.sleep(10000);
    	    p.destroy();
    	}
    	catch (Exception e) {Error.exceptionError("error executing remote command", e); }
    }
}
