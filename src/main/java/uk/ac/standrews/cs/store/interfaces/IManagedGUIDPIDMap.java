/*
 * Created on 09-Aug-2005
 */
package uk.ac.standrews.cs.store.interfaces;

import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;

/**
 * Version of GUIDPIDMap that allows GUIDs and PIDs to be removed.
 *
 * @author stuart, graham
 */
public interface IManagedGUIDPIDMap extends IGUIDPIDMap {
	
    /**
     * Removes the given GUID from the map.
     * 
     * @param guid the GUID to be removed
     */
    void removeGUID(IGUID guid);
    
    /**
     * Removes the given version from the map.
     * 
     * @param guid the GUID to be removed
     * @param versionPID the PID to be removed
     */
    void removeVersion(IGUID guid, IPID versionPID);
}
