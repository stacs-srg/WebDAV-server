/*
 * Created on May 24, 2005 at 9:56:57 AM.
 */
package uk.ac.standrews.cs.persistence.impl;

import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IData;

/**
 * Persistent object implementation which extracts normal object state reflectively, rather than using a separate state object.
 * This is intended to be sub-classed.
 * 
 * @author al
 */
public abstract class ReflectivePersistentObject extends PersistentBase {

    /**
     * For use by sub-classes; reinstantiates a previously stored object.
     * 
     * @param data the object's state
     * @param pid the object's PID
     * @param guid the object's GUID
     */
    public ReflectivePersistentObject(IData data, IPID pid, IGUID guid ) {
        initialise( data, pid, guid );
    }

    /**
     * For use by sub-classes; creates a new instance.
     */
    public ReflectivePersistentObject() {
        super();
    }

    /**
     * Gets a representation of the object.
     * 
     * @return the object's state
     * 
     * @see uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPersistentObject#reify()
     */
    public IData reify() {
    	
        // TODO RAFDA serialisation code needed to extract the current state of the object.
        return null;
    }
    
    /**
     * Initialises the object.
     * 
     * @param data the new state
     * @param pid the new PID
     * @param guid the new GUID
     * 
     * @see uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPersistentObject#initialise(uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData, uk.ac.stand.dcs.asa.interfaces.IPID, uk.ac.stand.dcs.asa.interfaces.IGUID)
     */
    public void initialise(IData data, IPID pid, IGUID guid) {
    	
        // TODO RAFDA serialisation code needed to set the current state of the object from 'data'.
    }
}
