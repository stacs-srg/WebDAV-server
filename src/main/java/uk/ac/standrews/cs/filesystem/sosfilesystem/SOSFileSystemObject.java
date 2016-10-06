package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.persistence.impl.AttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.persistence.interfaces.IVersionableObject;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemObject extends AttributedStatefulObject implements IVersionableObject {

    protected Client sos;

    protected String name;
    protected Version version;
    protected SOSDirectory parent;

    public SOSFileSystemObject(Client sos) {
        super(null);
        this.sos = sos;
    }


    public SOSFileSystemObject(Client sos, IData data) {
        super(data, null);
        this.sos = sos;
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
    public void persist() throws PersistenceException {}

    @Override
    public Collection<IGUID> getPrevious() {
        return version.getPreviousVersions();
    }

    @Override
    public IGUID getInvariant() {
        return version.getInvariantGUID();
    }

    @Override
    public Version getVersion() {
        return version;
    }
}
