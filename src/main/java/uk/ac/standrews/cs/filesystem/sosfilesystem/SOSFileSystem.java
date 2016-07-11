package uk.ac.standrews.cs.filesystem.sosfilesystem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.exceptions.AppendException;
import uk.ac.standrews.cs.filesystem.exceptions.UpdateException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.util.UriUtil;

import java.net.URI;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystem implements IFileSystem {

    private SeaOfStuff sos;

    private IGUID invariant;
    private IGUID head; // FIXME - head is never updated, but it should.
    private IDirectory root_collection;

    public SOSFileSystem(SeaOfStuff sos, IGUID rootGUID) {
        this.sos = sos;
        this.invariant = rootGUID;

        if (invariant == null) {
            invariant = GUIDFactory.generateRandomGUID();
        } else {
            // TODO - check if there assets with this invariant and load the latest?
        }

        try {
            root_collection = new SOSDirectory(sos);
            ((SOSDirectory) root_collection).setInvariant(invariant);
            root_collection.persist();
            head = root_collection.getGUID();
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    // TODO - create compound if large file
    // maybe have a different call for large files
    // via appendToFile
    @Override
    public IFile createNewFile(IDirectory parent, String name, String content_type, IData data) throws BindingPresentException, PersistenceException {

        // TODO - check if file already exists.
        // if it does, then throw exception BindingPresentException
        // should check against sos. So it will be a check by content, not by name
        // not sure how this will work because the stream will have to be consumed
        // check(parent, name, ...) // look at StoreBasedFileSystem

        IFile file = new SOSFile(sos, data);
        file.persist();

        // This Operation will create a new compound + asset
        ((SOSDirectory) parent).setInvariant(((SOSDirectory) parent).getInvariant());
        ((SOSDirectory) parent).setPrevious(parent.getGUID());
        parent.addFile(name, file, content_type);

        parent.persist();
        return file;
    }

    // TODO - should override
    //  this should be in IFile system
    // This way we could have a uniform way of dealing with large data (chunked)
    public IFile createNewFile(IDirectory parent, String name, String content_type) throws BindingPresentException, PersistenceException {

        // TODO - check if file already exists.
        // see comment above

        IFile file = new SOSFile(sos);
        // TODO - add to parent only when the file is persisted.
        return file;
    }

    @Override
    public synchronized void updateFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, UpdateException, PersistenceException {

        IAttributedStatefulObject previous = parent.get(name);
        IFile file = new SOSFile(sos, data, previous);
        file.persist();

        // This Operation will create a new compound + asset
        try {
            parent.addFile(name, file, content_type); // TODO - should add previous
        } catch (BindingPresentException e) {
           throw new PersistenceException("Binding exception on update file for SOS");
        }
        parent.persist();

    }

    @Override
    public synchronized void appendToFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, AppendException, PersistenceException {

        // NOTE call a series of append calls on SOSFile and end it with a persist - behaviour is different from the one in abstract file system

    }

    @Override
    public IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException {

        // TODO - should check if directory already exists

        IDirectory directory = null;
        try {
            directory = new SOSDirectory(sos);
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }
        directory.persist();

        parent.addDirectory(name, directory);
        parent.persist();

        return directory;
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
        // note: the HEAD could be changed via cli
        return root_collection;
    }

    @Override
    public IGUID getRootId() {
        // TODO - should this return the asset guid or the compound guid?
        return this.head;
    }


    // TODO - duplicate in AbstractFileSystem
    @Override
    public IAttributedStatefulObject resolveObject(URI uri) {

        Iterator iterator = UriUtil.pathElementIterator(uri);
        IDirectory parent = getRootDirectory();

        IAttributedStatefulObject object = parent;

        while (iterator.hasNext()) {

            String name = (String) iterator.next();

            object = parent.get(name);

            if (object == null)
                return null;  // No object with the current name.

            try {
                if (iterator.hasNext())
                    parent = (IDirectory) object;
            } catch (ClassCastException e) {
                return null;  // Current object isn't a directory, and we haven't reached the end of the path, so invalid path.
            }
        }

        return object;
    }
}
