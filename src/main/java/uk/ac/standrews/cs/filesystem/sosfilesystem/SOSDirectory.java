package uk.ac.standrews.cs.filesystem.sosfilesystem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.*;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.util.Error;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDirectory extends SOSFileSystemObject implements IDirectory {

    private Collection<Content> contents;

    public SOSDirectory(Client sos) throws GUIDGenerationException {
        super(sos);
        contents = new HashSet<>();
    }

    public SOSDirectory(Client sos, IGUID guid) throws GUIDGenerationException {
        // TODO - create a directory that already exists - needed from the #get() method in this class
        super(sos);
    }


    @Override
    public IAttributedStatefulObject get(String name) {
        Content content = getContent(name);
        IGUID guid = null;
        if (content != null) {
            guid = content.getGUID();
        }

        return getObject(guid);
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
    }

    private void addObject(String name, IAttributedStatefulObject object, IAttributes atts) throws BindingPresentException {
        if (contains(name)) {
            throw new BindingPresentException("Object already exists");
        } else {
            contents.add(new Content(name, object.getGUID()));
        }

    }

    private Content getContent(String name) {
        for(Content content:contents) {
            if (content.getLabel().equals(name)) {
                return content;
            }
        }
        return null;
    }

    @Override
    public void remove(String name) throws BindingAbsentException {
        // remove an element from the compound
        // result in new version
        // iterate over collection and remove element with given name
        // then persist
        contents.remove(getContent(name));
    }

    @Override
    public void persist() throws PersistenceException {
        try {
            Compound compound = sos.addCompound(CompoundType.COLLECTION, contents);

            IGUID content = compound.getContentGUID();
            Version asset = sos.addVersion(new VersionBuilder(content)
                    .setInvariant(getInvariant())); // TODO - metadata

            IGUID version = asset.getVersionGUID();
            guid = GUIDFactory.recreateGUID(version.toString());
        } catch (ManifestNotMadeException | GUIDGenerationException | ManifestPersistException e) {
            throw new PersistenceException("Manifest could not be created or persisted");
        }
    }

    @Override
    public IDirectory getParent() {
        return null;
    }

    @Override
    public void setParent(IDirectory parent) {
        throw new NotImplementedException();
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
            Manifest manifest = sos.getManifest(guid);
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
            } else if (manifest instanceof Version) {
                return getObject(manifest.getContentGUID());
            }
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
            return null;
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
            return null;
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

            SOSFileSystemObject obj = getObject(content.getGUID());
            String name = content.getLabel();
            if (obj == null)
                return null;
            else
                return new NameAttributedPersistentObjectBinding(name, obj);
        }
    }
}
