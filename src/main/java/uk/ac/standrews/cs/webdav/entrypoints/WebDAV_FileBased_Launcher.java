/**
 * Created on Aug 15, 2005 at 11:35:07 AM.
 */
package uk.ac.standrews.cs.webdav.entrypoints;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.storage.absfilesystem.exceptions.FileSystemCreationException;
import uk.ac.stand.dcs.asa.storage.absfilesystem.factories.LocalFileBasedFileSystemFactory;
import uk.ac.stand.dcs.asa.storage.absfilesystem.interfaces.IFileSystem;
import uk.ac.stand.dcs.asa.util.*;

import java.io.File;
import java.io.IOException;

/**
 * Launcher that runs a WebDAV server running over the local file system.
 *
 * @author graham
 */
public class WebDAV_FileBased_Launcher {
	
	/**
	 * Creates a file system, and runs a WebDAV server over it.
	 * 
	 * Usage: java WebDAV_FileBased_Launcher -r<store root guid> [-p<port>] [-d<root directory>] [-D]
	 * 
	 * @param args optional command line arguments	
	 */
	public static void main(String[] args) {
		
		String root_GUID_string = CommandLineArgs.getArg(args, "-r");
		
		String root_directory_path = CommandLineArgs.getArg(args, "-d");
		
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
				
				File root_directory = new File(root_directory_path);
				
				// Initialise a file system using the store.
				IFileSystem file_system = new LocalFileBasedFileSystemFactory(root_directory, root_GUID).makeFileSystem();
				
				// Run the WebDAV server.
				WebDAVServer server;
				
				if (port == 0) server = new WebDAVServer(file_system);
				else           server = new WebDAVServer(file_system, port);
				
				server.run();
			}
			catch (FileSystemCreationException e) { Error.exceptionError("couldn't create file system", e); }
			catch (IOException e)                 { Error.exceptionError("socket error", e); }
		}
		else Output.getSingleton().println("Usage: java WebDAV_FileBased_Launcher -r<store root guid> [-p<port>] [-d<root directory>] [-D]");
	}
}
