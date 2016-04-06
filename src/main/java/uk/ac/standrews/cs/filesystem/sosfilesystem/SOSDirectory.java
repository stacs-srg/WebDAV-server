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
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.store.general.NameGUIDBinding;
import uk.ac.standrews.cs.store.interfaces.INameGUIDMap;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Error;

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
                    return null; // Make collection
                }
            }
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        // TODO
        // check what this is, get it from SOS and then return either another directory or a file
        // see StoreDirectory for hints on how to do this

        return null;
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
        // TODO - remember to persist compound

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

            // TODO - add this to parent via asset ?

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
        return new CollectionIter(this);
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

    /**
     * This class acts as a type convertor hiding underlying iterator over GUIDs and gives
     * an iterator over Name_AttributedPersistentObject_Binding.
     *
     * @author al
     */
    // FIXME - duplicate in StoreBasedDirectory
    private class CollectionIter implements Iterator {

        Iterator baseIterator;
        IDirectory owner;

        public CollectionIter(IDirectory owner) {
            this.baseIterator = map.iterator();
            this.owner = owner;
        }

        public void remove() {
            baseIterator.remove();
        }

        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        public Object next() {
            Object o = baseIterator.next();
            if( o instanceof NameGUIDBinding) { // FIXME - should be SOSNameGUIDMap


//                NameGUIDBinding binding = (NameGUIDBinding) o;
//                String name = binding.getName();
//                IGUID g = binding.getGUID();
//                try {
//                    IAttributes atts = map.getAttributes(name);
//                    IDirectory d;
//                    if(atts.contains(FileSystemConstants.ISDIRECTORY)) {
//                        if( directoryCache.containsKey( g.toString() ) ) {
//                            Diagnostic.trace( "Found directory with name: " + name + " in cache", Diagnostic.RUN );
//                            d = (IDirectory) directoryCache.get( g.toString() );
//                        } else {
//                            d = makeCollection( g, owner, store, atts );
//                        }
//                        return new NameAttributedPersistentObjectBinding( name, d );
//                    }
//                    else if(atts.contains(FileSystemConstants.ISFILE)) {
//                        IFile f = makeFile( g, store, atts );
//                        return new NameAttributedPersistentObjectBinding( name, f );
//                    }
//                    else {
//                        Error.hardError( "encountered unknown file type" );
//                        // unreached
//                        return null;
//                    }
//                } catch (Exception e) {
//                    Error.hardError( "cannot extract attributes" );
//                    return null;
//                }
                return null;
            } else {
                Error.hardError( "encountered an unexpected object in iterator" + o.getClass() );
                return null;
            }
        }
    }
}
