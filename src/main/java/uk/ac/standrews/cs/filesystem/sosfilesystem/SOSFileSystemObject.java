package uk.ac.standrews.cs.filesystem.sosfilesystem;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.persistence.impl.AttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.persistence.interfaces.IVersionableObject;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemObject extends AttributedStatefulObject implements IVersionableObject {

    protected String name;
    protected SeaOfStuff sos;

    private IGUID invariant;
    private IGUID previous; // TODO - this should be a collection

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

    @Override
    public void setPrevious(IGUID previous) {

    }

    @Override
    public Collection<IGUID> getPrevious() {
        Collection<IGUID> retval =  new ArrayList<IGUID>();
        retval.add(previous);
        return retval;
    }

    @Override
    public void setInvariant(IGUID guid) {
        this.invariant = guid;
    }

    @Override
    public IGUID getInvariant() {
        return invariant;
    }
}
