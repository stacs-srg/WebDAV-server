/*
 * Created on May 25, 2005 at 12:12:53 PM.
 */
package uk.ac.standrews.cs.store.interfaces;

import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.store.exceptions.StoreGetException;
import uk.ac.standrews.cs.store.exceptions.StorePutException;

import java.util.Iterator;

/**
 * A mapping from GUIDs to PIDs. Each GUID maps to potentially multiple
 * PIDs. This may be used to keep track of multiple object versions.
 * 
 * @author al, graham
 */
public interface IGUIDPIDMap {
	
    /**
     * Gets the PID most recently associated with a GUID.
     * 
     * @param guid the GUID to be looked up
     * @return the latest PID associated with the given GUID
     * 
     * @throws StoreGetException if the PID could not be accessed
     */
    IPID getLatestPID(IGUID guid) throws StoreGetException;
    
    /**
     * Gets all the PIDs associated with a GUID.
     * 
     * @param guid the GUID to be looked up
     * @return an iterator over all the PIDs associated with the given GUID, each typed as {@link IPID}
     * 
     * @throws StoreGetException if the PIDs could not be accessed
     */    
    Iterator getAllPIDs(IGUID guid) throws StoreGetException;
    
    /**
     * Adds a new GUID to PID mapping.
     * 
     * @param guid the GUID
     * @param pid the PID
     * 
     * @throws StorePutException if the new mapping could not be added
     */
    void put(IGUID guid, IPID pid) throws StorePutException;
    
    /**
     * Gets the time/date at which the given GUID was first entered in the map.
     * 
     * @param guid the guid being queried
     * @return the time/date at which the given GUID was first entered in the map
     * 
     * @throws StoreGetException if the time/date could not be accessed
     */
    long getGUIDPutDate(IGUID guid) throws StoreGetException;
}
