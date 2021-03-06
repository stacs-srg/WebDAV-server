/*
 * Created on May 30, 2005 at 1:28:34 PM.
 */
package uk.ac.standrews.cs.filesystem.factories;

import uk.ac.standrews.cs.filesystem.absfilesystem.storebased.StoreBasedFileSystem;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.fs.store.exceptions.StoreIntegrityException;
import uk.ac.standrews.cs.fs.store.factories.LocalFileBasedStoreFactory;
import uk.ac.standrews.cs.fs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.guid.IGUID;

/**
 * Factory providing methods to create a new file system using a given store.
 *  
 * @author al, graham
 */
public class StoreFileSystemFactory implements IFileSystemFactory {
	
	private IGUIDStore store;
	private IGUID root_GUID;
    
    /**
     * Creates a file system factory using the default store.
     */
    public StoreFileSystemFactory(IGUID root_GUID) {
    	
    	this(null, root_GUID);
    }
    
    /**
     * Creates a file system factory using the specified store.
     */
   public StoreFileSystemFactory(IGUIDStore store, IGUID root_GUID) {
	   
	   this.store = store;
	   this.root_GUID = root_GUID;
     }
   
   public IFileSystem makeFileSystem() throws FileSystemCreationException {
	   
	   IGUIDStore store = this.store;
	   if (store == null) store = new LocalFileBasedStoreFactory().makeStore();
	   
	   try {
		   return new StoreBasedFileSystem(store, root_GUID);
	   }
	   catch (StoreIntegrityException e) { throw new FileSystemCreationException(e.getMessage()); }
	   catch (PersistenceException e)    { throw new FileSystemCreationException(e.getMessage()); }
   }
}
