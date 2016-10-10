package uk.ac.standrews.cs.webdav.entrypoints;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.Logger;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.filesystem.factories.SOSFileSystemFactory;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.Output;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WebDAV_SOS_Launcher extends WebDAVLauncher {

    /**
     * Creates a store and file system, and runs a WebDAV server over it.
     *
     * Usage: java WebDAV_StoreBased_Launcher -r<store root guid>
     *          [-p<port>] [-d<store root directory>]
     *          [-s<store name>] [-D]
     *          -c<configurationFilePath>
     *
     * @param args optional command line arguments
     */
    public static void main(String[] args) {
        Logger.LOG();

        String root_GUID_string = processStoreRoot(args);

        // Can't continue if no root GUID string supplied.
        if (root_GUID_string != null) {
            processDiagnostic(args);
            int port = processPort(args);
            String configFilePath = processConfigFile(args);

            try {
                IGUID root_GUID = GUIDFactory.recreateGUID(root_GUID_string);
                IFileSystem file_system =
                        new SOSFileSystemFactory(configFilePath, root_GUID)
                                .makeFileSystem();

                startWebDAVServer(file_system, port);
            } catch (FileSystemCreationException e) {
                Error.exceptionError("couldn't create file system", e);
            } catch (IOException e) {
                Error.exceptionError("socket error", e);
            } catch (GUIDGenerationException e) {
                e.printStackTrace();
            }
        } else {
            Output.getSingleton().println("Usage: java WebDAV_SOS_Launcher -r<store root guid> " +
                    "[-p<port>] [-d<store root directory>] " +
                    "[-s<store name>] [-D] " +
                    "-c<configurationFilePath>");
        }
    }

    public static void Lunch(IGUID root, int port, Client client) {

        try {
            IFileSystem file_system =
                    new SOSFileSystemFactory(root)
                            .makeFileSystem(client);

            startWebDAVServer(file_system, port);
        } catch (FileSystemCreationException e) {
            Error.exceptionError("couldn't create file system", e);
        } catch (IOException e) {
            Error.exceptionError("socket error", e);
        }

    }
}
