/*
 * Created on May 23, 2005 at 1:59:30 PM.
 */
package uk.ac.standrews.cs.persistence.interfaces;

import uk.ac.standrews.cs.interfaces.IPID;

/**
 * A potentially persistent object that retains a link to its previous version.
 *
 * @author al
 */
public interface IVersionedPersistentObject extends IPersistentObject {
	
    /**
     * Gets the PID referring to the object's previously recorded persistent state.
     * 
     * @return the PID for the object's previous persistent state
     */
    IPID previous();
}
