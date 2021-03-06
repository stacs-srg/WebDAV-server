/*
 * Created on Jun 16, 2005 at 9:03:03 AM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.storebased;

import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemObject;
import uk.ac.standrews.cs.fs.persistence.impl.FileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.store.exceptions.StoreGetException;
import uk.ac.standrews.cs.fs.store.exceptions.StorePutException;
import uk.ac.standrews.cs.fs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IPID;

/**
 * Attributed stateful object that uses a store as its persistence mechanism.
 * 
 * @author al, graham
 */
public abstract class StoreBasedFileSystemObject extends FileSystemObject implements IFileSystemObject {

    protected IGUIDStore store;
    
    public StoreBasedFileSystemObject(IGUIDStore store, IAttributes atts) {
        super(atts);
        this.store = store;
    }
 
    public StoreBasedFileSystemObject(IGUIDStore store, IGUID guid, IAttributes atts) {
        super(guid, atts);
        this.store = store;
    }

    public StoreBasedFileSystemObject(IGUIDStore store, IData data, IAttributes atts) {
        super(data, atts);
        this.store = store;
    }
    
    public StoreBasedFileSystemObject(IGUIDStore store, IData data, IPID pid, IGUID guid, IAttributes atts ) {
        super(data, pid, guid, atts);
        this.store = store;
    }
    
    public void persist() throws PersistenceException {
        try {
            pid = store.put(reify());
            store.put(guid, pid);
		} catch (StorePutException e) {
            throw new PersistenceException(e.getMessage());
        }
	}
    
    public long getCreationTime() throws AccessFailureException {
        try {
            return store.getGUIDPutDate(guid);
        } catch (StoreGetException e) {
            throw new AccessFailureException("could not retrieve creation time");
        }
    }

    public long getModificationTime() throws AccessFailureException {
        try {
            return store.getPIDPutDate(pid);
        } catch (StoreGetException e) {
            throw new AccessFailureException("could not retrieve modification time");
        }
    }
}
