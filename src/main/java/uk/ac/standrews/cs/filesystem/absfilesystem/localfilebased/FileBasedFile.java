/**
 * Created on Sep 9, 2005 at 12:36:03 PM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.localfilebased;

import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.util.Attributes;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.KeyImpl;
import uk.ac.standrews.cs.util.SHA1KeyFactory;
import uk.ac.standrews.cs.webdav.impl.MIME;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * File implementation using real local file system.
 * 
 * @author graham
 */
public class FileBasedFile extends FileBasedFileSystemObject implements IFile {

	/**
     * Used to create a new file.
     */
    public FileBasedFile(IDirectory logical_parent, String name, IData data) throws PersistenceException {
        super(logical_parent, name, data);
        
        if (!(logical_parent instanceof FileBasedDirectory)) {
			Error.hardError("parent of file-based file isn't file-based");
		}
        
        real_file = new File(((FileBasedDirectory)logical_parent).getRealFile(), name);
        
        try {
			guid = (KeyImpl) SHA1KeyFactory.generateKey(real_file.getCanonicalPath());    // generate GUID based on file path
		} catch (IOException e) {
			throw new PersistenceException("can't obtain file path for backing file");
		} 
        
        persist();
    }

	public long getCreationTime() {
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(real_file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            Error.error("Unable to get creation time for file " + real_file.getName());
        }

		return attr.creationTime().toMillis();
	}

	public long getModificationTime() {
		return real_file.lastModified();
	}

	public void persist() throws PersistenceException {
		
		if (real_file.exists()) {
			if (!real_file.isFile()) {
				throw new PersistenceException("backing file isn't a file");
			}
		} else {
			try {
				if (!real_file.createNewFile()) {
                    throw new PersistenceException("couldn't create file");
                }
			} catch (IOException e) {
                throw new PersistenceException("couldn't create file");
            }
		}
		
		// Write the data to the file.
        byte[] bytes = state.getState();
        
        try {
	        FileOutputStream output_stream = new FileOutputStream(real_file);
			output_stream.write(bytes);
			output_stream.close();
    	} catch (IOException e) {
            throw new PersistenceException("couldn't write data to file: "+ e.getMessage());
        }
	}

	public IAttributes getAttributes() {

		String content_type = MIME.getContentTypeFromFileName(name);

        IAttributes attributes = new Attributes(FileSystemConstants.ISFILE + Attributes.EQUALS + "true" + Attributes.SEPARATOR +
				FileSystemConstants.CONTENT + Attributes.EQUALS + content_type + Attributes.SEPARATOR );
        
        return attributes;
	}

	public void setAttributes(IAttributes atts) {
		Error.hardError("unimplemented method");
	}

}
