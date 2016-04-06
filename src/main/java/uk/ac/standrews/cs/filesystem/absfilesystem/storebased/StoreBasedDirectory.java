/*
 * Created on Jun 16, 2005 at 9:01:43 AM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.storebased;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.exceptions.StoreGetException;
import uk.ac.standrews.cs.store.exceptions.StorePutException;
import uk.ac.standrews.cs.store.general.NameGUIDBinding;
import uk.ac.standrews.cs.store.impl.localfilebased.NameGUIDMap;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.store.interfaces.INameGUIDMap;
import uk.ac.standrews.cs.util.Assert;
import uk.ac.standrews.cs.util.Attributes;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Error;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Directory implementation that knows about stores but not how they're implemented.
 * 
 * @author al, graham
 */
public class StoreBasedDirectory extends StoreBasedFileSystemObject implements IDirectory {

	private INameGUIDMap map;
	private IDirectory parent;

	// Obsolete?
	// private long mod_time;	  // date directory was last modified.
	
	//  Tracks active directories - in the distributed system this will track running Directories in the network
	private static HashMap directoryCache = new HashMap();	 // TODO change this when we go distributed

	public StoreBasedDirectory(INameGUIDMap map, IGUIDStore store, IAttributes atts) {
		super(store, map.getGUID(), atts);
		
		this.map = map;
		
		Assert.assertion(! directoryCache.containsKey( guid.toString()),"GUID already in dir cache" );
		directoryCache.put(guid.toString(), this);
		
		// Obsolete?
		// if (map.getPID() == null) mod_time = System.currentTimeMillis();			  // If no PID associated with map, treat modification time as now.
		// else					  mod_time = store.getPIDPutDate( map.getPID() );
	}

	public IDirectory getParent() {
		return parent;
	}

	public void setParent(IDirectory parent) {
		this.parent = parent;
	}

	public IAttributedStatefulObject get(String name) {
		try {
			Diagnostic.trace("Getting file with name: " + name, Diagnostic.RUN);
			IAttributes atts = map.getAttributes(name);
			IGUID guid = map.get(name);

			if (atts.contains(FileSystemConstants.ISDIRECTORY)) {
				
				if (directoryCache.containsKey(guid.toString())) {
					Diagnostic.trace("Found directory with name: " + name + " in cache", Diagnostic.RUN);
					return (IAttributedStatefulObject) directoryCache.get(guid.toString());
				} else {
                    return makeCollection(guid, this, store, atts);
                }

			} else if (atts.contains(FileSystemConstants.ISFILE)) {
                try {
                    return makeFile(guid, store, atts);
                } catch (StoreGetException e) {
                    Error.exceptionError("couldn't make file for " + name, e);
                    return null;
                }

            } else {
				Error.error("Encountered an unknown file type for " + name);
				return null;
			}
		}
		catch (BindingAbsentException e) {
			Diagnostic.trace("Getting name - name not found: " + name, Diagnostic.RUN);
			return null;
		}
		catch (PersistenceException e) {
			Error.exceptionError("couldn't make collection", e);
			return null;
		}
	}
	
	public boolean contains(String name) {

		return get(name) != null;
	}

	public void addFile(String name, IFile file, String contentType) throws BindingPresentException {
		
		Diagnostic.trace( "Adding file with name: " + name, Diagnostic.RUN );
		
		IAttributes atts = new Attributes( FileSystemConstants.ISFILE + Attributes.EQUALS + "true" + Attributes.SEPARATOR +
				FileSystemConstants.CONTENT + Attributes.EQUALS + contentType + Attributes.SEPARATOR );
		
		addObject(name, file, atts);
	}

	public void addDirectory(String name, IDirectory directory) throws BindingPresentException {
		
		Diagnostic.trace( "Adding directory with name: " + name, Diagnostic.RUN );
		
		IAttributes atts = new Attributes( FileSystemConstants.ISDIRECTORY + Attributes.EQUALS + "true" + Attributes.SEPARATOR );
		
		addObject(name, directory, atts);
		directory.setParent(this);
	}

	public void remove(String name) throws BindingAbsentException {
		
		Diagnostic.trace( "Removing entry with name: " + name, Diagnostic.RUN );  
		
		// If it's a directory being removed, remove link to this directory as parent.
		IAttributedStatefulObject entry = get(name);
		
		if (entry instanceof IDirectory) {
			IDirectory directory_being_removed = (IDirectory) entry;
			directory_being_removed.setParent(null);
		}

		map.delete(name);
		
		// Obsolete?
		// mod_time = System.currentTimeMillis();
	}
	
	public void persist() throws PersistenceException {
		
		map.persist(); // the map does all the work that is necesssary   
		pid = map.getPID();
		
		try {
            store.put(guid, pid);
        } catch (StorePutException e) {
            throw new PersistenceException("couldn't make directory map persistent", e);
        }
	}
	
	public IData reify() {
		return map.reify(); // the map does all the work that is necesssary 
	}
	
	public Iterator iterator() {
		return new CollectionIter(this);
	}
	

	// public long getCreationTime() {
	// return store.getGUIDPutDate( this.guid );
	// }
	//  
	public long getModificationTime() throws AccessFailureException {
		long mod_time = 0;
		IPID pid;
		try {
			pid = store.getLatestPID(guid);
		} catch (StoreGetException e) {
			throw new AccessFailureException(
					"could not get latest pid for this object");
		}
		try {
			mod_time = store.getPIDPutDate(pid);
		} catch (StoreGetException e) {
			throw new AccessFailureException(
					"could not get modification data for this object");
		}
		return mod_time;
	}
	
//	public URI getURI() {
//		
//		return UriUtil.childUri(parent.getURI(), name, false);
//	}

	/*********************** Private methods ***********************/
 
	private void addObject(String name, IAttributedStatefulObject object, IAttributes atts) throws BindingPresentException {
		
		// Add the name-GUID pair to the map.
		map.put(name, object.getGUID());

		// Set the attributes for the name.
		try {
			map.setAttributes(name, atts);
		} catch (BindingAbsentException e) {Error.hardExceptionError("Couldn't set attributes for newly added name", e);
        }
		
		// Record the modification time.
		// Obsolete?
		// mod_time = System.currentTimeMillis();
	}

	private IFile makeFile(IGUID guid, IGUIDStore store, IAttributes atts) throws StoreGetException {
		
		Diagnostic.trace("Making a file for GUID:" + guid, Diagnostic.RUN);

		IPID pid = store.getLatestPID(guid);
		IData data = store.get(pid);

		return new StoreBasedFile(store, data, guid, pid, atts);
	}
	
	private IDirectory makeCollection( IGUID guid, IDirectory parent, IGUIDStore store, IAttributes atts ) throws PersistenceException {
		
		Diagnostic.trace( "Making a collection for GUID:" + guid, Diagnostic.RUN );
		
		if (directoryCache.containsKey( guid.toString())) {
			return (IDirectory) directoryCache.get( guid.toString());
		}
		else {

			INameGUIDMap map = new NameGUIDMap(store, guid);

			StoreBasedDirectory new_collection = new StoreBasedDirectory( map, store, atts );
			new_collection.setParent(parent);
			return new_collection;
		}
	}

	/**
	 * This class acts as a type convertor hiding underlying iterator over GUIDs and gives
	 * an iterator over Name_AttributedPersistentObject_Binding.
	 * 
	 * @author al
	 */
	private class CollectionIter implements Iterator {

		Iterator baseIterator;
		IDirectory owner;
		
		public CollectionIter(IDirectory owner) {
			this.baseIterator = map.iterator();
			this.owner = owner;
		}

		public void remove() {
			baseIterator.remove(); 
		}

		public boolean hasNext() {
			return baseIterator.hasNext();
		}

		public Object next() {
			Object o = baseIterator.next();
			if( o instanceof NameGUIDBinding) {
				
				NameGUIDBinding binding = (NameGUIDBinding) o;
				String name = binding.getName();
				IGUID g = binding.getGUID();
				try {
					IAttributes atts = map.getAttributes(name);
					IDirectory d;
					if(atts.contains(FileSystemConstants.ISDIRECTORY)) {
						if( directoryCache.containsKey( g.toString() ) ) {
							Diagnostic.trace( "Found directory with name: " + name + " in cache", Diagnostic.RUN );
							d = (IDirectory) directoryCache.get( g.toString() );
						} else {
							d = makeCollection( g, owner, store, atts );
						}
						return new NameAttributedPersistentObjectBinding( name, d );
					}
					else if(atts.contains(FileSystemConstants.ISFILE)) {
						IFile f = makeFile( g, store, atts );
						return new NameAttributedPersistentObjectBinding( name, f );
					}
					else {
						Error.hardError( "encountered unknown file type" );
						// unreached
						return null;
					}
				} catch (Exception e) {
					Error.hardError( "cannot extract attributes" );
					return null;
				}
			} else {
				Error.hardError( "encountered an unexpected object in iterator" + o.getClass() );
				return null;
			}
		}
	}
}