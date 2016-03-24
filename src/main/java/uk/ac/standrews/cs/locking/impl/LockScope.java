/**
 * Created on Nov 30, 2005 at 3:26:23 PM.
 */
package uk.ac.standrews.cs.locking.impl;

/**
 * 
 *
 * @author graham
 */
public class LockScope {

    /**
	 * Representation of exclusive lock scope, corresponding to use in WebDAV.
	 */
	public static LockScope LOCK_SCOPE_EXCLUSIVE = new LockScope(99);
	
	/**
	 * Representation of shared lock scope, corresponding to use in WebDAV.
	 */
	public static LockScope LOCK_SCOPE_SHARED = new LockScope(102);
	
	private int value;
	
	private LockScope(int value) {
		
		this.value = value;
	}

	public boolean equals(Object other_lock_scope) {
		
		return other_lock_scope instanceof LockScope &&
		       ((LockScope)other_lock_scope).value == value;
	}

	public boolean conflictsWith(LockScope other_lock_scope) {
		
		return equals(LOCK_SCOPE_EXCLUSIVE) || other_lock_scope.equals(LOCK_SCOPE_EXCLUSIVE);
	}
}
