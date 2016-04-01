package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.persistence.impl.AttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemObject extends AttributedStatefulObject implements IAttributedStatefulObject {

    protected String name;
    protected IDirectory logical_parent;

    public SOSFileSystemObject() {
        super(null);
        this.name = "";
    }

    public SOSFileSystemObject(IDirectory logical_parent, String name) {
        super(null);
        this.name = name;
        this.logical_parent = logical_parent;
    }

    public SOSFileSystemObject(IDirectory logical_parent, String name, IData data) {
        super(data, null);
        this.name = name;
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
