/**
 * Created on Jun 23, 2005 at 9:38:38 PM.
 */
package uk.ac.standrews.cs.interfaces;

import java.util.Iterator;

/**
 * Interface defining generic events.
 *
 * @author graham
 */
public interface IEvent {

	/**
	 * Returns the value of the specified attribute.
	 * 
	 * @param attribute the name of an attribute
	 * @return the value of the specified attribute, or null if the event contains no value for this attribute.
	 */
	public Object get(String attribute);
	
	/**
	 * Adds an attribute to the event.
	 * 
	 * @param attribute attribute with which the specified value is to be associated
	 * @param value value to be associated with the specified attribute
	 */
	public void put(String attribute, Object value);
	
	/**
	 * Returns an iterator over the event attributes.
	 * 
	 * @return an iterator over the attributes, each typed as String.
	 */
	public Iterator iterator();
}
