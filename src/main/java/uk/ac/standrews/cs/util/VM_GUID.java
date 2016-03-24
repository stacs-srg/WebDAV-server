/*
 * Created on Jan 21, 2005 at 12:59:49 PM.
 */
package uk.ac.standrews.cs.util;

import java.rmi.dgc.VMID;

/**
 * Provides a unique ID for the current VM.
 *
 * @author graham
 */
public class VM_GUID {

    public static VMID id = new VMID();
    
    public static String getVMGUID() { return id.toString(); }
}
