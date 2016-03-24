/*
 * Created on May 20, 2005 at 12:04:39 PM.
 */
package uk.ac.standrews.cs.store.interfaces;

import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.exceptions.StoreGetException;
import uk.ac.standrews.cs.store.exceptions.StorePutException;

/**
 * A store that maps PIDs to data. The mapping is append-only; once a mapping
 * has been added it cannot be updated or deleted.
 *
 * @author al
 */
public interface IStore {
	
    /**
     * Gets the persistent object associated with a given PID.
     * 
     * @param pid the PID of a persistent object
     * @return the contents of the corresponding persistent object, or null if no object found
     * 
     * @throws StoreGetException if an internal store error occurred
     */
    IData get(IPID pid) throws StoreGetException;
    
    /**
     * Puts an object into the store.
     * 
     * @param data the contents of an object to be made persistent
     * @return a PID for subsequent retrieval of the object
     * 
     * @throws StorePutException if the object could not be made persistent
     */
    IPID put(IData data) throws StorePutException;
    
    /**
     * Gets the time/date at which the given PID was first entered in the store.
     * 
     * @param pid the PID being queried
     * @return the time/date at which the given PID was first entered in the store
     * 
     * @throws StoreGetException if the time/date could not be accessed
     */
    long getPIDPutDate(IPID pid) throws StoreGetException;
}
