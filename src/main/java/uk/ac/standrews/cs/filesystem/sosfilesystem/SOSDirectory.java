package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDirectory implements IDirectory {
    @Override
    public IAttributedStatefulObject get(String name) {
        return null;
    }

    @Override
    public boolean contains(String name) {
        return false;
    }

    @Override
    public void addFile(String name, IFile file, String content_type) throws BindingPresentException {

    }

    @Override
    public void addDirectory(String name, IDirectory directory) throws BindingPresentException {

    }

    @Override
    public void remove(String name) throws BindingAbsentException {

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

    @Override
    public void update(IData data) {

    }

    @Override
    public void append(IData data) {

    }

    @Override
    public IData reify() {
        return null;
    }

    @Override
    public void initialise(IData data, IPID pid, IGUID guid) {

    }

    @Override
    public void persist() throws PersistenceException {

    }

    @Override
    public IPID getPID() {
        return null;
    }

    @Override
    public IGUID getGUID() {
        return null;
    }
}
