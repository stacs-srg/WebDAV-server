package uk.ac.standrews.cs.eventModel.networkEventFactory.factoryInterfaces;

import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * @author stuart
 */
public interface INetworkEventFactory {
    
	public Event makeNetworkEvent(Event e);
}
