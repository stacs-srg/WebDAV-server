/*
 * Created on Aug 11, 2005 at 1:34:48 PM.
 */
package uk.ac.standrews.cs.locking.impl;

import uk.ac.standrews.cs.exceptions.LockException;
import uk.ac.standrews.cs.exceptions.LockUseException;
import uk.ac.standrews.cs.locking.interfaces.ILock;
import uk.ac.standrews.cs.locking.interfaces.ILockManager;
import uk.ac.standrews.cs.locking.interfaces.IResourceLockInfo;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LockManager implements ILockManager {

	// TODO a lock-null resource (locked non-extant URI) should appear in its parent collection i.e. returned by PROPFIND which needs to be able to locate such resources.
	// TODO the whole lock management should probably be distributed.

	private Set<ILock> lock_set = new HashSet<ILock>();
	
	/**
	 * Creates a new lock.
	 * 
	 * @param owner the owner of the lock
	 * @param token_prefix a prefix to be prepended to each generated lock token
	 * @return the new lock
	 */
	public ILock newLock(String owner, String token_prefix) {

		ILock lock = new Lock(owner, token_prefix);
		lock_set.add(lock);
		return lock;
	}

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
	public IResourceLockInfo addResourceToLock(ILock lock, URI resource, LockScope scope, LockType type, LockDepth depth) throws LockException {
		
		IResourceLockInfo new_resource_lock_info = new ResourceLockInfo(resource, depth, scope, type, lock.getTokenPrefix());

		if (wouldConflictWithExistingLock(new_resource_lock_info)) {
			throw new LockException("lock conflict");
		}
		
		lock.addResource(new_resource_lock_info);
		
		return new_resource_lock_info;
	}	

	/**
	 * Removes a resource from a given lock, subject to checking of the given lock token.
	 * 
	 * @param lock the lock from which the resource should be removed
	 * @param resource the resource to be removed
	 * @param lock_token the lock token to be checked
	 * 
	 * @throws LockException if the lock is not managed by this lock manager, or the given lock token does not match the lock token generated when the resource was added to the lock
	 */
	public void removeResourceFromLock(ILock lock, URI resource, String lock_token) throws LockException {
		
		lock.removeResource(resource, lock_token);
	}

	/**
	 * Removes a resource from all locks containing the resource and matching the given lock token.
	 * 
	 * @param resource the resource to be removed
	 * @param lock_token the lock token to be checked
	 */
	public void removeResourceFromMatchingLocks(URI resource, String lock_token) {
		
		Iterator iterator = lockIterator(resource);
		
		while (iterator.hasNext()) {
			
			ILock lock = (ILock) iterator.next();
			
			try {
				removeResourceFromLock(lock, resource, lock_token);
			}
			catch (LockException e) {
				// Ignore - token doesn't match.
			}
		}
	}
	
	/**
	 * Discards a given lock.
	 * 
	 * @param lock the lock to be discarded
	 * 
	 * @throws LockException if the lock is not managed by this lock manager
	 */
	public void discardLock(ILock lock) throws LockException {

		if (!lock_set.remove(lock)) {
            throw new LockException("lock not present");
        }
	}

	/**
	 * Checks whether a given resource is locked with a given lock token.
	 * Throws an exception if the resource is not locked or if it is locked with a different lock token.
	 * 
	 * @param resource the resource to be checked
	 * @param lock_token the lock token to be checked
	 * 
	 * @throws LockUseException if the resource is not locked, or it is locked with a different lock token from the one presented
	 */
	public void checkWhetherLockedWithToken(URI resource, String lock_token) throws LockUseException {
		
		Iterator lock_iterator = lockIterator(resource);
		
        if (!lock_iterator.hasNext()) {           // ... throw exception if the object isn't locked at all.
            throw new LockUseException("resource " + resource + " is not locked, but the token"  + lock_token + "was presented");
        }

		checkLockTokenOverLocks(resource, lock_token, lock_iterator);
	}
	
	/**
	 * Checks whether a given resource is locked with a different lock token from the one presented.
	 * If so, throws an exception, otherwise does nothing.
	 * 
	 * @param resource the resource to be checked
	 * @param lock_token the lock token to be checked
	 * 
	 * @throws LockUseException if the resource is locked with a different lock token from the one presented
	 */
	public void checkWhetherLockedWithOtherToken(URI resource, String lock_token) throws LockUseException {
		
		Iterator lock_iterator = lockIterator(resource);

		checkLockTokenOverLocks(resource, lock_token, lock_iterator);
	}

	private void checkLockTokenOverLocks(URI resource, String lock_token, Iterator lock_iterator) throws LockUseException {
		while (lock_iterator.hasNext()) {

			ILock lock = (ILock)(lock_iterator.next());

			Iterator resource_iterator = lock.resourceIterator();

			while (resource_iterator.hasNext()) {

				IResourceLockInfo resource_lock_info = (IResourceLockInfo)(resource_iterator.next());

				if (!resource_lock_info.getLockToken().equals(lock_token)) {           // ... throw exception if the same lock token hasn't been presented.

					throw new LockUseException("resource " + resource + " is locked with token: " + resource_lock_info.getLockToken() + ", token presented: " + lock_token);
				}
			}
		}
	}
	
	/**
	 * Returns an iterator over all URIs currently locked by this lock manager.
	 * 
	 * @return an iterator over all URIs currently locked by this lock manager, typed as {@link URI}
	 */
	public Iterator uriIterator() {
		
		Set uri_set = new HashSet();
		
		Iterator lock_iterator = lockIterator();
		
		while (lock_iterator.hasNext()) {
			
			ILock lock = (ILock)(lock_iterator.next());
			
			Iterator resource_iterator = lock.resourceIterator();
			
			while (resource_iterator.hasNext()) {
				
				IResourceLockInfo resource_lock_info = (IResourceLockInfo)(resource_iterator.next());

		        uri_set.add(resource_lock_info.getResource());
			}
		}
		
		return uri_set.iterator();
	}
	
	/**
	 * Returns an iterator over all locks currently managed by this lock manager.
	 * 
	 * @return an iterator over all locks currently managed by this lock manager, typed as {@link ILock}
	 */
	public Iterator<ILock> lockIterator() {
		
		return lock_set.iterator();
	}

	/**
	 * Returns an iterator over all locks that contain a given resource, currently managed by this lock manager.
	 * 
	 * @param resource the resource to be matched
	 * @return an iterator over all locks that contain the given resource currently managed by this lock manager, typed as {@link ILock}
	 */
	public Iterator<ILock> lockIterator(URI resource) {
		
		return new LockUriFilterIterator(lock_set.iterator(), resource);
	}
	
	/**
	 * Tests whether a given resource is locked.
	 * 
	 * @param resource the resource to be matched
	 * @return true if at least one extant lock contains the given resource.
	 */
	public boolean locked(URI resource) {
		
		return lockIterator(resource).hasNext();
	}
	
	private boolean wouldConflictWithExistingLock(IResourceLockInfo new_resource_lock_info) {
		
		// For each existing lock...
		Iterator lock_iterator = lock_set.iterator();
		while (lock_iterator.hasNext()) {
		
			// For each locked resource...
			Iterator resource_iterator = ((ILock)lock_iterator.next()).resourceIterator();
			while (resource_iterator.hasNext()) {
			
				// Check for conflict with the required new lock.
				IResourceLockInfo resource_lock_info = (IResourceLockInfo)resource_iterator.next();
				
				if (new_resource_lock_info.conflictsWith(resource_lock_info)) return true;
			}
		}
		return false;
	}
	
	class LockUriFilterIterator implements Iterator {
		
		private Iterator<ILock> iterator;
		private URI resource;
		private ILock next_lock;
		
		protected LockUriFilterIterator(Iterator<ILock> iterator, URI resource) {
			
			this.iterator = iterator;
			this.resource = resource;
			next_lock = getNext();
		}

		public boolean hasNext() {

			return (next_lock != null);
		}

		public ILock next() {
            ILock lock = next_lock;
			next_lock = getNext();
			return lock;
		}

		public void remove() {

			ErrorHandling.hardError("unimplemented method");
		}
		
		private ILock getNext() {
			
			while (iterator.hasNext()) {
				ILock lock = iterator.next();
				Iterator<IResourceLockInfo> resource_iterator = lock.resourceIterator();
				
				while (resource_iterator.hasNext()) {
					IResourceLockInfo resource_lock_info = resource_iterator.next();
					if (resource_lock_info.includes(resource)) {
                        return lock;
                    }
				}
			}
			return null;
		}
	}
}
