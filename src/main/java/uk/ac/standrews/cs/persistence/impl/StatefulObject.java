/*
 * Created on May 21, 2005 at 1:09:52 PM.
 */
package uk.ac.standrews.cs.persistence.impl;

import org.apache.commons.lang3.ArrayUtils;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.persistence.interfaces.IStatefulObject;
import uk.ac.standrews.cs.store.impl.localfilebased.ByteData;
import uk.ac.standrews.cs.util.Error;

/**
 * Simple persistent object implementation.
 * 
 * @author al
 */
public abstract class StatefulObject extends PersistentBase implements IStatefulObject {
    
    protected IData state;
   
    /**
     * Creates a new instance without state.
     */
    public StatefulObject() {
        super();
        state = null;
    }
    
    public StatefulObject(IGUID guid) {
        super(guid);
        state = null;
    }
    
    /**
     * Creates a new instance with given state.
     * 
     * @param data the new state
     */
    public StatefulObject(IData data) {
    	super();
        state = data;
    }
    
    /**
     * Reinstantiates a previously stored object.
     * 
     * @param data the object's state
     * @param pid the object's PID
     * @param guid the object's GUID
     */
    public StatefulObject(IData data, IPID pid, IGUID guid) {
        super(guid, pid);
        state = data;
    }
    
    /**
     * Gets a representation of the object.
     * 
     * @return the object's state
     */
    public IData reify() {
        if (state == null) Error.error("Attempt to reify stateless object");
        return state;
    }

    /**
     * Initialises the object.
     * Note this is very dangerous and should only be used for re-instating reified state - al.
     * 
     * @param data the new state
     * @param pid the new PID
     * @param guid the new GUID
     *
     */
    public void initialise(IData data, IPID pid, IGUID guid) {
        this.state = data;
        this.pid = pid;
        this.guid = guid;
    }
    
    /**
     * Updates the object's transient state.
     * 
     * @param data the new state
     */
    public void update(IData data) {
    	this.state = data;
    }

    public void append(IData data) {
        // TODO - this solution is weak.
        // this relies on the data being always in-memory
        // one solution would be to persist in the update method, rather than keeping the state around
        System.out.println("state size was " + state.getSize() + " and appending data is " + data.getSize());
        state = new ByteData(ArrayUtils.addAll(state.getState(), data.getState()));
        System.out.println("state size is " + state.getSize());
    }

}
