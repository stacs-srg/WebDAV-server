/*
 * Created on May 22, 2005 at 2:35:52 PM.
 */
package uk.ac.standrews.cs.persistence.impl;

import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.INameAttributedPersistentObjectBinding;

/**
 * Implements a binding between a logical name and an IAttributedStatefulObject
 * A Directory contains a collection of these bindings.
 *
 * @author al
 */
public class NameAttributedPersistentObjectBinding implements INameAttributedPersistentObjectBinding {

    private String name;
    private IAttributedStatefulObject obj;
    
    /**
     * Creates a binding between a name and a GUID.
     * 
     * @param name the name
     * @param obj an IAttributedStatefulObject
     */
    public NameAttributedPersistentObjectBinding(String name, IAttributedStatefulObject obj) {
        this.name = name;
        this.obj = obj;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the IAttributedStatefulObject.
     * 
     * @return the IAttributedStatefulObject
     */
    public IAttributedStatefulObject getObject() {
        return obj;
    }

}
