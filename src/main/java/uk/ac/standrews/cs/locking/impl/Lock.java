/*
 * Created on Aug 11, 2005 at 1:40:50 PM.
 * @author al 
 */
package uk.ac.standrews.cs.locking.impl;

import uk.ac.standrews.cs.exceptions.LockException;
import uk.ac.standrews.cs.locking.interfaces.ILock;
import uk.ac.standrews.cs.locking.interfaces.IResourceLockInfo;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Lock implements ILock {

    private String lock_owner;
    private String token_prefix;
    
    protected Set lock_info_set;
    
    protected Lock(String lock_owner, String token_prefix) {

        this.lock_owner = lock_owner;
        this.token_prefix = token_prefix;
        
        lock_info_set = new HashSet();
    }

    public String getOwner() {
    	
        return lock_owner;
    }
    
    public String getTokenPrefix() {

		return token_prefix;
	}

	public Iterator resourceIterator() {
		
		return lock_info_set.iterator();
	}

	public void addResource(IResourceLockInfo resource_lock_info) throws LockException {
		
		// Don't need to check for conflict - should only be called by lock manager.

		lock_info_set.add(resource_lock_info);
	}

	public void removeResource(URI uri, String lock_token) throws LockException {

		Iterator resource_iterator = resourceIterator();
		while (resource_iterator.hasNext()) {
			
			IResourceLockInfo resource_lock_info = (IResourceLockInfo)resource_iterator.next();
			
			if (resource_lock_info.getResource().equals(uri)) {
				
				if (!resource_lock_info.getLockToken().equals(lock_token)) throw new LockException("invalid lock token");
				lock_info_set.remove(resource_lock_info);
				return;
			}
		}
		
		throw new LockException("URI not found");
	}
}
