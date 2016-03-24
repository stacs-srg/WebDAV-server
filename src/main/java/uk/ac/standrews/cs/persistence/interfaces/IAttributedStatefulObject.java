/*
 * Created on May 30, 2005 at 2:12:56 PM.
 */
package uk.ac.standrews.cs.persistence.interfaces;


import uk.ac.standrews.cs.exceptions.AccessFailureException;

/**
 * Object with internal state and attributes.
 * 
 * @author al, graham
 */
public interface IAttributedStatefulObject extends IStatefulObject {

    /**
     * Returns the object's attributes.
     * 
     * @return the object's attributes
     */
    IAttributes getAttributes();
    
    /**
     * Sets the object's attributes.
     * 
     * @param attributes the new attributes
     */
    void setAttributes(IAttributes attributes);
    
    /**
     * Returns the time at which the object was created.
     * 
     * @return the time at which the object was created
     * 
     * @throws AccessFailureException if the creation time could not be accessed
     */
    long getCreationTime() throws AccessFailureException;
    
    /**
     * Returns the time at which the object was last modified.
     * 
     * @return the time at which the object was last modified
     * 
     * @throws AccessFailureException if the modification time could not be accessed
     */
    long getModificationTime() throws AccessFailureException;
}
