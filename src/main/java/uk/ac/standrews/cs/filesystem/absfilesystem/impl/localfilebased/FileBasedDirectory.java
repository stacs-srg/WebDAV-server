/**
 * Created on Sep 9, 2005 at 12:36:03 PM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.impl.localfilebased;

import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.impl.localfilebased.FileData;
import uk.ac.standrews.cs.util.Attributes;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Error;

import java.io.File;
import java.util.Iterator;

/**
 * Directory implementation using real local file system.
 * 
 * @author graham
 */
public class FileBasedDirectory extends FileBasedFileSystemObject implements IDirectory {

    public FileBasedDirectory(IDirectory logical_parent, String name) {
    	
        super(logical_parent, name);
        
        real_file = new File(((FileBasedDirectory)logical_parent).getRealFile(), name);
    }

    /**
     * Used for creating the logical root directory.
     */
    public FileBasedDirectory(File real_directory) {
    	
        super();
        
        logical_parent = null;
        
        real_file = real_directory;
    }

    public IDirectory getParent() {
        return logical_parent;
    }
    
    public void setParent(IDirectory logical_parent) {
        this.logical_parent = logical_parent;
    }

	public Iterator iterator() {
        return new DirectoryIterator(real_file);
	}

	public IAttributedStatefulObject get(String name) {

		File candidate = new File(real_file, name);
		if (!candidate.exists()) {
			Diagnostic.trace("Getting name - name not found: " + name, Diagnostic.RUN);
			return null;
		}
		
		if (candidate.isFile()) {
			IData file_data = new FileData(candidate);
			try {
				return new FileBasedFile(this, name, file_data);
			}
			catch (PersistenceException e) {
				Error.exceptionError("couldn't read file data", e);
				return null;
			}
		}
		
		if (candidate.isDirectory()) {
			return new FileBasedDirectory(this, name);
		}
		
		Error.hardError("directory entry not file or directory");
		return null;
	}

	public boolean contains(String name) {
		
		File candidate = new File(real_file, name);
		return candidate.exists();
	}

	public void addFile(String name, IFile file, String contentType) throws BindingPresentException {
		
		// Don't need to do anything since file can't be created in isolation from parent directory.
	}

	public void addDirectory(String name, IDirectory directory) throws BindingPresentException {

		// Don't need to do anything since directory can't be created in isolation from parent directory.
	}

	public void remove(String name) throws BindingAbsentException {
		
		File candidate = new File(real_file, name);
		if (!candidate.exists()) throw new BindingAbsentException("file " + name + " not present");
		
		candidate.delete();    // Ignore result - nothing to do with it.
	}

	public IAttributes getAttributes() {

        IAttributes attributes = new Attributes(FileSystemConstants.ISDIRECTORY + Attributes.EQUALS + "true" + Attributes.SEPARATOR);
        
        return attributes;
	}

	public void setAttributes(IAttributes atts) {
		Error.hardError("unimplemented method");
	}

    public long getCreationTime() {
        return 0;
    }

    public long getModificationTime() {
        return real_file.lastModified();
    }

	public void persist() throws PersistenceException {

		if (real_file.exists()) {
			if (!real_file.isDirectory()) throw new PersistenceException("directory file isn't a directory");
		}
		else {
			if (!real_file.mkdir()) throw new PersistenceException("couldn't create directory");
		}
	}

	private class DirectoryIterator implements Iterator {

        String[] names;
        int index;
        
        public DirectoryIterator(File real_file) {

            names = real_file.list();
            if (names == null) Error.hardError("file: " + real_file.getPath() + " is not a directory");
            index = 0;
        }

        public void remove() {
    		Error.hardError("unimplemented method");
        }

        public boolean hasNext() {
            return index < names.length;
        }

        public Object next() {
        	
        	String name = names[index];
        	IAttributedStatefulObject obj = get(name);
        	index++;
        	
        	return new NameAttributedPersistentObjectBinding(name, obj);
        }
    }
}
