/*
 * Created on 01-Nov-2004
 */
package uk.ac.standrews.cs.eventModel.eventBus.busInterfaces;

import uk.ac.standrews.cs.eventModel.Event;

/**
 * Event bus that accepts events and distributes them to all interested registered consumers.
 * 
 * @author stuart, graham
 */
public interface EventBus {
    
	/**
	 * Distributes the given event to all interested consumers.
	 * 
	 * @param event the event to be distributed
	 */
	void publishEvent(Event event);
	
	/**
	 * Registers a new consumer with the event bus.
	 * 
	 * @param consumer the event consumer to be registered
	 */
	void register(EventConsumer consumer);
	
	/**
	 * Removes a consumer from the event bus.
	 * 
	 * @param consumer the event consumer to be removed
	 */
	void unregister(EventConsumer consumer);
}
