/*
 * Created on May 30, 2005 at 1:28:34 PM.
 */
package uk.ac.standrews.cs.filesystem.factories;

import uk.ac.standrews.cs.filesystem.absfilesystem.localfilebased.FileBasedFileSystem;
import uk.ac.standrews.cs.filesystem.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.interfaces.IGUID;

import java.io.File;

/**
 * Factory providing methods to create a new file system using a given store.
 *  
 * @author al, graham
 */
public class LocalFileBasedFileSystemFactory implements IFileSystemFactory {
	
	IGUID root_GUID;
	File real_root_directory;
    
    /**
     * Creates a file system factory using the specified store.
     */
   public LocalFileBasedFileSystemFactory(File real_root_directory, IGUID root_GUID) {
	   
	   this.real_root_directory = real_root_directory;
	   this.root_GUID = root_GUID;
     }
   
   public IFileSystem makeFileSystem() throws FileSystemCreationException {
	   
	   return new FileBasedFileSystem(real_root_directory, root_GUID);
   }
}
