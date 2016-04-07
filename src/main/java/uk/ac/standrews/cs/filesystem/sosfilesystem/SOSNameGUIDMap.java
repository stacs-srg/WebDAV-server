package uk.ac.standrews.cs.filesystem.sosfilesystem; // TODO - move to appropriate package

import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.store.interfaces.INameGUIDMap;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.KeyImpl;

import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNameGUIDMap extends Properties implements INameGUIDMap {

    private static final String SEPARATOR = "|";	// used to separate GUIDs and attributes
    private static final String SPACE = " ";

    @Override
    public IGUID get(String name) {
        String result = (String) super.get(name);

        if (result == null) {
            return null;
        } else {
            return extractGUID(result);
        }
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
        if (containsKey(name)) {
            throw new BindingPresentException( "name: " + name + " already exists in directory");
        }

        Diagnostic.trace( "putting entry with name = " + name + " guid = " + guid.toString(), Diagnostic.RUN );

        super.put(name, guid.toString() + SEPARATOR + SPACE );
    }

    /**
     * @return the GUID from a properties entry - separated using SEPARATOR
     */
    private IGUID extractGUID( String text ) {
        StringTokenizer st = new StringTokenizer(text, SEPARATOR);
        if (st.countTokens() != 2) {
            Error.hardError( "internal inconsistency in mapping, found " + st.countTokens() + " expected 2" );
            // not reached
            return null;
        } else {
            return new KeyImpl(st.nextToken()); // first token is the GUID
        }
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
