/*
 * Created on Nov 15, 2005 at 3:07:49 APM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.impl.general;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.exceptions.UpdateException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.persistence.interfaces.INameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.UriUtil;

import java.net.URI;
import java.util.Iterator;

/**
 * Contains code generic to various file system implementations.
 * 
 * @author al, graham
 */
public abstract class AbstractFileSystem {
	
	protected IGUID root_GUID;
	protected IDirectory root_collection;

	public AbstractFileSystem(IGUID root_GUID) {

		this.root_GUID = root_GUID;
	}
	
	protected abstract void addCopyOfFileToDirectory(IDirectory destination_parent, String destination_name, IFile file) throws BindingPresentException, PersistenceException;
	public abstract IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException;

	public void check(IDirectory parent, String name, String error_message, boolean absence) throws BindingPresentException {

		// If 'absence' check that the name is absent, otherwise check that the name is present.
		if (!(parent.contains(name) ^ absence)) {
			
			String msg = error_message + ": " + name;
			Error.error(msg);							   // log it and
			throw new BindingPresentException(msg);		 // propagate to caller
		}
	}
	
	/**
	 * Updates an existing file.
	 * 
	 * @param directory the directory containing an entry for the file
	 * @param name the name for the directory entry
	 * @param content_type the new content type of the file
	 * @param data the new contents of the file
	 * 
	 * @throws BindingAbsentException if there is no directory entry with the given name
	 * @throws UpdateException if the directory entry with the given name is not a file
	 * @throws PersistenceException if the updated file cannot be made persistent
	 */
	public synchronized void updateFile(IDirectory directory, String name, String content_type, IData data) throws BindingAbsentException, UpdateException, PersistenceException {
		
		IAttributedStatefulObject source_file = directory.get(name);
		
		// Check that the file exists.
		if (source_file == null) {
			
			String msg = "attempt to update non-existent file: " + name;
			Error.error(msg);						  // log it and
			throw new BindingAbsentException(msg);	 // propagate to caller 
		}
		
		// Check that the file is really a file.
		if (! (source_file instanceof IFile)) throw new UpdateException("attempt to update non-file object: " + name);
		
		// Update file with new IData.
		source_file.update(data);
		
		// Make new data persistent.
		source_file.persist();
		
		// TODO Does the content type need to be updated?
		// If the content type is determined by the file extension then the content type can't change on an update, since the name is the same.
	}

	public synchronized void appendToFile(IDirectory directory, String name, String content_type, IData data) throws BindingAbsentException, PersistenceException{

        IAttributedStatefulObject source_file = directory.get(name);

        // Check that the file exists.
        if (source_file == null) {

            String msg = "attempt to update non-existent file: " + name;
            Error.error(msg);						  // log it and
            throw new BindingAbsentException(msg);	 // propagate to caller
        }

        // Check that the file is really a file.
        if (! (source_file instanceof IFile)) {
            throw new NotImplementedException();
            // TODO - throw appendException
            //throw new UpdateException("attempt to update non-file object: " + name);
        }

        // Append file with new IData.
        source_file.append(data);

        // Make new data persistent.
        source_file.persist();

        // TODO Does the content type need to be updated?
        // If the content type is determined by the file extension then the content type can't change on an update, since the name is the same.
	}

	/**
	 * Deletes a binding from a given directory.
	 * 
	 * @param parent the directory from which the binding should be deleted
	 * @param name the name of the entry to be deleted
	 * 
	 * @throws BindingAbsentException if there is no directory entry with the given name
	 */
	public void deleteObject(IDirectory parent, String name) throws BindingAbsentException {
		
		try {
			check(parent, name, "attempt to delete non-existent object", false);
		} catch (BindingPresentException e) {
			Error.hardExceptionError("shouldn't get BindingPresentException on deletion", e);
		}

		parent.remove(name);
	}
	
	/**
	 * Creates a complete copy of an object and creates a binding to the copy in
	 * a different location.
	 * 
	 * @param source_directory the directory containing the binding to the existing object
	 * @param source_name the name of the directory entry referring to the existing object
	 * @param destination_directory the directory in which the binding to the copied object should be created
	 * @param destination_name the name for the directory entry for the copied object
	 * @param overwrite true if any existing entry at the destination location should be overwritten
	 * 
	 * @throws BindingAbsentException if there is no entry with the source name in the source directory
	 * @throws BindingPresentException if an entry with the destination name is already present in the destination directory and the overwrite flag is false
	 * @throws PersistenceException if the copied object cannot be made persistent
	 */
	public synchronized void copyObject(IDirectory source_directory, String source_name,
			IDirectory destination_directory, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException, PersistenceException {

		IAttributedStatefulObject source_object = source_directory.get(source_name);
		
		checkSourceAndDestination(source_name, destination_directory, destination_name, overwrite, source_object);
		
		// If there is an existing destination object to be overwritten, delete it.
		// If the existing destination object is locked this will fail.
		if (destination_directory.contains(destination_name) && overwrite) deleteObject(destination_directory, destination_name);

		if (source_object instanceof IDirectory) {
		
			// need to create a new directory and copy contents
		
			IDirectory newDir = createNewDirectory(destination_directory, destination_name); // create the collection	  
			
			IDirectory oldDir = (IDirectory) source_object;
			Iterator iter = oldDir.iterator();  // of IName_AttributedPersistentObject_Binding
			
			// iterate over the contents of the directory and copy..
			while (iter.hasNext()) {
			
				INameAttributedPersistentObjectBinding binding = (INameAttributedPersistentObjectBinding) iter.next();
				// this is recursion - may need to change to purely iterative solution.
				copyObject(oldDir, binding.getName(), newDir, binding.getName(), false);
			}
		}
		else if (source_object instanceof IFile) {
		
			addCopyOfFileToDirectory(destination_directory, destination_name, (IFile)source_object);
		}
		else Error.hardError("unknown attributed stateful object encountered of type: " + source_object.getClass().getName());
	 }
	
	/**
	 * Returns the root GUID of the file system.
	 * 
	 * @return the root GUID
	 */
	public IGUID getRootId() {
		return root_GUID;
	}

	/**
	 * Returns the root directory of the file system.
	 * 
	 * @return the root directory
	 */
	public IDirectory getRootDirectory() {
		return root_collection;
	}

	/**
	 * Returns the file or directory associated with a given file path expression.
	 * 
	 * @param uri a path expression to a file or directory
	 * @return the file or directory associated with that path, or null if the given path expression does not resolve to a file or directory
	 */
	public IAttributedStatefulObject resolveObject(URI uri) {
		
		Iterator iterator = UriUtil.pathElementIterator(uri);
		IDirectory parent = getRootDirectory();
		
		IAttributedStatefulObject object = parent;
		
		while (iterator.hasNext()) {
		
			String name = (String) iterator.next();
			
			object = parent.get(name);
			
			if (object == null) return null;  // No object with the current name.

			try {
				if (iterator.hasNext()) parent = (IDirectory) object;
			}
			catch (ClassCastException e) { return null; }   // Current object isn't a directory, and we haven't reached the end of the path, so invalid path.
		}

		return object;
	}
	
	protected void checkSourceAndDestination(String source_name, IDirectory destination_parent, String destination_name, boolean overwrite, IAttributedStatefulObject source_object) throws BindingAbsentException, BindingPresentException {
		
		// Error if the source does not exist.
		if (source_object == null) {
		
			String msg = "attempt to access non-existent object: " + source_name;
			Error.error(msg);						// log it
			throw new BindingAbsentException(msg);   // propagate to caller		   
		}
		
	// Error if the destination already exists and we've not to overwrite.
		if (! overwrite && destination_parent.contains(destination_name))  {
		
			String msg = "destination exists but overwrite set to false for: " + destination_name;
			Error.error(msg);						// log it
			throw new BindingPresentException(msg);  // propagate to caller
		}
	}
}
