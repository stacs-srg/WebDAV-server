package uk.ac.standrews.cs.filesystem.sosfilesystem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFile extends SOSFileSystemObject implements IFile {


    private AssetManifest asset;

    public SOSFile(SeaOfStuff sos, IDirectory logical_parent, String name, IData data) throws PersistenceException {
        super(logical_parent, name, data);

        // TODO - create asset or atom?


        try {
            AtomManifest atom = sos.addAtom(data.getInputStream()); // NOTE - persist only for atom, otherwise watch for appendToFile!

            GUID content = atom.getContentGUID();
            asset = sos.addAsset(content, null, null, null); // TODO - add metadata

            guid = (IGUID) content;

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

    @Override
    public IAttributes getAttributes() {
        // TODO - get these from the asset manifest

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
        // TODO - it looks like this is needed only for store based storage
    }

    @Override
    public void persist() throws PersistenceException {
        // Persistance happens via the Sea Of Stuff
        throw new NotImplementedException();
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
