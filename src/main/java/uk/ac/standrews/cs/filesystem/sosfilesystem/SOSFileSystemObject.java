package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.persistence.impl.AttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemObject extends AttributedStatefulObject implements IAttributedStatefulObject {

    protected String name;
    protected SeaOfStuff sos;

    public SOSFileSystemObject(SeaOfStuff sos) {
        super(null);
        this.sos = sos;
    }


    public SOSFileSystemObject(SeaOfStuff sos, IData data) {
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
    public void persist() throws PersistenceException {

    }
}
