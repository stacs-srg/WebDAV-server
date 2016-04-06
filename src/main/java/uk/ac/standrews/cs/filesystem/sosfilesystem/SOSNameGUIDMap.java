package uk.ac.standrews.cs.filesystem.sosfilesystem; // TODO - move to appropriate package

import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.interfaces.INameGUIDMap;

import java.util.Iterator;
import java.util.Properties;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNameGUIDMap extends Properties implements INameGUIDMap {

    @Override
    public IGUID get(String name) {
        return null;
    }

    @Override
    public IAttributes getAttributes(String name) throws BindingAbsentException {
        return null;
    }

    @Override
    public void setAttributes(String name, IAttributes atts) throws BindingAbsentException {

    }

    @Override
    public void put(String name, IGUID guid) throws BindingPresentException {

    }

    @Override
    public void delete(String name) throws BindingAbsentException {

    }

    @Override
    public void rename(String old_name, String new_name) throws BindingAbsentException, BindingPresentException {

    }

    @Override
    public Iterator iterator() {
        return null;
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
