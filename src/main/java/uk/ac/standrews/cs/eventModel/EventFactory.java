/**
 * Created on Jul 1, 2005 at 10:05:09 AM.
 */
package uk.ac.standrews.cs.eventModel;

/**
 * Defines utility methods for creating particular kinds of events.
 *
 * @author graham
 */
public class EventFactory {

	public static Event makeDiagnosticEvent(String msg) {
		
		Event event = new Event();
		
		event.setType("DiagnosticEvent");
		event.put("msg", msg);
		
		return event;
	}
	
	public static Event makeErrorEvent(String msg) {

		Event event = new Event();
		
		event.setType("ErrorEvent");
		event.put("msg", msg);
		
		return event;
	}
}
