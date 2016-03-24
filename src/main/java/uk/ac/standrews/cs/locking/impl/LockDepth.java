/**
 * Created on Jan 6, 2006 at 2:07:23 PM.
 */
package uk.ac.standrews.cs.locking.impl;

/**
 * 
 *
 * @author graham
 */
public class LockDepth {

    /**
	 * Representation of zero lock depth, corresponding to use in WebDAV.
	 */
	public static LockDepth LOCK_DEPTH_ZERO = new LockDepth();
	
	/**
	 * Representation of infinite lock depth, corresponding to use in WebDAV.
	 */
	public static LockDepth LOCK_DEPTH_INFINITY = new LockDepth();
	
	private LockDepth() {
		// To prevent creation of other instances.
	}
}
