/*
 * Created on May 20, 2005 at 5:03:39 PM.
 */
package uk.ac.standrews.cs.store.impl.localfilebased;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.impl.KeyImpl;
import uk.ac.standrews.cs.IPID;
import uk.ac.standrews.cs.persistence.impl.PIDGenerator;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.persistence.interfaces.IPIDGenerator;
import uk.ac.standrews.cs.store.exceptions.StorePutException;
import uk.ac.standrews.cs.store.interfaces.IGUIDStore;
import uk.ac.standrews.cs.store.interfaces.IManagedGUIDStore;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Error;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Simple implementation of the IRootedGUIDStore interface.
 * Works by mapping all GUIDs to filenames in a single directory.
 *
 * @author al, graham
 */
public class FileStore implements IGUIDStore, IManagedGUIDStore {

    private String store_directory_path;
    private String guid_to_pid_directory_path;
    private PIDGenerator pidgen;
    
    private static final String LASTEST_PID_FILE_NAME_PREFIX = "LATEST-";
//    private static final String ROOTFILENAME = "ROOTGUID";

    ////////////////////////////////////////////// Constructor //////////////////////////////////////////////////////////
    
    /**
     *  Construct an instance of a FileStore using a supplied Directory as the store implementation
     */
    public FileStore( String store_directory_path, String guid_to_pid_directory_path) {
    	
        Diagnostic.trace( "Attempting to create a FileStore using file: " + store_directory_path, Diagnostic.INIT );
        
        this.store_directory_path = store_directory_path;
        this.guid_to_pid_directory_path = guid_to_pid_directory_path;
    	pidgen = new PIDGenerator();

        checkDir(store_directory_path);
        checkDir(guid_to_pid_directory_path);
    }

    ////////////////////////////////////////////// IStore Operations //////////////////////////////////////////////////////////
    
    /**
     * Gets the persistent object associated with a given PID.
     * 
     * @param pid the PID of a persistent object
     * @return the contents of the corresponding persistent object, or null if no object found
     */
    public IData get(IPID pid) {
    	
        String filename = pidToFilePath(pid);
        Diagnostic.trace( "Attempting to access file: " + filename, Diagnostic.RUN );
        File theFile = new File( filename );
        
        if (theFile.isFile()) {
            Diagnostic.trace( "Returning a file", Diagnostic.RUN );
            return new FileData( theFile );
        }
        
        if (!theFile.exists()) {
            Diagnostic.trace( "File doesn't exist - returning null", Diagnostic.RUN );
            return null;  // Non-existent PID.
        }
        
        if (theFile.isDirectory()) Error.hardError( "File is system directory: " + filename );
        Error.hardError( "Encountered an object that is neither file nor directory: " + filename );

        return null;
    }

    public long getPIDPutDate(IPID pid) {
        String filename = pidToFilePath(pid);
        Diagnostic.trace( "Attempting to access file: " + filename + " with pid: " + pid, Diagnostic.RUN );
        File theFile = new File( filename );
        
        if( !theFile.exists() ) {
            Diagnostic.trace( "File doesn''t exist - returning 0:" + filename, Diagnostic.RUN );
            return 0;  // Non-existent PID.
        }      
        return theFile.lastModified(); 
    }
    
    /**
     * Puts an object into the store. If the same data is already in the store, the existing PID is returned.
     * 
     * @param data the contents of an object to be made persistent
     * @return a PID for subsequent retrieval of the object
     * @throws StorePutException if the object could not be made persistent
     */
    public IPID put(IData data) throws StorePutException {
    	
        byte[] bytes = data.getState();
        
        IPID pid = pidgen.dataToPID(data);
        
        Diagnostic.trace("adding data of length: " + bytes.length + " with pid " + pid, Diagnostic.RUN);
        
        String filename = pidToFilePath(pid);
        File theFile = new File( filename );
        
        if (theFile.exists()) {
        	
        	// The PID derived from the data already exists in the store. Check whether the data
        	// associated with it is equal to the new data.
        	
        	IData existing_data = get(pid);
        	
        	if (data.equals(existing_data)) Diagnostic.trace("file already exists: " + filename + ", returning existing PID", Diagnostic.RUN);
        	else                            Error.hardError("file for PID " + pid + " already exists, but with different data");
        }
        else {
        	
        	// The PID derived from the data does not already exist in the store.

        	try {
		        if (! theFile.createNewFile()) Error.hardError("file already exists despite having checked that it doesn't");          
		        
		        FileOutputStream output_stream = new FileOutputStream(theFile);
				output_stream.write(bytes);
				output_stream.close();
        	}
        	catch (IOException e) { throw new StorePutException("cannot create file to hold data: " + filename + " - " + e.getMessage());}
        }
 
        return pid;
    }

    ////////////////////////////////////////////// IGUID_PID_Map Operations //////////////////////////////////////////////////////////
    
    /**
     * Gets the PID most recently associated with a GUID.
     * 
     * @param guid the GUID to be looked up
     * @return the latest PID associated with the given GUID
     */
    public IPID getLatestPID(IGUID guid) {
    	
        String latest_PID_file_name = guidToLatestFilePath(guid);
        Diagnostic.trace( "Attempting to access latest PID file: " + latest_PID_file_name, Diagnostic.RUN );
        
        File latest_PID_file = new File( latest_PID_file_name );
        if (! latest_PID_file.exists()) {
            Diagnostic.trace( "Cannot open file: " + latest_PID_file_name, Diagnostic.RUN);
            return null;
        }
        
        if (!latest_PID_file.isFile() ) {
            Error.hardError( "Latest PID file is not a file: " + latest_PID_file_name );
            return null;
        }
        
        try {
            InputStream stream = new FileInputStream(latest_PID_file);
            BufferedReader br = new BufferedReader( new InputStreamReader( stream ) );
            String pid_rep = br.readLine();
            return new KeyImpl(pid_rep);
        } catch (FileNotFoundException e) {
            Error.exceptionError( "Cannot open file: " + latest_PID_file_name, e );
        } catch (IOException e) {
            Error.exceptionError( "IO exception reading from stream from: " + latest_PID_file_name, e );
        } catch (GUIDGenerationException e) {
            Error.exceptionError( "GUIDGeneration exception on file: " + latest_PID_file_name, e );
        }

        return null;
    }

    public long getGUIDPutDate(IGUID guid) {
        String guid_file_name = guidToFilePath(guid);
        Diagnostic.trace("attempting to access file: " + guid_file_name, Diagnostic.RUN);
        long time=-1;
        // the file we are opening is an index file with the name of GUID in the guid_to_pid_directory
        File guid_file = new File(guid_file_name);
        
        if (guid_file.isDirectory()) Error.hardError("GUID file is a directory: " + guid_file_name);

        if (guid_file.isFile()) {     // if it doesn't exist return -1
            try{
                InputStream stream = new FileInputStream(guid_file);
                BufferedReader br = new BufferedReader( new InputStreamReader( stream ) );
                String pid_rep = br.readLine();
                time=Long.parseLong(pid_rep);
                return time;
            }catch (FileNotFoundException e){Error.exceptionError("Cannot open file: "+guid_file_name,e);}
            catch (IOException e){Error.exceptionError( "IO exception reading from stream from: "+guid_file_name,e);}
            catch (NumberFormatException e){Error.exceptionError( "Date field in GUID file: "+guid_file_name+" was not a valid data value",e);}
        }
        return time;
    }

    /**
     * Gets all the PIDs associated with a GUID.
     * 
     * @param guid the GUID to be looked up
     * @return an iterator over all the PIDs associated with the given GUID, each typed as PID
     */
    public Iterator getAllPIDs(IGUID guid) {
        String filename = guidToFilePath(guid);
        Diagnostic.trace( "Attempting to access file: " + filename, Diagnostic.RUN );
        File theFile = new File( filename );
        
        return new PIDIterator(theFile);
     }

    /**
     * Adds a new GUID to PID mapping.
     * 
     * @param guid the GUID
     * @param pid the PID
     */
    public void put(IGUID guid, IPID pid) {
    	boolean created=false;
        Diagnostic.trace("associating PID = " + pid + " with GUID = " + guid, Diagnostic.RUN);
        String guid_file_name = guidToFilePath(guid);
        Diagnostic.trace("attempting to access file: " + guid_file_name, Diagnostic.RUN);
        
        // the file we are opening is an index file with the name of GUID in the guid_to_pid_directory
        File guid_file = new File(guid_file_name);
        
        if (guid_file.isDirectory()) Error.hardError("GUID file is a directory: " + guid_file_name);

        if (! guid_file.isFile()) {		// if it doesn't exist create it
        	
            Diagnostic.trace("GUID file doesn't exist - creating it: " + guid_file_name, Diagnostic.RUN);
            try {
                if (! guid_file.createNewFile()) Error.hardError("cannot create GUID file: " + guid_file_name);
                created=true;
            }
            catch (IOException e1) { Error.hardExceptionError("cannot create GUID file: " + guid_file_name, e1); }
        }
    
        // we get to here have the index file ready to add stuff
        // first add data to the index file
        try {
            FileWriter fw = new FileWriter(guid_file, true);	// append data to the file
            if(created){
                long currentTime = System.currentTimeMillis();
                fw.write(Long.toString(currentTime));
                fw.write( "\n" );
            }
            fw.write( pid.toString() );					    // concatenate the PID to end of file
            fw.write( "\n" );
            fw.close();
        }
        catch (IOException e) { Error.exceptionError("cannot create FileWriter for GUID file: " + guid_file_name, e); }
        
        // next, save the latest pid in the latest file to make last lookups efficient		
        String latest_pid_file_name = guidToLatestFilePath(guid);	// TODO Al - need file locking!!!!!!!!!!!!!!!!!!!!!!
        Diagnostic.trace("attempting to access latest PID file: " + latest_pid_file_name, Diagnostic.RUN);
        
        File latest_pid_file = new File(latest_pid_file_name);
        if (! latest_pid_file.isFile()) {		// if it doesn't exist create it
        	
            try {
                if (! latest_pid_file.createNewFile()) Error.hardError("cannot create latest PID file: " + latest_pid_file_name);
            }
            catch (IOException e1) { Error.hardExceptionError("cannot create latest PID file: " + latest_pid_file_name, e1); }
        }
        
        // now add the PID data to the latest pid file
        try {
            FileWriter fw = new FileWriter(latest_pid_file, false);	// This should overwrite data in file.
            fw.write(pid.toString());					// concatenate the PID to end of file
            fw.write("\n");
            fw.close();
        }
        catch (IOException e) { Error.exceptionError("cannot create FileWriter for latest PID file: " + latest_pid_file, e); }
    }
    
    ////////////////////////////////////////// IRootedGUIDStore methods ///////////////////////////////////////
    
//    public IGUID getRoot() throws StoreIntegrityException {
//    	
//        Diagnostic.trace( "Getting root: " + store_directory_path + File.separator + ROOTFILENAME, Diagnostic.FULL );
//        
//        File theFile = new File(store_directory_path + File.separator + ROOTFILENAME);
//        if (! theFile.exists()) return null;
//
//        try {
//            InputStream stream = new FileInputStream(theFile);
//            BufferedReader br = new BufferedReader( new InputStreamReader( stream ) );
//            String guid_rep = br.readLine();
//            return GUIDFactory.recreateGUID(guid_rep);
//        }
//        catch (IOException e) { throw new StoreIntegrityException( "Cannot read root file: " + ROOTFILENAME); }
//    }
//
//    public void setRoot(IGUID g) {
//        Diagnostic.trace( "Setting root:" + store_directory_path + File.separator + ROOTFILENAME, Diagnostic.FULL );
//        File theFile = new File( store_directory_path + File.separator + ROOTFILENAME );
//        
//        if( theFile.isFile() ) {		// if the root file exists already
//            Error.hardError( "Root file for store already exists" );
//        }
//        try {
//            if( ! theFile.createNewFile() ) {
//                Error.hardError( "Cannot create root file: " + ROOTFILENAME );
//            }
//        } catch (IOException e) {
//            Error.exceptionError( "Cannot create root file: " + ROOTFILENAME, e );
//        }
//        
//        try {
//            FileWriter fw = new FileWriter(theFile);
//            fw.write(g.toString()); // concatenate the GUID to end of file
//            fw.write( "\n" );
//            fw.close();
//        } catch (Exception e1) {
//            Error.exceptionError( "Cannot write to root file: " + ROOTFILENAME, e1 );
//        }
//    }
    
    public String toString() {
    	
    	return store_directory_path;
    }
    
    ////////////////////////////////////////// Helper methods //////////////////////////////////////////
    
    private void checkDir(String directory_path) {
    	
        File directory = new File( directory_path );
        
        if( ! directory.exists() ) {
            if (!directory.mkdirs() ) Error.hardError( "Cannot create directory: " + directory_path );
        }
        
        if( ! directory.exists() ) {
            Error.hardError( "Cannot open directory: " + directory_path );
        }
        
        if( ! directory.isDirectory() ) {
            Error.hardError( "Not a directory: " + directory_path );
        }
        
        if( ! directory.canRead() ) {
            Error.hardError( "Cannot read directory: " + directory_path );
        }
    }
    
	private String pidToFilePath(IPID pid) {
		return store_directory_path + File.separator + pid;
	}
	
	private String guidToFilePath(IGUID guid) {
		return guid_to_pid_directory_path + File.separator + guid;
	}

	private String guidToLatestFilePath(IGUID guid) {
		return guid_to_pid_directory_path + File.separator + LASTEST_PID_FILE_NAME_PREFIX + guid;
	}
    
	// Obsolete?
/*    private String guidToEarliestFilePath(IGUID guid) {        
        Iterator iter = getAllPIDs(guid); // although we want only the first
        if( ! iter.hasNext() ) {
            Error.error( "Cannot get guid-pid mapping for: " + guid );
            return "";
        }
        IPID pid = (IPID) iter.next();
        return pidToFilePath(pid);
    }   
*/    
    public IPIDGenerator getPIDGenerator(){
        return pidgen;
    }
    
    private class PIDIterator implements Iterator {

        private BufferedReader br;
        private String next;
        private boolean start=true;
        
        public PIDIterator(File theFile) {
        
	        if( ! theFile.exists() )    Error.hardError( "Cannot open file: " + theFile.getName() );
	        if( theFile.isDirectory() ) Error.hardError( "GUID file is a directory: " + theFile.getName() );
	        if( ! theFile.isFile() )    Error.hardError( "GUID file is not a standard file: " + theFile.getName() );
        
            try {
                InputStream stream = new FileInputStream(theFile);
                br = new BufferedReader( new InputStreamReader( stream ) );
                nextString();
            }
            catch (FileNotFoundException e) { Error.hardExceptionError( "Cannot open file: " + theFile.getName(), e ); }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return next != null;
        }

        public Object next() {
        	
        	if (next == null) throw new NoSuchElementException();
        	
            String thisOne = next;
            nextString();
            try {
                return new KeyImpl( thisOne  );
            } catch (GUIDGenerationException e) {
                Error.exceptionError( "GUIDGenerationException in PIDIterator", e );
                throw new NoSuchElementException();
            }
        }
        
        private void nextString() {
            try {
                /*
                 * Skip over the fisrt line of the file since it contains the
                 * GUID put time instead of a PID
                 */
                if(start){
                    br.readLine();
                    start=false;
                }
                next = br.readLine();
            } catch (IOException e) {
                Error.exceptionError( "Reading nextString in iterator over file", e );
            }
        }
    }

    public void removeGUID(IGUID guid) {
    	Error.hardError("unimplemented method");
    }

    public void removeVersion(IGUID gid, IPID versionPID) {
    	Error.hardError("unimplemented method");
    }

    public void removePID(IPID pid) {
    	Error.hardError("unimplemented method");
    }
}
