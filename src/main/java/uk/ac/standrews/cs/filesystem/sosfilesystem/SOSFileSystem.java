package uk.ac.standrews.cs.filesystem.sosfilesystem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.exceptions.AppendException;
import uk.ac.standrews.cs.filesystem.exceptions.UpdateException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;

import java.net.URI;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystem implements IFileSystem {

    @Override
    public IFile createNewFile(IDirectory parent, String name, String content_type, IData data) throws BindingPresentException, PersistenceException {
        return null;
    }

    @Override
    public void updateFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, UpdateException, PersistenceException {

    }

    @Override
    public void appendToFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, AppendException, PersistenceException {

    }

    @Override
    public IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException {
        return null;
    }

    @Override
    public void deleteObject(IDirectory parent, String name) throws BindingAbsentException {
        throw new NotImplementedException();
    }

    @Override
    public void moveObject(IDirectory source_parent, String source_name, IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException {
        throw new NotImplementedException();
    }

    @Override
    public void copyObject(IDirectory source_parent, String source_name, IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException, PersistenceException {
        throw new NotImplementedException();
    }

    @Override
    public IDirectory getRootDirectory() {
        return null;
    }

    @Override
    public IGUID getRootId() {
        return null;
    }

    @Override
    public IAttributedStatefulObject resolveObject(URI file_path) {
        return null;
    }
}
