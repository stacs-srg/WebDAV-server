/*
 * Created on Jun 24, 2005 at 2:11:03 PM.
 */
package uk.ac.standrews.cs.store.factories;

import uk.ac.standrews.cs.store.impl.localfilebased.FileStore;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.util.CommandLineArgs;

import java.io.File;
import java.util.Map;

/**
 * Factory providing methods to create new stores.
 * 
 * @author al, graham
 */
public class LocalFileBasedStoreFactory extends AbstractStoreFactory {
	
    private String store_directory_path = null;
    private String store_name = null;
    
    public LocalFileBasedStoreFactory() {
    	
    	// Allow store details not to be specified - will be set to defaults in makeStore().
    }

    public LocalFileBasedStoreFactory(String store_directory_path) {

        this.store_directory_path = store_directory_path;
    }

    public LocalFileBasedStoreFactory(String store_directory_path, String store_name) {

        this.store_directory_path = store_directory_path;
        this.store_name = store_name;
    }

    /**
     * Creates a new store, with optional store directory and store name extracted from command line arguments.
     * 
     * Usage: java WebDAVServer [-d<store root directory>] [-s<store name>]
     * 
     * @param args optional command line arguments specifying the store directory path and name.
     */
    public LocalFileBasedStoreFactory(String[] args) {
        
        Map args_map = CommandLineArgs.parseCommandLineArgs(args);
        
        store_directory_path = (String) args_map.get("-d");
        store_name =           (String) args_map.get("-s");                         // Default location and name.
    }
    
    /**
     * Creates a new store, located in the default store directory, with a fixed store name.
     * 
     * @return a new store
     */
    public IGUIDStore makeStore() {
        
        String path;
        if (store_directory_path == null) path = defaultStoreDirectory();
        else                              path = store_directory_path;
        
        String name;
        if (store_name == null) name = STORE_DIR_PATH_PREFIX;
        else                    name = store_name;

		return makeStore(path, name);
	}
    
    /**
     * Creates a new store, located in the given store directory, with a given store name.
     * 
     * @param store_directory_path the path of the directory in which the store is to be created
     * @param store_name the name of the root directory of the store
     * @return a new store
     */
    private IGUIDStore makeStore(String store_directory_path, String store_name) {

		String store_dir_path = store_directory_path + File.separator + store_name;
		return new FileStore(store_dir_path, store_dir_path + File.separator + GUID_TO_PID_DIR_NAME);
	}
}
