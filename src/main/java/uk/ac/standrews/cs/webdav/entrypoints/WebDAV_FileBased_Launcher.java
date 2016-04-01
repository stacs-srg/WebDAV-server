/**
 * Created on Aug 15, 2005 at 11:35:07 AM.
 */
package uk.ac.standrews.cs.webdav.entrypoints;

import uk.ac.standrews.cs.filesystem.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.filesystem.factories.LocalFileBasedFileSystemFactory;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.GUIDFactory;
import uk.ac.standrews.cs.util.Output;

import java.io.File;
import java.io.IOException;

/**
 * Launcher that runs a WebDAV server running over the local file system.
 *
 * @author graham
 */
public class WebDAV_FileBased_Launcher extends WebDAVLauncher {
	
	/**
	 * Creates a file system, and runs a WebDAV server over it.
	 * 
	 * Usage: java WebDAV_FileBased_Launcher -r<store root guid> [-p<port>] [-d<root directory>] [-D]
	 * 
	 * @param args optional command line arguments	
	 */
	public static void main(String[] args) {

		String root_directory_path = processDirectoryRoot(args);
        String root_GUID_string = processStoreRoot(args);

        // Can't continue if no root GUID string supplied.
        if (root_GUID_string != null) {
            processDiagnostic(args);
            int port = processPort(args);
			
			try {
				IGUID root_GUID = GUIDFactory.recreateGUID(root_GUID_string);
				File root_directory = new File(root_directory_path);
				IFileSystem file_system = new LocalFileBasedFileSystemFactory(root_directory, root_GUID).makeFileSystem();

				startWebDAVServer(file_system, port);
			} catch (FileSystemCreationException e) {
				Error.exceptionError("couldn't create file system", e);
			} catch (IOException e) {
				Error.exceptionError("socket error", e);
			}
		}
		else Output.getSingleton().println("Usage: java WebDAV_FileBased_Launcher -r<store root guid> [-p<port>] [-d<root directory>] [-D]");
	}

}
