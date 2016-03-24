package uk.ac.standrews.cs.eventModel;

import uk.ac.stand.dcs.rafda.rrt.soap.customserialisers.java_util_HashMap;

public class EventSerializer extends java_util_HashMap {

	public Object[] convertObjectToArrayOfValues(Object object) {

		// Get the values for the hash map state.
		Object[] hash_map_values = super.convertObjectToArrayOfValues(object);
		
		// Add the state for the 'type' field.
		return new Object[] { hash_map_values[0], hash_map_values[1], ((Event)object).getType() };
	}

	public void initializeObjectUsingArrayOfValues(Object object, Object[] values) {

		// Initialise the hash map state.
		super.initializeObjectUsingArrayOfValues(object, values);
		
		// Initialise the 'type' field.
		((Event)object).setType((String)values[2]);
	}
}
