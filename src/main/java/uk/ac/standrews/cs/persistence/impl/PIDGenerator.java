/*
 * Created on 11-Aug-2005
 */
package uk.ac.standrews.cs.persistence.impl;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.impl.SHA1KeyFactory;
import uk.ac.standrews.cs.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.persistence.interfaces.IPIDGenerator;
import uk.ac.standrews.cs.util.Error;

public class PIDGenerator implements IPIDGenerator {
    public IPID dataToPID(IData data) {
        IPID pid = null;
        try {
            pid = (IPID) SHA1KeyFactory.generateKey(data.getState());
        } catch (GUIDGenerationException e) {
            Error.error("Unable to generate sha1 key");
        }
        return pid;
    }
}
