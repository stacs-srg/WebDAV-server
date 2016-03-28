/*
 * Created on Jun 23, 2005 at 1:42:11 PM.
 */
package uk.ac.standrews.cs.filesystem.interfaces;

import java.util.List;

/**
 * List of strings used to represent the names in an abstract file path.
 * 
 * @author al
 */
public interface IFilePath extends List {
	
    /**
     * Extracts a sub-path from this path.
     * 
     * @param from_index the start index for the sub-path (inclusive), 0 indicating the start of this path
     * @param to_index the end index for the sub-path (exclusive)
     * @return the extracted sub-path
     */
    IFilePath subPath(int from_index, int to_index);
    
    /**
     * Returns the name of the final element on the path.
     * 
     * @return the name of the final element on the path
     */
    String baseName();
}
