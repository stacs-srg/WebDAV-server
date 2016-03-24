/*
 * Created on May 21, 2005 at 1:09:52 PM.
 */
package uk.ac.standrews.cs.persistence.impl;

import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IPersistentObject;
import uk.ac.standrews.cs.util.GUIDFactory;

/**
 * Skeleton implementation of IPersistentObject.
 * 
 * @author al
 */
public abstract class PersistentBase implements IPersistentObject {
	
    protected IGUID guid = null;
    protected IPID pid = null;
    
    /*********************** IPersistentObject Operations ***********************/
    
    /**
     * For use by sub-classes; records given GUID and PID.
     * 
     * @param guid the GUID
     * @param pid the PID
     */
    public PersistentBase(IGUID guid, IPID pid) { 	
        this.guid = guid;   
        this.pid = pid;
    }
    
    /**
     * For use by sub-classes; records given GUID.
     * 
     * @param guid the GUID
     */
    public PersistentBase(IGUID guid) { 	
        this.guid = guid;
    }
    
    /**
     * For use by sub-classes; creates a new random GUID.
     */
    public PersistentBase() {
        this(generateRandomGUID());
    }
    
    /**
     * Gets the GUID of the object.
     * 
     * @return the GUID of the object
     */
    public IGUID getGUID() {
        return guid;
    }
    
    /**
     * Gets the PID referring to the object's most recently recorded persistent state.
     * 
     * @return the PID for the object's most recent persistent state
     */
    public IPID getPID() {
        return pid;
    }
    
    /**
     * Records the object's current state.
     * 
     * @throws PersistenceException if the object's state could not be recorded
     *
     * @see uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPersistentObject#persist()
     */
    public abstract void persist() throws PersistenceException;
    
    public static IGUID generateRandomGUID() {
    	
    	return GUIDFactory.generateRandomGUID();
    }
}
