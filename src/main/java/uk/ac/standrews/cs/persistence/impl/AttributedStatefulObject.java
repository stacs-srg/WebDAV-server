/*
 * Created on Jun 16, 2005 at 9:03:03 AM.
 */
package uk.ac.standrews.cs.persistence.impl;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.util.Error;

/**
 * Adds support for attributes to the state held by StatefulObject
 * 
 * @author al
 */
public abstract class AttributedStatefulObject extends StatefulObject implements IAttributedStatefulObject {

    IAttributes atts;
    
    /**
     * @param atts
     */
    public AttributedStatefulObject(IAttributes atts) {
        super();
        this.atts = atts;
    }
 
    public AttributedStatefulObject(IGUID guid, IAttributes atts) {
        super(guid);
        this.atts = atts;
    }
    
    public AttributedStatefulObject(IData data, IAttributes atts) {
        super(data);
        this.atts = atts;
    }
    
    public AttributedStatefulObject(IData data, IPID pid, IGUID guid, IAttributes atts ) {
        super( data, pid, guid );
        this.atts = atts;
    }

    public IAttributes getAttributes() {
        return atts;
    }

    public void setAttributes( IAttributes atts ) {
        this.atts = atts;
    }

    public abstract long getCreationTime() throws AccessFailureException;

    public abstract long getModificationTime() throws AccessFailureException;
}
