package uk.ac.standrews.cs.locking.impl.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.storage.exceptions.LockException;
import uk.ac.stand.dcs.asa.storage.exceptions.LockUseException;
import uk.ac.stand.dcs.asa.storage.locking.impl.LockDepth;
import uk.ac.stand.dcs.asa.storage.locking.impl.LockManager;
import uk.ac.stand.dcs.asa.storage.locking.impl.LockScope;
import uk.ac.stand.dcs.asa.storage.locking.impl.LockType;
import uk.ac.stand.dcs.asa.storage.locking.interfaces.ILock;
import uk.ac.stand.dcs.asa.storage.locking.interfaces.ILockManager;
import uk.ac.stand.dcs.asa.storage.locking.interfaces.IResourceLockInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class LockManagerTest extends TestCase {

	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.LockManager.addResource(ILock, URI, LockScope, LockType, LockDepth)'
	 */
	public void testAddResource() {
		
		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");
		ILock lock2 = lock_manager.newLock("al", "lkajsfd");

		IResourceLockInfo rl1 = null;
		IResourceLockInfo rl2 = null;
		
		// Create a shared lock.
		
		try {
			rl1 = lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Create another shared lock on same resource.
		
		try {
			rl2 = lock_manager.addResourceToLock(lock2, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Try to obtain an exclusive lock on same resource - should fail.
		
		try {
			lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			fail();
		} catch (LockException e) { // Should give exception.
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Try to remove first lock with wrong lock token - should fail.

		try {
			lock_manager.removeResourceFromLock(lock1, rl1.getResource(), rl2.getLockToken());
			fail();
		} catch (LockException e) { // Should give exception.
		}
		
		// Remove first shared lock.
		
		try {
			lock_manager.removeResourceFromLock(lock1, rl1.getResource(), rl1.getLockToken());
		} catch (LockException e) {
			fail(e.getMessage());
		}
		
		// Remove second shared lock.
		
		try {
			lock_manager.removeResourceFromLock(lock2, rl2.getResource(), rl2.getLockToken());
		} catch (LockException e) {
			fail(e.getMessage());
		}
		
		// Create an exclusive lock.
		
		try {
			lock_manager.addResourceToLock(lock2, new URI("/a/b"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Try to obtain a shared lock on same resource - should fail.
		
		try {
			lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			fail();
		} catch (LockException e) { // Should give exception.
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
	}
	
	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.LockManager.addResource(ILock, URI, LockScope, LockType, LockDepth)'
	 */
	public void testAddResource2() {
		
		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");
		ILock lock2 = lock_manager.newLock("al", "lkajsfd");
			
		try {
			// Create an exclusive lock with depth zero.
			lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);

			// Obtain an exclusive lock on resource higher in hierarchy.
			lock_manager.addResourceToLock(lock2, new URI("/a"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);

			// Obtain an exclusive lock on resource lower in hierarchy.
			lock_manager.addResourceToLock(lock2, new URI("/a/b/c"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
	}
	
	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.LockManager.addResource(ILock, URI, LockScope, LockType, LockDepth)'
	 */
	public void testAddResource3() {
		
		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");
		ILock lock2 = lock_manager.newLock("al", "lkajsfd");
		
		// Create an exclusive lock with depth infinity.
		
		try {
			lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_INFINITY);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Obtain an exclusive lock depth zero on resource higher in hierarchy.
		
		try {
			lock_manager.addResourceToLock(lock2, new URI("/a"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Try to obtain an exclusive lock depth zero on resource lower in hierarchy - should fail.
		
		try {
			lock_manager.addResourceToLock(lock2, new URI("/a/b/c"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			fail();
		} catch (LockException e) {
			// Should fail.
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Try to obtain an exclusive lock depth infinity on resource higher in hierarchy - should fail.
		
		try {
			lock_manager.addResourceToLock(lock2, new URI("/a"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_INFINITY);
			fail();
		} catch (LockException e) {
			// Should fail.
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
	}

	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.LockManager.removeResourceFromMatchingLocks(URI, String)'
	 */
	public void testRemoveResourceFromMatchingLocks() {

		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");
		ILock lock2 = lock_manager.newLock("al", "lkajsfd");

		IResourceLockInfo rl1 = null;
		
		// Create a shared lock.
		
		try {
			rl1 = lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Create another shared lock on different resource.
		
		try {
			lock_manager.addResourceToLock(lock2, new URI("/a/b/c"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Remove first resource from matching locks.

		try {
			lock_manager.removeResourceFromMatchingLocks(new URI("/a/b"), rl1.getLockToken());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Should now be able to obtain exclusive lock.
		
		try {
			lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_EXCLUSIVE, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
	}

	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.LockManager.lockIterator()'
	 */
	public void testLockIterator() {

		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");
		ILock lock2 = lock_manager.newLock("al", "lkajsfd");
		
		try {
			// Create a shared lock.
			lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);

			// Create another shared lock on different resource.
			lock_manager.addResourceToLock(lock2, new URI("/a/b/c"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		Iterator iterator = lock_manager.lockIterator();
		
		// Check that the two original locks are returned.
		
		ILock first = (ILock) iterator.next();
		ILock second = (ILock) iterator.next();
		
		assertFalse(iterator.hasNext());
		assertNotSame(first, second);
		assertTrue(first == lock1 || first == lock2);
		assertTrue(second == lock1 || second == lock2);
	}

	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.LockManager.lockIterator(URI)'
	 */
	public void testLockIteratorURI() {

		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");
		ILock lock2 = lock_manager.newLock("al", "lkajsfd");
		
		// Create various locks.
		
		try {
			lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			lock_manager.addResourceToLock(lock2, new URI("/a/b/c"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			lock_manager.addResourceToLock(lock2, new URI("/e"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			
			Iterator iterator = lock_manager.lockIterator(new URI("/a/b"));
			
			// Check that only the first original lock is returned.
			
			ILock first = (ILock) iterator.next();
			
			assertFalse(iterator.hasNext());
			assertTrue(first == lock1);
			
			iterator = lock_manager.lockIterator(new URI("/e/f/g"));
			
			// Check that only the second original lock is returned.
			
			first = (ILock) iterator.next();
			
			assertFalse(iterator.hasNext());
			assertTrue(first == lock2);
			
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
	}

	public void testUriIterator() {
		
		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");
		ILock lock2 = lock_manager.newLock("al", "lkajsfd");
		
		try {
			Iterator iterator = lock_manager.uriIterator();
			assertFalse(iterator.hasNext());
			
			// Create a shared lock.
			lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			
			// Create another shared lock on different resource.
			lock_manager.addResourceToLock(lock2, new URI("/a/b/c"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
			
			iterator = lock_manager.uriIterator();
			
			// Check that the two original locks are returned.
			
			URI first = (URI) iterator.next();
			URI second = (URI) iterator.next();
			
			assertFalse(iterator.hasNext());
			assertNotSame(first, second);
			assertTrue(first.equals(new URI("/a/b")) || first.equals(new URI("/a/b/c")));
			assertTrue(second.equals(new URI("/a/b")) || second.equals(new URI("/a/b/c")));
			
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
	}

	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.LockManager.checkWhetherLockedWithOtherToken(URI, String)'
	 */
	public void testCheckWhetherLockedWithOtherToken() {

		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");
		ILock lock2 = lock_manager.newLock("al", "lkajsfd");

		IResourceLockInfo rl1 = null;
			
		try {
			// Create a shared lock.
			rl1 = lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);

			// Shouldn't be locked with any other token.
			lock_manager.checkWhetherLockedWithOtherToken(new URI("/a/b"), rl1.getLockToken());
			
			// Create another shared lock on same resource.
			lock_manager.addResourceToLock(lock2, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);

		} catch (LockException e) {
			fail(e.getMessage());
		} catch (LockUseException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Should now be locked with another token.
		
		try {
			lock_manager.checkWhetherLockedWithOtherToken(new URI("/a/b"), rl1.getLockToken());
			fail();
			
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (LockUseException e) {
	        // Should fail.
        }
	}

	/*
	 * Test method for 'uk.ac.stand.dcs.asa.storage.locking.impl.LockManager.checkWhetherLockedWithToken(URI, String)'
	 */
	public void testCheckWhetherLockedWithToken() {

		ILockManager lock_manager = new LockManager();
		
		ILock lock1 = lock_manager.newLock("graham", "lkajsfd");

		IResourceLockInfo rl1 = null;
		
		// Shouldn't be locked with given token.
		
		try {
			lock_manager.checkWhetherLockedWithToken(new URI("/a/b"), "fish");
			fail();
			
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (LockUseException e) {
	        // Should throw exception.
		}
		
		// Create a shared lock.
		
		try {
			rl1 = lock_manager.addResourceToLock(lock1, new URI("/a/b"), LockScope.LOCK_SCOPE_SHARED, LockType.LOCK_TYPE_WRITE, LockDepth.LOCK_DEPTH_ZERO);
		} catch (LockException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// Should be locked with given token.
		
		try {
			lock_manager.checkWhetherLockedWithToken(new URI("/a/b"), rl1.getLockToken());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (LockUseException e) {
	        fail();
		}
	}
}
