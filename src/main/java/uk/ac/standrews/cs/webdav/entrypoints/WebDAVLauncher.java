package uk.ac.standrews.cs.webdav.entrypoints;

import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.util.CommandLineArgs;
import uk.ac.standrews.cs.util.CommandLineInput;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Output;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class WebDAVLauncher {

    protected static String processStoreRoot(String[] args) {
        return CommandLineArgs.getArg(args, "-r");
    }

    protected static String processDirectoryRoot(String[] args) {
        return CommandLineArgs.getArg(args, "-d");
    }

    protected static void processDiagnostic(String[] args) {
        // Read diagnostic level from the console if not already specified in command line argument.
        Diagnostic.setLevel(Diagnostic.FULL);

        if (CommandLineArgs.getArg(args, "-D") == null) {

            Output.getSingleton().print("Enter D<return> for diagnostics, anything else for no diagnostics: ");
            String input = CommandLineInput.readLine();

            if (! input.equalsIgnoreCase("D"))
                Diagnostic.setLevel(Diagnostic.NONE);
        }
    }

    protected static int processPort(String[] args) {
        int port = 0;
        String port_string = CommandLineArgs.getArg(args, "-p");
        if (port_string != null) {
            port = Integer.parseInt(port_string);
        }

        return port;
    }

    protected static String processConfigFile(String[] args) {
        return CommandLineArgs.getArg(args, "-c");
    }

    protected static void startWebDAVServer(IFileSystem file_system, int port) throws IOException {
        WebDAVServer server;
        if (port == 0) {
            server = new WebDAVServer(file_system);
        } else {
            server = new WebDAVServer(file_system, port);
        }

        server.run();
    }
}
