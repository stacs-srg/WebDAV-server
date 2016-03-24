/*
 * Created on 04-Dec-2004 at 16:34:25.
 */
package uk.ac.standrews.cs.util.test;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * @author al
 *
 * A Comparitor class for comparing JChordNodeImpl objects via their keys
 */
public class BigIntComparator implements Comparator {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1) {
        BigInteger bi0 = (BigInteger)arg0;
        BigInteger bi1 = (BigInteger)arg1;
    	return (bi0.compareTo(bi1));
    }

}
