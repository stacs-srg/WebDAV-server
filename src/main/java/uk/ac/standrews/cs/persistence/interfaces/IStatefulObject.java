/*
 * Created on May 30, 2005 at 2:12:56 PM.
 */
package uk.ac.standrews.cs.persistence.interfaces;


/**
 * A mutable object.
 * 
 * @author al
 */
public interface IStatefulObject extends IPersistentObject {

    /**
     * Updates the state of the object.
     * 
     * @param data the new state for the object
     */
    void update(IData data);

    /**
     * Append to data to this object.
     *
     * TODO - maybe have issue with big data (keeping it all in memory is expensive or just not possible)
     *
     * @param data
     */
    void append(IData data);
}
