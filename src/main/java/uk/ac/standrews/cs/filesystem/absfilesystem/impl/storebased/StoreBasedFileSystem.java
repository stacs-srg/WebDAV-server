/*
 * Created on Jun 16, 2005 at 8:59:49 AM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.impl.storebased;

import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.filesystem.absfilesystem.impl.general.AbstractFileSystem;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.exceptions.StoreIntegrityException;
import uk.ac.standrews.cs.store.impl.localfilebased.NameGUIDMap;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.store.interfaces.INameGUIDMap;
import uk.ac.standrews.cs.util.Attributes;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Error;

/**
 * File system implementation using a given IGUIDStore. Knows about stores but not how they're implemented.
 * 
 * @author al, graham
 */
public class StoreBasedFileSystem extends AbstractFileSystem implements IFileSystem {
	
	private IGUIDStore store;
	private INameGUIDMap store_root_map;
	
	/**
	 * Creates a file system operating over a given store.
	 * 
	 * @param store the store over which this file system will operate
	 * @throws StoreIntegrityException if the supplied root GUID is null
	 * @throws PersistenceException if a new map with the given root GUID could not be made persistent
	 */
	public StoreBasedFileSystem(IGUIDStore store, IGUID root_GUID) throws StoreIntegrityException, PersistenceException {
		
		super(root_GUID);
		
		this.store = store;
		
		if (root_GUID == null) throw new StoreIntegrityException("supplied root GUID is null");

		IAttributes attributes = new Attributes(FileSystemConstants.ISDIRECTORY + Attributes.EQUALS + "true" + Attributes.SEPARATOR);

		store_root_map = new NameGUIDMap(store, root_GUID);
		
		root_collection = new StoreBasedDirectory(store_root_map, store, attributes);
	}

	/**
	 * Creates a completely new file and a binding to it in a directory. Similar to 'creat' in Unix.
	 * 
	 * @param parent the directory in which the binding should be created
	 * @param name the name for the directory entry
	 * @param contentType the content type of the file
	 * @param data the contents of the file
	 * @return the new file
	 * 
	 * @throws BindingPresentException if a binding with the same name is already present in the directory
	 * @throws PersistenceException if the new file cannot be made persistent
	 */
	public synchronized IFile createNewFile(IDirectory parent, String name, String contentType, IData data) throws BindingPresentException, PersistenceException {

			check(parent, name, "file already exists", true);

		// Create the file.
		IFile new_file = new StoreBasedFile(store, data);

		// Add the binding to the directory.
		parent.addFile(name, new_file, contentType);

		return new_file;
	}

	/**
	 * Creates a completely new directory and a binding to it in a parent
	 * directory.
	 * 
	 * @param parent the directory in which the binding should be created
	 * @param name the name for the directory entry
	 * @return the new directory
	 * 
	 * @throws BindingPresentException if an entry with the same name is already present in the directory
	 * @throws PersistenceException if the new directory cannot be made persistent
	 */
	public synchronized IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException {
 
		check(parent, name, "directory already exists", true);

		INameGUIDMap map = new NameGUIDMap(store);
		IDirectory new_directory;
		
		new_directory = new StoreBasedDirectory(map, store, null);
		new_directory.setParent(parent);
		
		Diagnostic.trace( "Directory created with GUID =" + new_directory.getGUID() + " and pid =" + new_directory.getPID(), Diagnostic.FULL );

		Diagnostic.trace( "Persisting GUID Map", Diagnostic.FULL );		   
		new_directory.persist(); // write the directory to store - creates a pid and updates guid->pid map
		
		Diagnostic.trace( "Adding newly created Directory to parent", Diagnostic.FULL );
		parent.addDirectory(name, new_directory);
	   
		return new_directory;
	}

	/**
	 * Deletes a given binding to an object and creates a new binding to the same object in a different location.
	 * 
	 * @param sourceDirectory the directory containing the binding to the object
	 * @param sourceName the name of the directory entry currently referring to the object
	 * @param destinationDirectory the directory in which the new binding should be created
	 * @param destinationName the name for the new directory entry
	 * @param overwrite true if any existing entry at the destination location should be overwritten
	 * 
	 * @throws BindingAbsentException if there is no entry with the source name in the source directory
	 * @throws BindingPresentException if an entry with the destination name is already present in the destination directory and the overwrite flag is false
	 */
	public synchronized void moveObject(IDirectory sourceDirectory, String sourceName,
			IDirectory destinationDirectory, String destinationName, boolean overwrite) throws BindingAbsentException, BindingPresentException {

		IAttributedStatefulObject source_object = sourceDirectory.get(sourceName);
		
		checkSourceAndDestination(sourceName, destinationDirectory, destinationName, overwrite, source_object);

		// Delete the source object to be moved.
		deleteObject(sourceDirectory, sourceName);
		
		// If there is an existing destination object to be overwritten, delete it.
		// If the existing destination object is locked this will fail.
		if (destinationDirectory.contains(destinationName) && overwrite) deleteObject(destinationDirectory, destinationName);

		if (source_object instanceof IDirectory) {
				
			destinationDirectory.addDirectory(destinationName, (IDirectory)source_object);
		}
		else if (source_object instanceof IFile) {
			
			addExistingFileToDirectory(destinationDirectory, destinationName, (IFile)source_object);
		}
		else Error.hardError("unknown attributed stateful object encountered of type: " + source_object.getClass().getName());
	}
	
	/************************************ Private methods ************************************/
	
	private void addExistingFileToDirectory(IDirectory destinationParent, String destinationName, IFile file) throws BindingPresentException {
		
		IAttributes atts = file.getAttributes();
		String mime = atts.get(FileSystemConstants.CONTENT);
		
		destinationParent.addFile(destinationName, file, mime);
	}
	
	protected void addCopyOfFileToDirectory(IDirectory destinationParent, String destinationName, IFile file) throws PersistenceException, BindingPresentException  {
		
		IAttributes atts = file.getAttributes();
		String mime = atts.get(FileSystemConstants.CONTENT);
		
		IFile f = new StoreBasedFile(store, file.reify(), atts);
		
		destinationParent.addFile(destinationName, f, mime);
	}
}
