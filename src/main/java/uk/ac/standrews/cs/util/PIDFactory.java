/*
 * Created on 19-Aug-2005
 */
package uk.ac.standrews.cs.util;

import uk.ac.standrews.cs.interfaces.IPID;

public class PIDFactory {
    
    public static IPID generateRandomPID() {
    	
        return (KeyImpl)SHA1KeyFactory.generateRandomKey();
    }
}
