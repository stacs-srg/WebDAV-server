/*
 * Created on 11-Aug-2005
 */
package uk.ac.standrews.cs.persistence.impl;

import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.persistence.interfaces.IPIDGenerator;
import uk.ac.standrews.cs.util.SHA1KeyFactory;

public class PIDGenerator implements IPIDGenerator {
    public IPID dataToPID(IData data) {
        IPID pid = (IPID) SHA1KeyFactory.generateKey(data.getState());
        return pid;
    }
}
