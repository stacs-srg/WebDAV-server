package uk.ac.standrews.cs.locking.impl;

import junit.framework.TestCase;
import org.junit.Test;
import uk.ac.standrews.cs.locking.impl.LockDepth;
import uk.ac.standrews.cs.locking.impl.LockScope;
import uk.ac.standrews.cs.locking.impl.LockType;
import uk.ac.standrews.cs.locking.impl.ResourceLockInfo;
import uk.ac.standrews.cs.locking.interfaces.IResourceLockInfo;

import java.net.URI;
import java.net.URISyntaxException;

public class ResourceLockInfoTest extends TestCase {
	
	private static String prefix1 = "prefix321";
	
	private IResourceLockInfo makeResourceLockInfo() throws URISyntaxException {
		
		return new ResourceLockInfo(new URI("test"), LockDepth.LOCK_DEPTH_ZERO, LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, prefix1);
	}

	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.ResourceLockInfo.getLockToken()'
	 */
	@Test
	public void testGetLockToken() {
		
		IResourceLockInfo r1 = null;
		try {
			r1 = makeResourceLockInfo();
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		String r1_token = r1.getLockToken();
		assertTrue(r1_token.startsWith(prefix1));               // Check that the token starts with the correct prefix.
		assertFalse(r1_token.length() == prefix1.length());     // Check that the token isn't just the prefix.
		
		IResourceLockInfo r2 = null;
		try {
			r2 = makeResourceLockInfo();
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}

		String r2_token = r2.getLockToken();
		assertTrue(r2_token.startsWith(prefix1));               // Check that the token starts with the correct prefix.
		assertFalse(r1_token.equals(r2_token));                 // Check that the two tokens are different.
	}
	
	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.ResourceLockInfo.conflictsWith(IResourceLockInfo)'
	 */
    @Test
	public void testConflictsWith() {
		
		try {
	        IResourceLockInfo r1 = new ResourceLockInfo(new URI("a/b"), LockDepth.LOCK_DEPTH_ZERO, LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, prefix1);
	        IResourceLockInfo r2 = new ResourceLockInfo(new URI("a/c"), LockDepth.LOCK_DEPTH_ZERO, LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, prefix1);
	        IResourceLockInfo r3 = new ResourceLockInfo(new URI("a/c"), LockDepth.LOCK_DEPTH_ZERO, LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, prefix1);

	        assertTrue(r1.conflictsWith(r1));     // Should conflict with itself.
	        assertFalse(r1.conflictsWith(r2));    // Shouldn't conflict with lock on different URI.
	        assertTrue(r2.conflictsWith(r3));     // Should conflict with different lock on same URI.
	        
	        IResourceLockInfo r4 = new ResourceLockInfo(new URI("a/c"), LockDepth.LOCK_DEPTH_ZERO, LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, prefix1);
	        IResourceLockInfo r5 = new ResourceLockInfo(new URI("a/c"), LockDepth.LOCK_DEPTH_ZERO, LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, prefix1);

	        assertFalse(r4.conflictsWith(r5));    // Shared locks shouldn't conflict.
	        assertTrue(r4.conflictsWith(r3));     // Shared lock should conflict with exclusive lock.
	        
	        IResourceLockInfo r6 = new ResourceLockInfo(new URI("a/b/c"), LockDepth.LOCK_DEPTH_ZERO, LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, prefix1);

	        assertFalse(r6.conflictsWith(r1));    // Shouldn't conflict since both depth zero.
	        assertFalse(r1.conflictsWith(r6));
	        
	        IResourceLockInfo r7 = new ResourceLockInfo(new URI("a/b"), LockDepth.LOCK_DEPTH_INFINITY, LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, prefix1);

	        assertTrue(r7.conflictsWith(r6));    // Should conflict since parent depth infinity.
	        assertTrue(r6.conflictsWith(r7));
	        assertFalse(r1.conflictsWith(r6));
	        
	        IResourceLockInfo r8 = new ResourceLockInfo(new URI("a/b/c"), LockDepth.LOCK_DEPTH_INFINITY, LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, prefix1);

	        assertFalse(r8.conflictsWith(r1));    // Shouldn't conflict since parent depth zero.
	        assertFalse(r1.conflictsWith(r8));
	        assertTrue(r8.conflictsWith(r7));     // Should conflict since both depth infinity.
	        assertTrue(r7.conflictsWith(r8));


        } catch (URISyntaxException e) {
	        fail(e.getMessage());
        }
	}
	
	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.ResourceLockInfo.includes(URI)'
	 */
    @Test
	public void testIncludes() {
		
		try {
			IResourceLockInfo r1 = new ResourceLockInfo(new URI("a/b"), LockDepth.LOCK_DEPTH_ZERO, LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, prefix1);

			assertTrue(r1.includes(new URI("a/b")));
			assertTrue(r1.includes(new URI("a/b/c")));
			assertFalse(r1.includes(new URI("a")));
			assertFalse(r1.includes(new URI("a/bc")));
			
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
	}
}
