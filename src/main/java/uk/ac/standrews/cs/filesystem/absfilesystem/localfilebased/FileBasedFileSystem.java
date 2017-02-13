/**
 * Created on Sep 9, 2005 at 12:36:03 PM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.localfilebased;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.filesystem.absfilesystem.AbstractFileSystem;
import uk.ac.standrews.cs.fs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.fs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.utils.Error;

import java.io.File;

/**
 * File system implementation using real local file system.
 * 
 * @author graham
 */
public class FileBasedFileSystem extends AbstractFileSystem implements IFileSystem {

	/**
     * Creates a file system operating over a given real file system directory.
     */
    public FileBasedFileSystem(File real_root_directory, IGUID root_GUID) {
    	super(root_GUID);
        
        if (!real_root_directory.exists()) {
			real_root_directory.mkdirs();
		}

        root_collection = new FileBasedDirectory(real_root_directory); 
    }

    /**
     * Creates a completely new file and a binding to it in a directory. Similar to 'creat' in Unix.
     * 
     * @param parent the directory in which the binding should be created
     * @param name the name for the directory entry
     * @param contentType the content type of the file
     * @param data the contents of the file
     * @return the new file
     * 
     * @throws BindingPresentException if a binding with the same name is already present in the directory
     * @throws PersistenceException if the new file cannot be made persistent
     */
    public IFile createNewFile(IDirectory parent, String name, String contentType, IData data) throws BindingPresentException, PersistenceException {

		check(parent, name, "file already exists", true);

		// Create the file.
		IFile new_file = new FileBasedFile(parent, name, data);

		return new_file;
	}

	public IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException {

		check(parent, name, "directory already exists", true);

		IDirectory new_directory = new FileBasedDirectory(parent, name);
		new_directory.persist();

		return new_directory;
	}

	public void moveObject(IDirectory sourceDirectory, String sourceName, IDirectory destinationDirectory, String destinationName, boolean overwrite) throws BindingAbsentException, BindingPresentException {
        
        IFileSystemObject source_object = sourceDirectory.get(sourceName);
        
        checkSourceAndDestination(sourceName, destinationDirectory, destinationName, overwrite, source_object);
        
        File real_source_directory = ((FileBasedDirectory)sourceDirectory).real_file;
        File real_destination_directory = ((FileBasedDirectory)destinationDirectory).real_file;
        
        File source_path = new File(real_source_directory, sourceName);
        File destination_path = new File(real_destination_directory, destinationName);
        
        if (!source_path.renameTo(destination_path)) Error.error("couldn't move object");
	}

    /************************************ Private methods ************************************/

	protected void addCopyOfFileToDirectory(IDirectory destinationParent, String destinationName, IFile file) throws PersistenceException {
		
        new FileBasedFile(destinationParent, destinationName, file.reify());
	}
}
