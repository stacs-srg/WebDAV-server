/*
 * Created on 02-Nov-2004
 */
package uk.ac.standrews.cs.eventModel.eventBus.test;

import uk.ac.standrews.cs.eventModel.Event;
import uk.ac.standrews.cs.eventModel.eventBus.busInterfaces.EventConsumer;

/**
 * @author stuart, graham
 */
public class MyConsumer implements EventConsumer {
	
	private String prefix;

	
	public MyConsumer(String prefix) {
		
		this.prefix=prefix;
	}
	
	public boolean interested(Event event) {
		
		String event_type = event.getType();
		return event_type.equals("PredecessorRepEvent") || event_type.equals("SuccessorRepEvent") || event_type.equals("JChordRepEvent");
	}

	public void receiveEvent(Event event) {
		
		if (event.getType().equals("PredecessorRepEvent")) System.out.println(prefix+" : received PredecessorRepEvent");
		if (event.getType().equals("SuccessorRepEvent"))   System.out.println(prefix+" : received SuccessorRepEvent");
		if (event.getType().equals("JChordRepEvent"))      System.out.println(prefix+" : received JChordRepEvent");
		
	}
}
