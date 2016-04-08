package uk.ac.standrews.cs.filesystem.factories;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.filesystem.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.filesystem.sosfilesystem.SOSFileSystem;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SeaOfStuffException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.SeaOfStuffImpl;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;

import java.util.Collections;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemFactory implements IFileSystemFactory {

    private String rootName;
    private IGUID rootGUID;

    public SOSFileSystemFactory(String rootName, IGUID rootGUID) {
        this.rootName = rootName;
        this.rootGUID = rootGUID;
    }

    @Override
    public IFileSystem makeFileSystem() throws FileSystemCreationException {

        try {
            SeaConfiguration.setRootName(rootName);
            SeaConfiguration configuration = SeaConfiguration.getInstance();
            Index index = LuceneIndex.getInstance(configuration);
            SeaOfStuff sos = new SeaOfStuffImpl(configuration, index);

            Asset rootAsset = createRoot(sos);

            return new SOSFileSystem(sos, rootAsset.getInvariantGUID());
        } catch (SeaOfStuffException | IndexException | SeaConfigurationException e) {
            throw new FileSystemCreationException();
        }

    }

    private Asset createRoot(SeaOfStuff sos) {

        Asset retval = null;

        retval = rootExists(sos, rootGUID);
        if (retval == null) {
            try {
                Compound compound = sos.addCompound(CompoundType.COLLECTION, Collections.emptyList());
                retval =  sos.addAsset(compound.getContentGUID(), rootGUID, null, null);
            } catch (ManifestNotMadeException | ManifestPersistException e) {
                e.printStackTrace();
            }
        }

        return retval;

    }

    private Asset rootExists(SeaOfStuff sos, IGUID root) {
        Asset retval = null;
        try {
            retval = (Asset) sos.getManifest(root);
        } catch (ManifestNotFoundException e) {
            return retval;
        }
        return retval;
    }

}
