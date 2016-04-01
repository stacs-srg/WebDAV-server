package uk.ac.standrews.cs.filesystem.factories;

import uk.ac.standrews.cs.filesystem.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.filesystem.sosfilesystem.SOSFileSystem;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.managers.Index;
import uk.ac.standrews.cs.sos.managers.LuceneIndex;
import uk.ac.standrews.cs.sos.model.implementations.SeaOfStuffImpl;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;

import java.io.IOException;

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
        } catch (IOException | KeyGenerationException | KeyLoadedException e) {
            throw new FileSystemCreationException();
        }

    }

}
