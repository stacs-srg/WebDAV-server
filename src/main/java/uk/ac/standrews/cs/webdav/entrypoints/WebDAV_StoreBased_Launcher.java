/**
 * Created on Aug 15, 2005 at 11:35:07 AM.
 */
package uk.ac.standrews.cs.webdav.entrypoints;

import uk.ac.standrews.cs.filesystem.factories.StoreFileSystemFactory;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.store.factories.LocalFileBasedStoreFactory;
import uk.ac.standrews.cs.fs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.io.IOException;

/**
 * Launcher that brings together policies for how to create store and file system for a
 * WebDAV server.
 *
 * @author graham
 */
public class WebDAV_StoreBased_Launcher extends WebDAVLauncher {

	/**
	 * Creates a store and file system, and runs a WebDAV server over it.
	 * 
	 * Usage: java WebDAV_StoreBased_Launcher -r<store root guid> [-p<port>] [-d<store root directory>] [-s<store name>] [-D]
	 * 
	 * @param args optional command line arguments	
	 */
	public static void main(String[] args) {

        String root_GUID_string = processStoreRoot(args);

	    // Can't continue if no root GUID string supplied.
	    if (root_GUID_string != null) {
            processDiagnostic(args);
	        int port = processPort(args);

 			try {
				IGUID root_GUID = GUIDFactory.recreateGUID(root_GUID_string);
				IGUIDStore store = new LocalFileBasedStoreFactory(args).makeStore();
		        IFileSystem file_system = new StoreFileSystemFactory(store, root_GUID).makeFileSystem();

				StartWebDAVServer(file_system, port);
			} catch (FileSystemCreationException e) {
				ErrorHandling.exceptionError(e, "couldn't create file system", e);
			} catch (IOException e) {
				ErrorHandling.exceptionError(e, "socket error", e);
			} catch (GUIDGenerationException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Usage: java WebDAV_StoreBased_Launcher -r<store root guid> [-p<port>] [-d<store root directory>] [-s<store name>] [-D]");
		}
	}

}
