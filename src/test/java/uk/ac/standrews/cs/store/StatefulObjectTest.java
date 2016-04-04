package uk.ac.standrews.cs.store;

import org.junit.Test;
import uk.ac.standrews.cs.filesystem.absfilesystem.storebased.StoreBasedFile;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.factories.LocalFileBasedStoreFactory;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;

import static org.junit.Assert.*;

/**
 * Test class for StatefulObject:
 * 
 * public class StatefulObject extends PersistentBase {
 *   public IData reify() ;
 *   public void initialise(IData data, PID pid, GUID guid);
 *   public void update(IData data);
 * }
 * 
 * @author graham
 */
public class StatefulObjectTest {
	
	private IData data1 = new StringData("quick brown fox");
	private IData data2 = new StringData("lazy dog");

    @Test
	public void testPersist() {
		
        IGUIDStore store = new LocalFileBasedStoreFactory(StoreConstants.STORE_DIRECTORY_PATH, StoreConstants.STORE_NAME).makeStore();

        try {
            StoreBasedFile file = new StoreBasedFile(store, data1);

            file.persist();
            
            IPID pid1 = file.getPID();
            IGUID guid1 = file.getGUID();
            
            assertEquals(store.get(pid1), data1);              // Once the file has been persisted, its data should be retrievable using its PID.
            
            file.persist();
            IPID pid2 = file.getPID();
            
            assertEquals(pid1, pid2);                          // Persisting it twice with the same data shouldn't change its PID.
            
            file.update(data2);
            
            IPID pid3 = file.getPID();
            
            assertEquals(pid2, pid3);                          // Changing its state without persisting it shouldn't change its PID.
            
            file.persist();
            IPID pid4 = file.getPID();
            
            assertNotSame(pid3, pid4);                         // Persisting it with updated state should change its PID.
            assertEquals(store.get(pid4), data2);              // The new data should be retrievable using the new PID.
            
            IGUID guid2 = file.getGUID();
            
            assertEquals(guid1, guid2);                        // Updating the file shouldn't change its GUID.
            
            IPID latest_pid = store.getLatestPID(guid2);
            
            assertEquals(pid4, latest_pid);                    // The store's idea of the latest PID should match the file's.

        } catch (Exception e) { fail(e.getMessage()); }
	}

    @Test
	public void testReify() {

		try {
            IGUIDStore store = new LocalFileBasedStoreFactory(StoreConstants.STORE_DIRECTORY_PATH, StoreConstants.STORE_NAME).makeStore();
	        StoreBasedFile file = new StoreBasedFile(store, data1);
	
	        IData data = file.reify();
	        
	        assertEquals(data1, data);                             // Reifying the file should return the original data.
	    } catch (Exception e) { fail(e.getMessage()); }
	}

    @Test
	public void testUpdate() {
		
		try {
            IGUIDStore store = new LocalFileBasedStoreFactory(StoreConstants.STORE_DIRECTORY_PATH, StoreConstants.STORE_NAME).makeStore();
	        StoreBasedFile file = new StoreBasedFile(store, data1);
	        file.update(data2);
	
	        IData data = file.reify();
	        
	        assertEquals(data, data2);                             // Reifying the updated file should return the new data.
	    } catch (Exception e) { fail(e.getMessage()); }

	}
}
