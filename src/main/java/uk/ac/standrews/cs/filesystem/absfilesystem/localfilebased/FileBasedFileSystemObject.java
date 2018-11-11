/**
 * Created on Sep 9, 2005 at 12:36:03 PM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.localfilebased;

import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemObject;
import uk.ac.standrews.cs.fs.persistence.impl.FileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;
import uk.ac.standrews.cs.utilities.network.UriUtil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Stateful object that uses the file system as its persistence mechanism.
 * 
 * @author al, graham
 */
public abstract class FileBasedFileSystemObject extends FileSystemObject implements IFileSystemObject {
	
	protected String name;
	protected IDirectory logical_parent;
	protected File real_file;

    public FileBasedFileSystemObject() {
        super(null);
        this.name = "";
    }
 
    public FileBasedFileSystemObject(IDirectory logical_parent, String name) {
        super(null);
        this.name = name;
        this.logical_parent = logical_parent;
    }
    
    public FileBasedFileSystemObject(IDirectory logical_parent, String name, IData data) {
        super(data, null);
        this.name = name;
    }

	protected File getRealFile() {
		return real_file;
	}

	public URI getURI() {

		try {
			return new URI(UriUtil.uriEncode(real_file.getCanonicalPath()));
		}
		catch (URISyntaxException e) {
			ErrorHandling.hardExceptionError(e, "uri syntax error", e);
			return null;
		}
		catch (IOException e) {
			ErrorHandling.hardExceptionError(e, "I/O error", e);
			return null;
		}
	}
}
