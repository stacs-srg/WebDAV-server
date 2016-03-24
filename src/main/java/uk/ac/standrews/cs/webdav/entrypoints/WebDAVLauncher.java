/**
 * Created on Aug 15, 2005 at 11:35:07 AM.
 */
package uk.ac.standrews.cs.webdav.entrypoints;

import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.util.*;

import java.io.IOException;

/**
 * Launcher that brings together policies for how to create store and file system for a
 * WebDAV server.
 *
 * @author graham
 */
public class WebDAVLauncher {

	/**
	 * Creates a store and file system, and runs a WebDAV server over it.
	 * 
	 * Usage: java WebDAVLauncher -r<store root guid> [-p<port>] [-d<store root directory>] [-s<store name>] [-D]
	 * 
	 * @param args optional command line arguments	
	 */
	public static void main(String[] args) {
		
	    String root_GUID_string = CommandLineArgs.getArg(args, "-r");
	    
	    // Can't continue if no root GUID string supplied.
	    if (root_GUID_string != null) {
	    	
	        // ********************************************************************************

	    	// Read diagnostic level from the console if not already specified in command line argument.
			Diagnostic.setLevel(Diagnostic.FULL);

			if (CommandLineArgs.getArg(args, "-D") == null) {
	    		
				Output.getSingleton().print("Enter D<return> for diagnostics, anything else for no diagnostics: ");
				String input = CommandLineInput.readLine();

		        if (! input.equalsIgnoreCase("D")) Diagnostic.setLevel(Diagnostic.NONE);
			}
			
	        // ********************************************************************************

			// Get port number.
		    int port = 0;
		    String port_string = CommandLineArgs.getArg(args, "-p");	    // Get port argument.
	   	    if (port_string != null) port = Integer.parseInt(port_string);	// Try to extract port number.
	    		    
	        // ********************************************************************************

 			try {
				IGUID root_GUID = GUIDFactory.recreateGUID(root_GUID_string);

				// Initialise the store.
				IGUIDStore store = new LocalFileBasedStoreFactory(args).makeStore();
	        
		        // Initialise a file system using the store.
		        IFileSystem file_system = new StoreBasedFileSystemFactory(store, root_GUID).makeFileSystem();
	
		        // Run the WebDAV server.
		        WebDAVServer server;
		        
		        if (port == 0) server = new WebDAVServer(file_system);
		        else           server = new WebDAVServer(file_system, port);
		        
		        server.run();
			}
			catch (FileSystemCreationException e) { Error.exceptionError("couldn't create file system", e); }
			catch (IOException e)                 { Error.exceptionError("socket error", e); }
	    }
	    else Output.getSingleton().println("Usage: java WebDAVLauncher -r<store root guid> [-p<port>] [-d<store root directory>] [-s<store name>] [-D]");
	}
}
