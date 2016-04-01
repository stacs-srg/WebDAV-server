package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.persistence.impl.AttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemObject extends AttributedStatefulObject implements IAttributedStatefulObject {

    public SOSFileSystemObject(IAttributes atts) {
        super(atts);
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
