/*
 * Created on May 23, 2005 at 10:51:17 AM.
 */
package uk.ac.standrews.cs.store.impl.localfilebased;

import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.util.Error;

import java.io.*;
import java.util.Arrays;

/**
 * IData implementation using a conventional file.
 *
 * @author al
 */
public class FileData implements IData {

    private File theFile;
    
    /**
     * Creates an instance using a given file.
     * 
     * @param theFile a file containing the underlying data
     */
    public FileData(File theFile) {
        this.theFile = theFile;
    }
    
    /**
     * Gets the data.
     * 
     * @return the underlying data
     */
    public byte[] getState() {
        byte[] bytes = new byte[(int) getSize()];
        try {
            new FileInputStream(theFile).read(bytes);
        } catch (FileNotFoundException e) {
            Error.exceptionError("Cannot find file: " + theFile.getName(), e);
        } catch (IOException e) {
            Error.exceptionError("IO error: " + theFile.getName(),e);
        }
        return bytes;
    }
    
    /**
     * Gets the size of the data in bytes.
     * 
     * @return the size of the data
     */
    public long getSize() {
        return theFile.length();
    }

    /**
     * Creates an input stream reading from the file.
     * 
     * @return an input stream reading from the file
     */
    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(theFile);
    }

    /**
     * Tests equality with another instance.
     * 
     * @return true if the file's contents are equivalent to those of the given file
     * @see Object#equals(Object)
     */
    public boolean equals( Object o ) {
        return o instanceof IData && Arrays.equals( getState(), ((IData)(o)).getState() );
    }
}
