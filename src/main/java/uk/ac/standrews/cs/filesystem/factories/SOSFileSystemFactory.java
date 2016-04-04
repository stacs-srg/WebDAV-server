package uk.ac.standrews.cs.filesystem.factories;

import uk.ac.standrews.cs.filesystem.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.filesystem.sosfilesystem.SOSFileSystem;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SeaOfStuffException;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.SeaOfStuffImpl;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemFactory implements IFileSystemFactory {

    private String rootName;

    public SOSFileSystemFactory(String rootName) {
        this.rootName = rootName;
    }

    @Override
    public IFileSystem makeFileSystem() throws FileSystemCreationException {

        try {
            SeaConfiguration.setRootName(rootName);
            SeaConfiguration configuration = SeaConfiguration.getInstance();
            Index index = LuceneIndex.getInstance(configuration);
            SeaOfStuff sos = new SeaOfStuffImpl(configuration, index);

            return new SOSFileSystem(sos);
        } catch (SeaOfStuffException | IndexException | SeaConfigurationException e) {
            throw new FileSystemCreationException();
        }

    }

}
