package uk.ac.standrews.cs.persistence.interfaces;

import uk.ac.standrews.cs.IGUID;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface IVersionableObject extends IAttributedStatefulObject {

    void setPrevious(IGUID previous);

    Collection<IGUID> getPrevious();

    void setInvariant(IGUID guid);

    IGUID getInvariant();

}