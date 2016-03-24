/**
 * Created on Nov 30, 2005 at 3:26:23 PM.
 */
package uk.ac.standrews.cs.locking.impl;

/**
 * 
 *
 * @author graham
 */
public class LockType {

	/**
	 * Representation of exclusive lock scope, corresponding to use in WebDAV.
	 */
	public static LockType LOCK_TYPE_WRITE = new LockType(107);
	
	private int value;
	
	private LockType(int value) {
		
		this.value = value;
	}

	public boolean equals(Object other_lock_type) {
		
		return other_lock_type instanceof LockType &&
		       ((LockType)other_lock_type).value == value;
	}
}
