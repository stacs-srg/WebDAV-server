/*
 * Created on Jun 16, 2005 at 9:03:03 AM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.impl.storebased;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.impl.AttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.exceptions.StoreGetException;
import uk.ac.standrews.cs.store.exceptions.StorePutException;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;

/**
 * Attributed stateful object that uses a store as its persistence mechanism.
 * 
 * @author al, graham
 */
public abstract class StoreBasedFileSystemObject extends AttributedStatefulObject implements IAttributedStatefulObject {

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
		}
        catch (StorePutException e) { throw new PersistenceException(e.getMessage()); }
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
