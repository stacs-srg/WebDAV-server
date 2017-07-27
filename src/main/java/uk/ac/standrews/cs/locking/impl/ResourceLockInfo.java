/*
 * Created on Aug 11, 2005 at 1:40:50 PM.
 * @author al 
 */
package uk.ac.standrews.cs.locking.impl;

import uk.ac.standrews.cs.guid.impl.RandomGUID;
import uk.ac.standrews.cs.locking.interfaces.IResourceLockInfo;

import java.net.URI;

public class ResourceLockInfo implements IResourceLockInfo {
	
    private URI resource;
    private LockDepth depth; 
    private LockScope scope;
    private LockType type;
    private String lockToken;
    
    public ResourceLockInfo(URI resource, LockDepth depth, LockScope scope, LockType type, String token_prefix) {
    	
        this.depth = depth;
        this.scope = scope;
        this.type = type;
        this.lockToken = token_prefix + (new RandomGUID());
        this.resource = resource.normalize();
    }

    public LockDepth getDepth() {
        return depth;
    }
    
    public LockScope getScope() {
        return scope;
    }

    public void setScope(LockScope scope) {
        this.scope = scope;
    }

    public String getLockToken() {
        return lockToken;
    }

    public LockType getType() {
        return type;
    }
    
    public String toString() {
    		return "<" + resource + "," + depth + "," + scope + "," + type + "," + lockToken + ">";
    }

	public URI getResource() {
		
		return resource;
	}

	public boolean conflictsWith(IResourceLockInfo other_resource_lock) {
		
		// No conflict if the scopes don't conflict.
		if (!scope.conflictsWith(other_resource_lock.getScope())) return false;
		
		if (depth == LockDepth.LOCK_DEPTH_ZERO) {
			
			if (other_resource_lock.getDepth() == LockDepth.LOCK_DEPTH_ZERO) {
				
				// Both locks depth zero so only conflict if URIs are equal
				return resource.equals(other_resource_lock.getResource());
			}
			else {
				
				// Other lock depth infinity so conflict if its URI contains this one.
				return other_resource_lock.includes(resource);
			}
		}
		else {
			if (other_resource_lock.getDepth() == LockDepth.LOCK_DEPTH_ZERO) {
				
				// This lock depth infinity, other zero, so conflict if this URI contains that one.
				return includes(other_resource_lock.getResource());
			}
			else {
				
				// Both locks depth infinity, so conflict if either URI contains the other.
				return other_resource_lock.includes(resource) || includes(other_resource_lock.getResource());
			}
		}
	}

	public boolean includes(URI resource) {
		
		String this_path = this.resource.getPath();             // Already normalized.
		String other_path = resource.normalize().getPath();
		
		return other_path.equals(this_path) || other_path.startsWith(this_path + "/");
	}
}
