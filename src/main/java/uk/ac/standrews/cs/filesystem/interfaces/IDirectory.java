/*
 * Created on May 30, 2005 at 2:23:39 PM.
 */
package uk.ac.standrews.cs.filesystem.interfaces;

import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.INameAttributedPersistentObjectBinding;

import java.util.Iterator;

/**
 * Abstract directory interface.
 * 
 * @author al, graham
 */
public interface IDirectory extends IAttributedStatefulObject {

    /**
     * Looks up a name in the directory.
     * 
     * @param name the name of the item to look up
     * @return the corresponding file or directory, or null if no such entry is found
     */
    IAttributedStatefulObject get(String name);

    /**
     * Checks whether a name is contained in the directory.
     * 
     * @param name the name of the item to look up
     * @return true if the directory contains an item with the given name
     */
    boolean contains(String name);

    /**
     * Adds a binding to the given existing file to the directory.
     * 
     * @param name the name of the new binding
     * @param file the existing file to be added
     * @param content_type the content type of the file
     * 
     * @throws BindingPresentException if a binding with the given name is already present in the directory
     */
    void addFile(String name, IFile file, String content_type) throws BindingPresentException;
    
    /**
     * Adds a binding to the given existing directory to the directory.
     * 
     * @param name the name of the new binding
     * @param directory the existing directory to be added
     * 
     * @throws BindingPresentException if a binding with the given name is already present in the directory
     */
    void addDirectory(String name, IDirectory directory) throws BindingPresentException;
    
    /**
     * Removes the binding with the given name from the directory.
     * 
     * @param name the name of the item to be removed
     * 
     * @throws BindingAbsentException if a binding with the given name is not present in the directory
     */
    void remove(String name) throws BindingAbsentException;
    
    /**
     * Returns the parent directory of this directory.
     * 
     * @return the parent directory of this directory, or null if this directory is the root directory
     */
    IDirectory getParent();
    
    /**
     * Sets the parent directory of this directory, discarding any existing link to a parent directory.
     * 
     * @param parent the new parent directory of this directory
     */
    void setParent(IDirectory parent);
    
    /**
     * Returns an iterator over the bindings in the directory.
     * 
     * @return an iterator over the bindings in the directory, each typed as {@link INameAttributedPersistentObjectBinding}
     */
    Iterator iterator();
}
