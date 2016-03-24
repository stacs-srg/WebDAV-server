/*
 * Created on 01-Nov-2004
 */
package uk.ac.standrews.cs.eventModel.eventBus.busInterfaces;

import uk.ac.standrews.cs.eventModel.Event;

/**
 * @author stuart, graham
 */
public interface EventConsumer {
    
	boolean interested(Event event);
	
	void receiveEvent(Event event);
}
