/*
 * Created on 03-Nov-2004
 */
package uk.ac.standrews.cs.eventModel.eventBus.consumers;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;

/**
 * @author stuart, graham
 */
public class DiagnosticEventStringWriter implements EventConsumer {

	public boolean interested(Event event) {

		return event.getType().equals("DiagnosticEvent");
	}

	public void receiveEvent(Event event) {

		System.out.println(event.get("msg"));
	}
}
