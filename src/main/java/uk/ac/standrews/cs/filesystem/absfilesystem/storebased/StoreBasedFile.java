/*
 * Created on Jun 16, 2005 at 9:02:19 AM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.storebased;

import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;

/**
 * File implementation that knows about stores but not how they're implemented.
 * 
 * @author al, graham
 */
public class StoreBasedFile extends StoreBasedFileSystemObject implements IFile {

	String file_separator = System.getProperty ("file.separator");
	protected String name;
	protected IDirectory parent;

    /**
     * Used to create an instance corresponding to an extant file.
     */
    public StoreBasedFile(IGUIDStore store, IData data, IGUID guid, IPID pid, IAttributes atts) {
        super(store, data, pid, guid, atts);
    }
    
    /**
     * Used to create a new file.
     */
    public StoreBasedFile(IGUIDStore store, IData data, IAttributes atts) throws PersistenceException {
    	
        super(store, data, atts);
        persist();
    }
    
    public StoreBasedFile(IGUIDStore store, IData data) throws PersistenceException {
    	
        this(store, data, null);
    }
}
