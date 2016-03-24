/*
 * Created on 09-Aug-2005
 */
package uk.ac.standrews.cs.store.interfaces;

import uk.ac.standrews.cs.persistence.interfaces.IPIDGenerator;

/**
 * Version of IGUIDStore that allows GUIDs and PIDs to be removed.
 *
 * @author stuart, graham
 */
public interface IManagedGUIDStore extends IManagedGUIDPIDMap, IManagedStore {
	
    /**
     * Returns the PID generator for this store. This encapsulates the policy on
     * how to generate PIDs for given data.
     * 
     * @return the PID generator
     */
    IPIDGenerator getPIDGenerator();
}
