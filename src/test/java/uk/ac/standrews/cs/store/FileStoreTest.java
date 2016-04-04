/*
 * Created on May 22, 2005 at 4:19:05 PM.
 */
package uk.ac.standrews.cs.store;

import org.junit.Test;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.exceptions.StoreGetException;
import uk.ac.standrews.cs.store.exceptions.StorePutException;
import uk.ac.standrews.cs.store.factories.LocalFileBasedStoreFactory;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.util.GUIDFactory;
import uk.ac.standrews.cs.util.PIDFactory;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Test class for FileStore.
 * 
 * @author al, graham
 */
public class FileStoreTest {

    /**
     * Tests IStore functionality:
     * 
     *   public interface IStore {
     *     public IData get( PID pid );
     *     public PID put( IData data ) throws Exception;
     *   }
     */
	@Test
    public void testIStore() {
    	
        IGUIDStore store = new LocalFileBasedStoreFactory(StoreConstants.STORE_DIRECTORY_PATH, StoreConstants.STORE_NAME).makeStore();

        try {
        	IData data1 = new StringData( "quick brown fox" );
        	IData data2 = new StringData( "lazy dog" );
        	IData data3 = new StringData( "lazy dog" );
        	
        	IPID pid1 = store.put(data1);
        	IPID pid2 = store.put(data2);
        	IPID pid3 = store.put(data3);
        	
        	assertNotSame(pid1, pid2);                         // Storing different data items should result in different PIDs.
        	assertEquals(pid2, pid3);                          // Storing the same data twice should result in the same PID.
        	
        	IData data4 = store.get(pid1);
        	IData data5 = store.get(pid2);
        	IData data6 = store.get(pid3);
        	
        	assertEquals(data1, data4);                        // Retrieval from the store should result in the original data.
        	assertEquals(data2, data5);
        	assertEquals(data3, data6);
        	
        	IPID pid4 = PIDFactory.generateRandomPID();
        	IData data7 = store.get(pid4);
        	
        	assertNull(data7);                                 // Retrieval using a synthesised PID should return null.
        }
        catch (Exception e) { e.printStackTrace(); fail(); }
    }

    /**
     * Tests IGUID_PID_Map functionality:
     * 
     *   public interface IGUID_PID_Map {
     *     public PID getLatestPID( GUID guid );
     *     public Iterator getAllPIDs( GUID guid );
     *     public void put( GUID guid, PID pid );
     *     public Iterator getAllBindings();
     *   }
     */
    @Test
    public void testIGUID_PID_Map() {

        IGUIDStore store = new LocalFileBasedStoreFactory(StoreConstants.STORE_DIRECTORY_PATH, StoreConstants.STORE_NAME).makeStore();

    	IPID pid1 = PIDFactory.generateRandomPID();
    	IPID pid2 = PIDFactory.generateRandomPID();
    	IPID pid3 = PIDFactory.generateRandomPID();
    	
    	assertNotSame(pid1, pid2);                             // Two randomly generated PIDs should be different.
    	
     	IGUID guid1 = GUIDFactory.generateRandomGUID();
    	IGUID guid2 = GUIDFactory.generateRandomGUID();
    	
      	try {
            store.put(guid1, pid1);
        	store.put(guid1, pid2);
        	store.put(guid2, pid3);
        	
        	assertEquals(pid2, store.getLatestPID(guid1));         // The latest PID for GUID guid1 should be pid2.
        	assertEquals(pid3, store.getLatestPID(guid2));         // The latest PID for GUID guid2 should be pid3.
        	
        	store.put(guid1, pid1);
        	store.put(guid2, pid1);
        	
        	assertEquals(pid1, store.getLatestPID(guid1));         // The latest PID for GUID guid1 should now be pid1.
        	assertEquals(pid1, store.getLatestPID(guid2));         // The latest PID for GUID guid2 should now be pid1.
        	
            // Check that the temporal sequence of PIDs for guid1 is returned correctly.
        	
            IPID[] pid_sequence = new IPID[] {pid1, pid2, pid1};
            Iterator pid_iterator = store.getAllPIDs(guid1);
            int seq_no = 0;
            while (pid_iterator.hasNext()) {
                IPID p = (IPID) pid_iterator.next();
                assertEquals(p, pid_sequence[seq_no++]);           // The next PID in the iteration should be the corresponding one in the defined sequence.
            }
        } catch (StorePutException e) {
            e.printStackTrace(); fail();
        } catch (StoreGetException e) {
            e.printStackTrace(); fail();
        }
        
    }
}
