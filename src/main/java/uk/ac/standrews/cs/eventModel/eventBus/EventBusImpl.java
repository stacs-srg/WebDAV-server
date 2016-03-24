/*
 * Created on 01-Nov-2004
 */
package uk.ac.standrews.cs.eventModel.eventBus;

import uk.ac.standrews.cs.eventModel.Event;
import uk.ac.standrews.cs.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.standrews.cs.eventModel.eventBus.busInterfaces.EventConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author stuart, graham
 */
public class EventBusImpl implements EventBus {
    
	private Collection registered_consumers;

	public EventBusImpl() {
		
		registered_consumers = new ArrayList();
	}
	
	/**
	 * Distributes the given event to all interested consumers.
	 * 
	 * @param event the event to be distributed
	 */
	public void publishEvent(Event event) {
		
		Iterator iterator = registered_consumers.iterator();
		
		while (iterator.hasNext()) {
			
			EventConsumer event_consumer = (EventConsumer)iterator.next();
			if (event_consumer.interested(event)) event_consumer.receiveEvent(event);
		}
	}
	
	/**
	 * Registers a new consumer with the event bus.
	 * 
	 * @param consumer the event consumer to be registered
	 */
	public void register(EventConsumer consumer) {
		
		registered_consumers.add(consumer);
	}

	/**
	 * Removes a consumer from the event bus.
	 * 
	 * @param consumer the event consumer to be removed
	 */
	public void unregister(EventConsumer consumer) {
		
		registered_consumers.remove(consumer);
	}
}
