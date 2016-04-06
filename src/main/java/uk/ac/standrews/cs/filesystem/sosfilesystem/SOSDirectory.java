package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDirectory extends SOSFileSystemObject implements IDirectory {



    public SOSDirectory(SeaOfStuff sos /* name-guid map */) {
        super(sos);
    }


    @Override
    public IAttributedStatefulObject get(String name) {

        // this will return an object inside a compound

        return null;
    }

    @Override
    public boolean contains(String name) {

        // check if the compound contains an element with given name
        return false;
    }

    @Override
    public void addFile(String name, IFile file, String content_type) throws BindingPresentException {
        // create new compound with given file
        // result in new version
    }

    @Override
    public void addDirectory(String name, IDirectory directory) throws BindingPresentException {
        // same as above - from the compound perspective it does not matter whether it is a compound or an atom that we add
        // result in new version
    }

    @Override
    public void remove(String name) throws BindingAbsentException {

        // remove an element from the compound
        // result in new version
    }

    @Override
    public IDirectory getParent() {
        return null;
    }

    @Override
    public void setParent(IDirectory parent) {

    }

    @Override
    public Iterator iterator() {
        // iterate over elements of the compound
        return null;
    }

    @Override
    public IAttributes getAttributes() {
        return null;
    }

    @Override
    public void setAttributes(IAttributes attributes) {

    }

    @Override
    public long getCreationTime() throws AccessFailureException {
        return 0;
    }

    @Override
    public long getModificationTime() throws AccessFailureException {
        return 0;
    }

}
