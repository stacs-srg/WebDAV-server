package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.filesystem.absfilesystem.storebased.StoreBasedDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.filesystem.utils.ConversionHelper;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.store.general.NameGUIDBinding;
import uk.ac.standrews.cs.store.interfaces.INameGUIDMap;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.GUIDFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDirectory extends SOSFileSystemObject implements IDirectory {

    private INameGUIDMap map;

    private Collection<Content> contents;

    public SOSDirectory(SeaOfStuff sos, INameGUIDMap map) {
        super(sos);
        this.map = map;

        contents = new HashSet<>();
    }

    public SOSDirectory(SeaOfStuff sos, IGUID guid) {
        // TODO - create a directory that already exists - needed from the #get() method in this class
        super(sos);
    }


    @Override
    public IAttributedStatefulObject get(String name) {
        IGUID guid = map.get(name);

        return getObject(guid);

        // NOTE
        // check what this is, get it from SOS and then return either another directory or a file
        // see StoreDirectory for hints on how to do this
    }

    @Override
    public boolean contains(String name) {
        return get(name) != null;
    }

    @Override
    public void addFile(String name, IFile file, String content_type) throws BindingPresentException {
        // create new compound with given file
        // result in new version

        addObject(name, file, null);
    }

    @Override
    public void addDirectory(String name, IDirectory directory) throws BindingPresentException {
        // same as above - from the compound perspective it does not matter whether it is a compound or an atom that we add
        // result in new version
        addObject(name, directory, null);
        directory.setParent(this); // TODO - in SOS what does it mean for a directory to know its parent?
    }

    // FIXME - mostly duplicated code in StoreBasedDirectory
    private void addObject(String name, IAttributedStatefulObject object, IAttributes atts) throws BindingPresentException {

        map.put(name, object.getGUID());

        contents.add(new Content(name, ConversionHelper.toSOSGUID(object.getGUID())));

        // Set the attributes for the name.
        try {
            map.setAttributes(name, atts);
        } catch (BindingAbsentException e) {
            Error.hardExceptionError("Couldn't set attributes for newly added name", e);
        }

    }

    @Override
    public void remove(String name) throws BindingAbsentException {
        // remove an element from the compound
        // result in new version
        // iterate over collection and remove element with given name
        // then persist
    }

    @Override
    public void persist() throws PersistenceException {
        try {
            Compound compound = sos.addCompound(CompoundType.COLLECTION, contents);

            uk.ac.standrews.cs.utils.IGUID content = compound.getContentGUID();
            Asset asset = sos.addAsset(content, null, null, null); // TODO - add metadata

            // TODO - maybe return asset GUID
            guid = GUIDFactory.recreateGUID(asset.getVersionGUID().toString());

        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
        }
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
        return new CompoundIterator();
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

    public SOSFileSystemObject getObject(IGUID guid) {
        if (guid == null)
            return null;

        try {
            Manifest manifest = sos.getManifest(ConversionHelper.toSOSGUID(guid));
            if (manifest instanceof Atom) {
                return new SOSFile(sos, guid);
            } else if (manifest instanceof  Compound) {
                // Still this might be a data compound
                Compound compound = (Compound) manifest;
                if (compound.getType() == CompoundType.DATA) {
                    return null; // Make compound file
                } else {
                    return new SOSDirectory(sos, guid);
                    // return null; // Make collection
                }
            } else if (manifest instanceof Asset) {
                return getObject(ConversionHelper.toWebDAVGUID(manifest.getContentGUID()));
            }
        } catch (ManifestNotFoundException e) {
            return null; // FIXME - deal gracefully with this exception
        }
        return null;
    }


    private class CompoundIterator implements Iterator {

        Iterator<Content> contentIterator;

        public CompoundIterator() {
            contentIterator = contents.iterator();
        }

        public void remove() {
            Error.hardError("unimplemented method");
        }

        public boolean hasNext() {
            return contentIterator.hasNext();
        }

        public Object next() {
            Content content = contentIterator.next();

            SOSFileSystemObject obj = getObject(ConversionHelper.toWebDAVGUID(content.getGUID()));
            String name = content.getLabel();
            if (obj == null)
                return null;
            else
                return new NameAttributedPersistentObjectBinding(name, obj);
        }
    }
}
