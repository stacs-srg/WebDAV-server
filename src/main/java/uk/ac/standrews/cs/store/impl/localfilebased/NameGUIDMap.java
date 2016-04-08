/*
 * Created on May 20, 2005 at 5:03:55 PM.
 */
package uk.ac.standrews.cs.store.impl.localfilebased;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.impl.KeyImpl;
import uk.ac.standrews.cs.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.exceptions.StoreGetException;
import uk.ac.standrews.cs.store.exceptions.StorePutException;
import uk.ac.standrews.cs.store.general.NameGUIDBinding;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.store.interfaces.INameGUIDMap;
import uk.ac.standrews.cs.util.*;
import uk.ac.standrews.cs.util.Error;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Name to GUID map (directory) using Java Properties file as underlying implementation.
 *
 * @author al, graham
 */
public class NameGUIDMap extends Properties implements INameGUIDMap {

    //******************************** Local State ********************************
  
    private static final String SEPARATOR = "|";	// used to separate GUIDs and attributes
    private static final String SPACE = " ";
    
    private IGUIDStore backing_store;
    protected IGUID guid;
    private IPID pid;
    
    //******************************** Constructor Methods ********************************
    
    /**
     * Creates a new map using a given backing store.
     * 
     * @param store the backing store
     */
    public NameGUIDMap(IGUIDStore store) {
        super();
        backing_store = store;

        guid = GUIDFactory.generateRandomGUID();
        pid = null;
    }
    
    /**
     * Reinstantiates a previously stored map from a given backing store, or creates a new map with the given GUID if necessary.
     * 
     * @param store the backing store
     * @param guid the GUID for the map
     * @throws PersistenceException if a new map with the given GUID cannot be created
     */
    public NameGUIDMap(IGUIDStore store, IGUID guid) throws PersistenceException {
        super();
        backing_store = store;
        
        try {
            pid = store.getLatestPID(guid);
        } catch (StoreGetException e) {
            //TODO may be a transient error - could try again
            throw new PersistenceException("could not retrieve latest pid for specified GUID");
        }
        
        if (pid == null) {
        	// The store doesn't currently contain data for a map with the given GUID.
        	// Record the given GUID and make the new map persistent.
        	this.guid = guid;
        	persist();
        }
        else {
        	// The store already contains a persistent map with the given GUID.
        	// Reinstantiate the map from the store.
	        IData data;
            try {
                data = store.get(pid);
            } catch (StoreGetException e) {
                //TODO may be a transient error - could try again
                throw new PersistenceException("could not retrieve data for latest PID of specified GUID");
            }
	        initialise(data, pid, guid);
        }
    }
    
     // ******************************** IName_GUID_Map Methods ********************************

    public IAttributes getAttributes(String name) throws BindingAbsentException {

    	if (! containsKey(name)) throw new BindingAbsentException( "name: " + name + " doesn't exist in directory");
        
    	String result = (String) super.get(name);
        String attString = extractAttributes(result);
        return new Attributes( attString );
    }

    public void setAttributes(String name, IAttributes atts) throws BindingAbsentException {
    	
        if (! containsKey(name)) throw new BindingAbsentException( "name: " + name + " doesn't exist in directory");

        IGUID guid = get(name);
        super.put(name, guid.toString() + SEPARATOR + atts );
        synchroniseState();
    }
    
    /**
     * Looks up a given name.
     * 
     * @param name the name to be looked up
     * @return the GUID associated with the name, or null if it is not found
     */
    public synchronized IGUID get(String name) {

        String result = (String) super.get(name);

        if (result == null) return null;
        else                return extractGUID(result);
    }

    /**
     * Adds a new name to GUID binding.
     *
     * @param name the name to be added
     * @param guid the GUID to be added
     * @throws BindingPresentException if a binding with the given name is already present
     */
    public synchronized void put(String name, IGUID guid) throws BindingPresentException {

        if (containsKey(name)) throw new BindingPresentException( "name: " + name + " already exists in directory");

        Diagnostic.trace( "putting entry with name = " + name + " guid = " + guid.toString(), Diagnostic.RUN );

        super.put(name, guid.toString() + SEPARATOR + SPACE );
        synchroniseState();
    }

    /**
     * Deletes a name to GUID binding.
     *
     * @param name the name to be deleted
     * @throws BindingAbsentException if no binding with the given name is present
     */
    public synchronized void delete(String name) throws BindingAbsentException {

        if (!containsKey(name)) throw new BindingAbsentException( "name: " + name + " does not exist in directory");

    	super.remove(name);
        synchroniseState();
    }

    /**
     * Renames a binding.
     *
     * @param old_name the old name
     * @param new_name the new name
     * @throws BindingAbsentException if no binding with the old name is present
     * @throws BindingPresentException if a binding with the new name is already present
     */
    public synchronized void rename(String old_name, String new_name) throws BindingAbsentException, BindingPresentException {
    	
        if (!containsKey(old_name)) throw new BindingAbsentException( "old name: " + old_name + " doesn't exist in directory");
        if (containsKey(new_name))  throw new BindingPresentException( "new name: " + new_name + " already exists in directory");
        
        String guid_string = (String) super.get(old_name);
        
        super.remove(old_name);
        super.put(new_name, guid_string);
        
        synchroniseState();
    }

    /**
     * Gets all the name-GUID bindings.
     * 
     * @return an iterator over all the bindings, each typed as IName_GUID_Binding
     */
    public synchronized Iterator iterator() {
    	
        return new DirectoryIterator(keys());
    }
    
    private class DirectoryIterator implements Iterator {

        Enumeration enumeration;
        String currentKey = null;

        public DirectoryIterator( Enumeration enumeration ) {
            this.enumeration = enumeration;
        }

        public boolean hasNext() {
            return enumeration.hasMoreElements();
        }

        public Object next() {
            String currentKey = (String) enumeration.nextElement();
            IGUID value = get(currentKey);
            return new NameGUIDBinding(currentKey,value);
        }

        public void remove() {
            if( currentKey == null ) {
                Error.error( "Attempt to remove a non-element from a Directory (remove called before next)");
                return;
            }
            try {
                delete(currentKey);
            } catch (Exception e) {
                Error.exceptionError("Error removing attempting to remove element: " + currentKey, e);
            }
        }
    }

    //******************************** IPersistentObject Methods ********************************
    
    /**
     * Gets a representation of the object.
     * 
     * @return the object's state
     */
    public IData reify() {
    	
    	ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
    	
    	try {
    		// Convert the Properties state to a byte array.
    		
            store( output_stream, "" );                                // Empty header string.
           	return new ByteData( output_stream.toByteArray() );
    	}
    	catch (IOException e) {
    	    Error.exceptionError( "Cannot stream directory state into output stream", e);
    	    return null;
    	}
     }

    /**
     * Initialises the object.
     * 
     * @param data the new state
     * @param pid the new PID
     * @param guid the new GUID
     */
    public void initialise(IData data, IPID pid, IGUID guid) {
    	
        this.pid = pid;
        this.guid = guid;
        
//        IPersistentObject persistent_object = new StatefulObject(data, pid, guid);
//        
//        if (persistent_object == null) Error.hardError( "Cannot obtain directory from store" );

        try {
//            IData state = persistent_object.reify();
//            load(state.getInputStream());
        	
        	load(data.getInputStream());
        }
        catch (IOException e) { Error.exceptionError("Cannot load directory state: " + pid, e); }        
    }

    /**
     * Records the object's current state.
     * 
     * @throws PersistenceException if the object's state could not be recorded
     */
    public void persist() throws PersistenceException {
    	
        Diagnostic.trace( "store.put on reified state of Name_GUID map", Diagnostic.RUN );
        
        try {
			pid = backing_store.put(reify());   // put name_guid map in store
	        backing_store.put(guid, pid);		// update the guid->pid map

		}
        catch (StorePutException e) { throw new PersistenceException(e.getMessage()); }
        
        Diagnostic.trace( "Name_GUID map written to pid=" + pid + " guid=" + guid, Diagnostic.RUN );
    }

    /**
     * Gets the PID referring to the object's most recently recorded persistent state.
     * 
     * @return the PID for the object's most recent persistent state
     */
    public IPID getPID() {
    	
        return pid;
    }

    /**
     * Gets the GUID of the object.
     * 
     * @return the GUID of the object
     */
    public IGUID getGUID() {
    	
        return guid;
    }
    
    //******************************** Helper Methods ********************************
    
    /**
     * Write persistent state back to the store.
     */
    private void synchroniseState() {
    	
        try {
            Diagnostic.trace( "persisting state of Name_GUID map", Diagnostic.RUN );
            persist();
        }
        catch (Exception e) {
            Error.exceptionError( "Error synchronising state of Name_GUID_MAP", e );
        }  	
    }

    /**
     * @return the GUID from a properties entry - separated using SEPARATOR
     */
    private IGUID extractGUID( String text ) {
        StringTokenizer st = new StringTokenizer(text, SEPARATOR);
        if (st.countTokens() != 2) {
            Error.hardError( "internal inconsistency in mapping, found " + st.countTokens() + " expected 2" );
            // not reached
            return null;
        } else {
            try {
                return new KeyImpl(st.nextToken()); // first token is the GUID
            } catch (GUIDGenerationException e) {
                Error.hardError( "Could not extract GUID");
                return null;
            }
        }
    }
   
    /**
     * @return the GUID from a properties entry - separated using SEPARATOR
     */
    private String extractAttributes( String text ) {
        StringTokenizer st = new StringTokenizer(text, SEPARATOR);
        if (st.countTokens() != 2) {
            Error.hardError( "internal inconsistency in mapping, found " + st.countTokens() + " expected 2" );
            // not reached
            return null;
        } else {    
            st.nextToken();
            return st.nextToken(); // first token are the attributes
        }
    }
}
