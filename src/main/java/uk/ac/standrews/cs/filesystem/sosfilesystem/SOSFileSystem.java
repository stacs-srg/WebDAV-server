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
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;

import java.net.URI;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystem implements IFileSystem {

    private SeaOfStuff sos;

    public SOSFileSystem(SeaOfStuff sos) {
        this.sos = sos;
    }

    // TODO - create compound if large file
    @Override
    public IFile createNewFile(IDirectory parent, String name, String content_type, IData data) throws BindingPresentException, PersistenceException {

        // TODO - check if file already exists.
        // if it does, then throw exception BindingPresentException
        // should check against sos. So it will be a check by content, not by name

        IFile file = new SOSFile(sos, parent, name, data);

        return file;
    }

    @Override
    public void updateFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, UpdateException, PersistenceException {

        // THIS WILL CREATE A NEW ATOM (under asset)
        // not sure if this will ever be called. it depends on whether we update based on name or not. need to check
    }

    @Override
    public void appendToFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, AppendException, PersistenceException {
        // This will be used for large data
        // this should result in a compound of type bigdata
        // need to have a special call for the last incoming data, so that we can finalise all atoms and create the compound

    }

    @Override
    public IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException {

        // TODO - this will create a compound of type collection

        return null;
    }

    @Override
    public void deleteObject(IDirectory parent, String name) throws BindingAbsentException {

        // TODO - This will add a version to the asset with no content

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

        // TODO - make a call to the sea of stuff to resolve a path
        // sos.getManifest(GUID)
        return null;
    }
}
