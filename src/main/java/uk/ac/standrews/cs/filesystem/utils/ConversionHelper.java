package uk.ac.standrews.cs.filesystem.utils;

import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;
import uk.ac.standrews.cs.util.GUIDFactory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ConversionHelper {

    public static uk.ac.standrews.cs.utils.IGUID toSOSGUID(IGUID guid) {
        uk.ac.standrews.cs.utils.IGUID retval = null;
        try {
            retval = uk.ac.standrews.cs.utils.GUIDFactory.recreateGUID(guid.toString());
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
            // TODO - throw guid conversion exceptions
        }

        return retval;
    }

    public static IGUID toWebDAVGUID(uk.ac.standrews.cs.utils.IGUID guid) {
        IGUID retval = GUIDFactory.recreateGUID(guid.toString());

        return retval;
    }

}
