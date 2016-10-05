package uk.ac.standrews.cs.filesystem.factories;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.filesystem.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.filesystem.sosfilesystem.SOSFileSystem;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.LOG;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.File;
import java.util.Collections;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemFactory implements IFileSystemFactory {

    private String configurationPath;
    private IGUID rootGUID;

    private InternalStorage internalStorage;

    public SOSFileSystemFactory(String configurationPath, IGUID rootGUID) {
        this.configurationPath = configurationPath;
        this.rootGUID = rootGUID;
    }

    @Override
    public IFileSystem makeFileSystem() throws FileSystemCreationException {

        LOG.log(LEVEL.INFO, "WEBDAV - Factory - Making the File System");

        Client client = getSOSClient();

        if (client != null) {
            Version rootAsset = createRoot(client);
            return new SOSFileSystem(client, rootAsset.getInvariantGUID());
        } else {
            LOG.log(LEVEL.ERROR, "WEBDAV - Unable to create file system");
            return null;
        }
    }

    private Client getSOSClient() {
        Client client = null;
        try {
            SOSConfiguration configuration = createConfiguration();
            createNodeDependencies(configuration);

            SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
            LocalNode localSOSNode = builder
                    .configuration(configuration)
                    .internalStorage(internalStorage)
                    .build();

            client = localSOSNode.getClient();
        } catch (GUIDGenerationException | SOSException e) {
            e.printStackTrace();
        }

        return client;
    }

    private SOSConfiguration createConfiguration() throws SOSConfigurationException {
        File file = new File(configurationPath);
        return new SOSConfiguration(file);
    }

    private void createNodeDependencies(SOSConfiguration configuration) throws SOSException {
        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            internalStorage = new InternalStorage(StorageFactory
                            .createStorage(storageType, root, true)); // FIXME - storage have very different behaviours if mutable or not
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

    }

    private Version createRoot(Client sos) {
        Version retval = rootExists(sos, rootGUID);

        LOG.log(LEVEL.INFO, "WEBDAV - Creating ROOT " + rootGUID + " Exist: " + (retval != null));
        if (retval == null) {
            try {

                Compound compound = sos.addCompound(CompoundType.COLLECTION, Collections.emptyList());
                retval =  sos.addVersion(new VersionBuilder(compound.getContentGUID())
                        .setInvariant(rootGUID));
            } catch (ManifestNotMadeException | ManifestPersistException e) {
                e.printStackTrace();
            }
        }

        return retval;

    }

    private Version rootExists(Client sos, IGUID root) {
        Version retval = null;
        try {
            retval = sos.getHEAD(root);
        } catch (ManifestNotFoundException e) {
            return retval;
        }
        return retval;
    }

}
