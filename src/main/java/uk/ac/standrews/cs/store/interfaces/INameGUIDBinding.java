/*
 * Created on May 20, 2005 at 12:02:37 PM.
 */
package uk.ac.standrews.cs.store.interfaces;

import uk.ac.standrews.cs.IGUID;

/**
 * Represents a binding between a logical name and a GUID (identifying an object). A directory (IName_GUID_Map) contains
 * a collection of these bindings.
 *
 * @author al
 */
public interface INameGUIDBinding {
	
    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();
    
    /**
     * Gets the GUID.
     * 
     * @return the GUID
     */
    IGUID getGUID();
}
