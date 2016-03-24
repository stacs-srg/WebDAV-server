/*
 * Created on May 25, 2005 at 12:51:45 PM.
 */
package uk.ac.standrews.cs.store.interfaces;


/**
 * Aggregation of the functionality of IStore and IGUID_PID_Map. An instance
 * can store multiple versions of objects.
 * 
 * @author al
 */
public interface IGUIDStore extends IStore, IGUIDPIDMap {

	// No additional methods.
}
