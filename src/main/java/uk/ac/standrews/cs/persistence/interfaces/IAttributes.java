/*
 * Created on Jun 17, 2005 at 4:48:12 PM.
 */
package uk.ac.standrews.cs.persistence.interfaces;

import java.util.Iterator;

/**
 * Collection of string key-value pairs.
 * 
 * @author al, graham
 */
public interface IAttributes {
	
    /**
     * Adds a given attribute.
     * 
     * @param key the new attribute
     * @param value the new attribute value
     */
    void addAttribute(String key, String value);

    /**
     * Gets the value of the given attribute.
     * 
     * @param key the attribute to be looked up
     * @return the value of the attribute, or null if it is not found
     */
    String get(String key);
    
    /**
     * Tests whether the given attribute is present.
     * 
     * @param key the attribute to be tested
     * @return true if the attribute is present
     */
    boolean contains(String key);
    
    /**
     * Returns an iterator over the attributes.
     * 
     * @return an iterator over the attributes, typed as {@link IAttribute}
     */
    Iterator iterator();
}