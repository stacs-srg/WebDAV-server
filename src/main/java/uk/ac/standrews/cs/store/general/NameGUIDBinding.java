/*
 * Created on May 22, 2005 at 2:35:52 PM.
 */
package uk.ac.standrews.cs.store.general;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.store.interfaces.INameGUIDBinding;

/**
 * Implements a binding between a logical name and a GUID (identifying an object).
 * A directory (IName_GUID_Map) contains
 * a collection of these bindings.
 *
 * @author al
 */
public class NameGUIDBinding implements INameGUIDBinding {

    private String name;
    private IGUID guid;
    
    /**
     * Creates a binding between a name and a GUID.
     * 
     * @param name the name
     * @param guid the GUID
     */
    public NameGUIDBinding(String name, IGUID guid) {
        this.name = name;
        this.guid = guid;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the GUID.
     * 
     * @return the GUID
     */
    public IGUID getGUID() {
        return guid;
    }

}
