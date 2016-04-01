/*
 * Created on May 30, 2005 at 1:11:31 PM.
 */
package uk.ac.standrews.cs.filesystem.interfaces;

import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.exceptions.AppendException;
import uk.ac.standrews.cs.filesystem.exceptions.UpdateException;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;

import java.net.URI;

/**
 * Abstract file system interface.
 * 
 * @author al, graham
 */
public interface IFileSystem {

    /**
     * Creates a completely new file and a binding to it in a directory. Similar to 'creat' in Unix.
     * 
     * @param parent the directory in which the binding should be created
     * @param name the name for the directory entry
     * @param content_type the content type of the file
     * @param data the contents of the file
     * @return the new file
     * 
     * @throws BindingPresentException if a binding with the same name is already present in the directory
     * @throws PersistenceException if the new file cannot be made persistent
     */
    IFile createNewFile(IDirectory parent, String name, String content_type, IData data) throws BindingPresentException, PersistenceException;

    /**
     * Updates an existing file.
     * 
     * @param parent the directory containing an entry for the file
     * @param name the name for the directory entry
     * @param content_type the new content type of the file
     * @param data the new contents of the file
     * 
     * @throws BindingAbsentException if there is no directory entry with the given name
     * @throws UpdateException if the directory entry with the given name is not a file
     * @throws PersistenceException if the updated file cannot be made persistent
     */
    void updateFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, UpdateException, PersistenceException;

    /**
     * Appens data to an existing file.
     *
     * @param parent the directory containing an entry for the file
     * @param name the name for the directory entry
     * @param content_type the new content type of the file
     * @param data the new contents to append to the file
     *
     * @throws BindingAbsentException
     * @throws AppendException
     * @throws PersistenceException
     */
    void appendToFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, AppendException, PersistenceException;

    /**
     * Creates a completely new sub-directory and a binding to it in a parent directory. Similar to 'creat' in Unix.
     * 
     * @param parent the directory in which the binding should be created
     * @param name the name for the directory entry
     * @return the new directory
     * 
     * @throws BindingPresentException if a binding with the same name is already present in the directory
     * @throws PersistenceException if the new directory cannot be made persistent
     */
    IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException;

    /**
     * Deletes an existing file or directory.
     * 
     * @param parent the parent directory containing the entry to be deleted
     * @param name the name of the directory entry to be deleted
     * 
     * @throws BindingAbsentException if there is no directory entry with the given name
     */
    void deleteObject(IDirectory parent, String name) throws BindingAbsentException;

    /**
     * Deletes a given binding to an object and creates a new binding to the same object in a different location.
     * 
     * @param source_parent the directory containing the binding to the object
     * @param source_name the name of the directory entry currently referring to the object
     * @param destination_parent the directory in which the new binding should be created
     * @param destination_name the name for the new directory entry
     * @param overwrite true if any existing entry at the destination location should be overwritten
     * 
     * @throws BindingAbsentException if there is no entry with the source name in the source directory
     * @throws BindingPresentException if an entry with the destination name is already present in the destination directory and the overwrite flag is false
     */
    void moveObject(IDirectory source_parent, String source_name,
                    IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException;

    /**
     * Creates a complete copy of an object and creates a binding to the copy in a different location.
     * 
     * @param source_parent the directory containing the binding to the existing object
     * @param source_name the name of the directory entry referring to the existing object
     * @param destination_parent the directory in which the binding to the copied object should be created
     * @param destination_name the name for the directory entry for the copied object
     * @param overwrite true if any existing entry at the destination location should be overwritten
     * 
     * @throws BindingAbsentException if there is no entry with the source name in the source directory
     * @throws BindingPresentException if an entry with the destination name is already present in the destination directory and the overwrite flag is false
     * @throws PersistenceException if the copied object cannot be made persistent
     */
    void copyObject(IDirectory source_parent, String source_name,
                    IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException, PersistenceException;

    /**
     * Returns the root directory of the file system.
     * 
     * @return the file system root directory
     */
    IDirectory getRootDirectory();

    /**
     * Returns the root GUID of the file system. This is the GUID of the root
     * name <-> GUID map. Note that this is different from the GUID of the root
     * directory, which can be obtained using <code>getRootDirectory().getGUID()</code>.
     * 
     * @return the root GUID of the file system
     */
    IGUID getRootId();
    
    /**
     * Returns the file or directory associated with a given file path expression.
     * 
     * @param file_path a path expression to a file or directory
     * @return the file or directory associated with that path, or null if the given path expression does not resolve to a file or directory
     */
    IAttributedStatefulObject resolveObject(URI file_path);
}
