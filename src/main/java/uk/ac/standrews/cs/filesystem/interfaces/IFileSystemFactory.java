/*
 * Created on Sep 9, 2005 at 10:01:22 AM.
 */
package uk.ac.standrews.cs.filesystem.interfaces;


import uk.ac.standrews.cs.filesystem.exceptions.FileSystemCreationException;

/**
 * Provides an interface to allow the instantiation of a file system without knowing its implementation.
 *
 * @author graham
 */
public interface IFileSystemFactory {

    /**
     * Creates a new abstract file system instance.
     * 
     * @return a new file system
     * @throws FileSystemCreationException if the file system could not be instantiated
     */
    IFileSystem makeFileSystem() throws FileSystemCreationException;
}