/*
 * Created on Jun 24, 2005 at 2:11:03 PM.
 */
package uk.ac.standrews.cs.store.factories;

import uk.ac.standrews.cs.store.impl.localfilebased.FileStore;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;

import java.io.File;

/**
 * Factory providing methods to create new stores.
 * 
 * @author stuart, graham
 * 
 * Al made us write it like this.
 */
public class ManagedLocalFileBasedStoreFactory extends AbstractStoreFactory {
	
	private static int store_count = 1;
    private String customPrefix;
    
    public ManagedLocalFileBasedStoreFactory(String customPrefix) {

        this.customPrefix = customPrefix;
    }

    /**
     * Creates a new store, located in the default store directory, with a store name ending in an incremented counter.
     * 
     * @return a store to be used for testing purposes
     */
    public static IGUIDStore makeTestStore() {

		String store_dir_path = defaultStoreDirectory() + File.separator + STORE_DIR_PATH_PREFIX + store_count++;
		return new FileStore(store_dir_path, store_dir_path + File.separator + GUID_TO_PID_DIR_NAME);
	}

    public IGUIDStore makeStore() {
        // This will create the store directory in the current working directory - can't think of anything more portable.
        String store_dir_path = defaultStoreDirectory() + File.separator + "ASA_Stores" + File.separator + customPrefix + File.separator + STORE_DIR_PATH_PREFIX;
        return new FileStore(store_dir_path, store_dir_path + File.separator + GUID_TO_PID_DIR_NAME);
    }
}
