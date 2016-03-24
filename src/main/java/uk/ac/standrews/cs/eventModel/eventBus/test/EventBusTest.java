/*
 * Created on 01-Nov-2004
 */
package uk.ac.standrews.cs.eventModel.eventBus.test;

import uk.ac.standrews.cs.eventModel.Event;
import uk.ac.standrews.cs.eventModel.eventBus.EventBusImpl;
import uk.ac.standrews.cs.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.standrews.cs.eventModel.eventBus.busInterfaces.EventConsumer;

/**
 * @author stuart
 */
public class EventBusTest {

	public static void main(String[] args) {
	    
		EventBus eb = new EventBusImpl();
		EventConsumer a1 = new MyConsumer("a1");
			
		eb.register(a1);
		
		eb.publishEvent(new Event("PredecessorRepEvent"));
		
		EventConsumer a2 = new MyConsumer("a2");
		eb.register(a2);
		
		eb.publishEvent(new Event("SuccessorRepEvent"));
		
		eb.unregister(a1);
		eb.publishEvent(new Event("PredecessorRepEvent"));
	}
}
