/*
 * Created on May 20, 2005 at 11:22:06 AM.
 */
package uk.ac.standrews.cs.store.interfaces;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IPersistentObject;

import java.util.Iterator;

/**
 * A mapping from logical names to GUIDs. Each name maps to a single GUID.
 * This may be used to provide directory functionality.
 * 
 * @author al, graham
 */
public interface INameGUIDMap extends IPersistentObject {
	
    /**
     * Looks up a given name.
     * 
     * @param name the name to be looked up
     * @return the GUID associated with the name, or null if it is not found
     */
    IGUID get(String name);
    
    /**
     * Looks up attributes associated with a given name.
     * 
     * @param name the name to be looked up
     * @return the attributes associated with that name
     * @throws BindingAbsentException if a binding with the given name is not present
     */
    IAttributes getAttributes(String name) throws BindingAbsentException;
    
    /**
     * Associates attributes with a name to GUID binding.
     * NOTE THAT THIS IS DESTRUCTIVE AND OVERWRITES ANY OTHER ATTRIBUTES
     * NOTE also that attributes that are added to atts after this call will not be
     * added to the mapping. If you need to add new attributed you must re-add the attributes structure.
     * Side effects will not work - it is not clear to me if this is a good or bad decision at this time - al
     * 
     * @param name the name to which attributes should be added
     * @param atts the attributes to associate
     * @throws BindingAbsentException if a binding with the given name is not present
     */
    void setAttributes(String name, IAttributes atts) throws BindingAbsentException;
    
    /**
     * Adds a new name to GUID binding.
     * 
     * @param name the name to be added
     * @param guid the GUID to be added
     * @throws BindingPresentException if a binding with the given name is already present
     */
    void put(String name, IGUID guid) throws BindingPresentException;
    
    /**
     * Deletes a name to GUID binding.
     * 
     * @param name the name to be deleted
     * @throws BindingAbsentException if no binding with the given name is present
     */
    void delete(String name) throws BindingAbsentException;
    
    /**
     * Renames a binding.
     * 
     * @param old_name the old name
     * @param new_name the new name
     * @throws BindingAbsentException if no binding with the old name is present
     * @throws BindingPresentException if a binding with the new name is already present
     */
    void rename(String old_name, String new_name) throws BindingAbsentException, BindingPresentException;
    
    /**
     * Gets all the name-GUID bindings.
     * 
     * @return an iterator over all the bindings, each typed as {@link INameGUIDBinding}
     */
    Iterator iterator();
}
