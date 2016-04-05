package uk.ac.standrews.cs.filesystem.sosfilesystem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.GUIDFactory;
import uk.ac.standrews.cs.utils.IGUID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFile extends SOSFileSystemObject implements IFile {

    private Asset asset;
    private SeaOfStuff sos;

    boolean isCompoundData;
    private Collection<Content> atoms;

    public SOSFile(SeaOfStuff sos, IDirectory logical_parent, String name, IData data) throws PersistenceException {
        super(logical_parent, name, data);
        isCompoundData = false;

        // TODO - what does it mean to have a directory here? is this a pointer to a compound - collection?
        // then we would have to update the compound too

        this.sos = sos;

        try {
            Atom atom = sos.addAtom(data.getInputStream()); // TODO - persist only for atom, otherwise watch for appendToFile!

            IGUID content = atom.getContentGUID();
            asset = sos.addAsset(content, null, null, null); // TODO - add metadata

            guid = GUIDFactory.recreateGUID(content.toString());
        } catch (DataStorageException e) {
            e.printStackTrace();
        } catch (ManifestPersistException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ManifestNotMadeException e) {
            e.printStackTrace();
        }

    }

    public SOSFile(SeaOfStuff sos, IDirectory logical_parent, String name) {
        super(logical_parent, name);
        isCompoundData = true;
        atoms = new ArrayList<>();

        this.sos = sos;
    }

    @Override
    public IAttributes getAttributes() {
        Collection<IGUID> metadata = asset.getMetadata();

        // TODO - iterate over metadata and build attributes

        return null;
    }

    @Override
    public void setAttributes(IAttributes attributes) {
        // This will allow to explitly set new attributes, resulting in a new version of the data
        // or we could have the data pointing an asset, so the file does not need to change as the metadata does
        Error.hardError("unimplemented method");
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
        // TODO - this should result in a new version pointing to a previous one

        throw new NotImplementedException();
    }

    @Override
    public void append(IData data) {
        if (! isCompoundData)
            return;

        try {
            Atom atom = sos.addAtom(data.getInputStream());
            IGUID guid = atom.getContentGUID();
            Content content = new Content(guid);
            atoms.add(content);

        } catch (DataStorageException | ManifestPersistException | IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void persist() throws PersistenceException {
        if (! isCompoundData)
            return;

        try {
            Compound compound = sos.addCompound(CompoundType.DATA, atoms);
            asset = sos.addAsset(compound.getContentGUID(), null, null, null); // TODO - add metadata

            guid = GUIDFactory.recreateGUID(compound.getContentGUID().toString());

        } catch (ManifestNotMadeException e) {
            e.printStackTrace();
        } catch (ManifestPersistException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IData reify() {
        // TODO - look for guid and return idata

        return null;
    }
}
