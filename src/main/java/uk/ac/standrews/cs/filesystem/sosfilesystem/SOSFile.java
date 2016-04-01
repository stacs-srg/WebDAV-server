package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFile implements IFile {
    @Override
    public IAttributes getAttributes() {
        return null;
    }

    @Override
    public void setAttributes(IAttributes attributes) {

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
    public void update(IData data) {

    }

    @Override
    public void append(IData data) {

    }

    @Override
    public IData reify() {
        return null;
    }

    @Override
    public void initialise(IData data, IPID pid, IGUID guid) {

    }

    @Override
    public void persist() throws PersistenceException {

    }

    @Override
    public IPID getPID() {
        return null;
    }

    @Override
    public IGUID getGUID() {
        return null;
    }
}
