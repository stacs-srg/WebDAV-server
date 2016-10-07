package uk.ac.standrews.cs.filesystem.sosfilesystem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
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
import uk.ac.standrews.cs.sos.utils.LOG;
import uk.ac.standrews.cs.util.Error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDirectory extends SOSFileSystemObject implements IDirectory {

    private Collection<Content> contents;
    private Compound compound;

    public SOSDirectory(Client sos, SOSDirectory parent, String name) throws GUIDGenerationException {
        super(sos);
        this.name = name;
        this.parent = parent;
        contents = new HashSet<>();

        try {
            compound = sos.addCompound(CompoundType.COLLECTION, contents);
        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
        }
    }

    public SOSDirectory(Client sos, Version version, Compound compound) {
        super(sos);

        contents = compound.getContents();
        this.compound = compound;
        this.version = version;
    }

    // Use this constructor for the root folder only
    public SOSDirectory(Client sos, Version version) {
        super(sos);
        this.version = version;
        this.name = null;

        try {
            compound = (Compound) sos.getManifest(version.getContentGUID());
            contents = compound.getContents();
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }
    }

    public SOSDirectory(Client sos, SOSDirectory previous, String name, SOSFileSystemObject object) {
        super(sos);

        try {
            this.name = previous.name;
            contents = new ArrayList<>(previous.getContents());
            addOrUpdate(name, new Content(name, object.getGUID()));

            compound = sos.addCompound(CompoundType.COLLECTION, contents);

            boolean previousVersionDiffers = checkPreviousDiffers(compound.getContentGUID());
            if (previousVersionDiffers) {

                Collection<IGUID> previousVersion = new ArrayList<>();
                previousVersion.add(previous.getVersion().getVersionGUID());
                VersionBuilder versionBuilder = new VersionBuilder(compound.getContentGUID())
                        .setInvariant(previous.getInvariant())
                        .setPrevious(previousVersion);

                version = sos.addVersion(versionBuilder);

                this.previous = previous;
            } else {
                System.out.println("This create an identical new object to previous. Can be optimised to occupy less memory");
                this.previous = previous.getPreviousDIR();
            }



        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
        }

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
        LOG.log(LEVEL.INFO, "WEBDAV - Add file " + name + " to folder " + this.name);

        addObject(name, file, null);
    }

    @Override
    public void addDirectory(String name, IDirectory directory) throws BindingPresentException {
        // same as above - from the compound perspective it does not matter whether it is a compound or an atom that we add
        // result in new version
        LOG.log(LEVEL.INFO, "WEBDAV - Add directory " + name + " to folder " + this.name);

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

    private void addOrUpdate(String name, Content newContent) {
        for(Content content:contents) {
            if (content.getLabel().equals(name)) {
                contents.remove(content);
                contents.add(newContent);

                return;
            }
        }

        contents.add(newContent);
    }

    @Override
    public void remove(String name) throws BindingAbsentException {
        // remove an element from the compound
        // result in new version
        // iterate over collection and remove element with given name
        // then persist
        LOG.log(LEVEL.INFO, "WEBDAV - Remove object " + name + " from folder " + this.name);

        contents.remove(getContent(name));
    }

    protected IGUID getContentGUID() {
        return compound.getContentGUID();
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

    public Collection<Content> getContents() {
        return contents;
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
            if (manifest instanceof Version) {
                return getObject((Version) manifest);
            } else {
                LOG.log(LEVEL.ERROR, "WEBDAV - attempting to retrieve manifest of wrong type");
            }
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public SOSFileSystemObject getObject(Version version) {

        try {
            IGUID contentGUID = version.getContentGUID();
            Manifest manifest = sos.getManifest(contentGUID);
            if (manifest instanceof Atom) {
                return new SOSFile(sos, version, (Atom) manifest);

            } else if (manifest instanceof  Compound) {
                return getCompoundObject(version, (Compound) manifest);
            }
        } catch (GUIDGenerationException | ManifestNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    private SOSDirectory getCompoundObject(Version version, Compound compound) throws GUIDGenerationException {
        // Still this might be a data compound
        if (compound.getType() == CompoundType.DATA) {
            return null; // Make compound file
        } else {
            return new SOSDirectory(sos, version, compound);
        }
    }

    private SOSDirectory getPreviousDIR() {
        return (SOSDirectory) previous;
    }

    /**
     * Iterator for the compound. The iterator returns Contents until hasNext returns false.
     */
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
