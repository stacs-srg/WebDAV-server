/*
 * Created on Aug 11, 2005 at 1:33:44 PM.
 */
package uk.ac.standrews.cs.locking.interfaces;


import uk.ac.standrews.cs.exceptions.LockException;
import uk.ac.standrews.cs.exceptions.LockUseException;
import uk.ac.standrews.cs.locking.impl.LockDepth;
import uk.ac.standrews.cs.locking.impl.LockScope;
import uk.ac.standrews.cs.locking.impl.LockType;

import java.net.URI;
import java.util.Iterator;

/**
 * Provides lock management functionality.
 * 
 * Each lock contains a number of resources (URIs), each locked with a particular scope and type, to a particular depth.
 *
 * @author graham
 */
public interface ILockManager {
    
	/**
	 * Creates a new lock.
	 * 
	 * @param owner the owner of the lock
	 * @param token_prefix a prefix to be prepended to each generated lock token
	 * @return the new lock
	 */
	ILock newLock(String owner, String token_prefix);

	/**
	 * Adds a resource to a given lock.
	 * 
	 * @param lock the lock to which the resource is to be added
	 * @param resource the resource to be added
	 * @param scope the scope of the lock
	 * @param type the type of the lock
	 * @param depth the depth of the lock
	 * @return information about the added resource, including a lock token for use in later removal
	 * 
	 * @throws LockException if the lock is not managed by this lock manager, or the request conflicts with an existing lock issued by this lock manager
	 */
	IResourceLockInfo addResourceToLock(ILock lock, URI resource, LockScope scope, LockType type, LockDepth depth) throws LockException;

	/**
	 * Removes a resource from a given lock, subject to checking of the given lock token.
	 * 
	 * @param lock the lock from which the resource should be removed
	 * @param resource the resource to be removed
	 * @param lock_token the lock token to be checked
	 * 
	 * @throws LockException if the lock is not managed by this lock manager, or the given lock token does not match the lock token generated when the resource was added to the lock
	 */
	void removeResourceFromLock(ILock lock, URI resource, String lock_token) throws LockException;
	
	/**
	 * Removes a resource from all locks containing the resource and matching the given lock token.
	 * 
	 * @param resource the resource to be removed
	 * @param lock_token the lock token to be checked
	 */
	void removeResourceFromMatchingLocks(URI resource, String lock_token);

	/**
	 * Discards a given lock.
	 * 
	 * @param lock the lock to be discarded
	 * 
	 * @throws LockException if the lock is not managed by this lock manager
	 */
	void discardLock(ILock lock) throws LockException;

	/**
	 * Checks whether a given resource is locked with a given lock token. Throws an exception if the resource is not locked or if it is locked with a different lock token.
	 * 
	 * @param resource the resource to be checked
	 * @param lock_token the lock token to be checked
	 * 
	 * @throws LockUseException if the resource is not locked, or it is locked with a different lock token from the one presented
	 */
	void checkWhetherLockedWithToken(URI resource, String lock_token) throws LockUseException;

	/**
	 * Checks whether a given resource is locked with a different lock token from the one presented. If so, throws an exception, otherwise does nothing.
	 * 
	 * @param resource the resource to be checked
	 * @param lock_token the lock token to be checked
	 * 
	 * @throws LockUseException if the resource is locked with a different lock token from the one presented
	 */
	void checkWhetherLockedWithOtherToken(URI resource, String lock_token) throws LockUseException;
	
	/**
	 * Returns an iterator over all URIs currently locked by this lock manager.
	 * 
	 * @return an iterator over all URIs currently locked by this lock manager, typed as {@link URI}
	 */
	Iterator uriIterator();
	
	/**
	 * Returns an iterator over all locks currently managed by this lock manager.
	 * 
	 * @return an iterator over all locks currently managed by this lock manager, typed as {@link ILock}
	 */
	Iterator lockIterator();

	/**
	 * Returns an iterator over all locks that contain a given resource, currently managed by this lock manager.
	 * 
	 * @param resource the resource to be matched
	 * @return an iterator over all locks that contain the given resource currently managed by this lock manager, typed as {@link ILock}
	 */
	Iterator lockIterator(URI resource);

	/**
	 * Tests whether a given resource is locked.
	 * 
	 * @param resource the resource to be matched
	 * @return true if at least one extant lock contains the given resource.
	 */
	boolean locked(URI resource);
}
