/*
 * Created on Aug 11, 2005 at 1:37:20 PM.
 */
package uk.ac.standrews.cs.locking.interfaces;

import uk.ac.standrews.cs.locking.impl.LockDepth;
import uk.ac.standrews.cs.locking.impl.LockScope;
import uk.ac.standrews.cs.locking.impl.LockType;

import java.net.URI;

/**
 * Abstract lock interface.
 *
 * @author al, graham
 */
public interface IResourceLockInfo {
	
    /**
     * Returns the URI of the locked entity.
     * 
     * @return the URI of the locked entity
     */
	URI getResource();
    
    /**
     * Returns the scope of the lock.
     * 
     * @return the scope of the lock, either {@link LockScope#LOCK_SCOPE_EXCLUSIVE} or {@link LockScope#LOCK_SCOPE_SHARED}
     */
    LockScope getScope();

    /**
     * Returns the depth covered by the lock.
     * 
     * @return the depth of the lock.
     */
    LockDepth getDepth();
    
    /**
     * Returns the type of the lock.
     * 
     * @return the type of the lock, currentlyl only {@link LockType#LOCK_TYPE_WRITE}
     */
    LockType getType();
    
    /**
     * Returns the lock token.
     * 
     * @return the lock token
     */
    String getLockToken();
    
	boolean conflictsWith(IResourceLockInfo other_resource_lock);
	
	boolean includes(URI resource);
}
