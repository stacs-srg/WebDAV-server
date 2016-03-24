/**
 * Created on Jun 23, 2005 at 9:36:35 PM.
 */
package uk.ac.standrews.cs.eventModel;

import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;
import uk.ac.standrews.cs.interfaces.IEvent;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Generic event implementation.
 *
 * @author graham
 */
public class Event extends HashMap implements IEvent {
	
	private String type = "";
	
	static {
		RafdaRunTime.registerCustomSerializer(Event.class, new EventSerializer());
	}
	
	public Event() {
		super();
	}

	public Event(String type) {
		this();
		this.type = type;
	}

	/**
	 * Returns the value of the specified attribute.
	 * 
	 * @param attribute the name of an attribute
	 * @return the value of the specified attribute, or null if the event contains no value for this attribute.
	 */
	public Object get(String attribute) {

		return super.get(attribute);
	}

	/**
	 * Adds an attribute to the event.
	 * 
	 * @param attribute attribute with which the specified value is to be associated
	 * @param value value to be associated with the specified attribute
	 */
	public void put(String attribute, Object value) {
		
		super.put(attribute, value);
	}

	/**
	 * Returns an iterator over the event attributes.
	 * 
	 * @return an iterator over the attributes, each typed as String.
	 */
	public Iterator iterator() {

		return values().iterator();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
