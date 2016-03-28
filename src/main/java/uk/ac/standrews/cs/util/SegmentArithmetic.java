/*
 * Created on 08-Dec-2004
 *
 */
package uk.ac.standrews.cs.util;

import java.math.BigInteger;

/**
 * Implementation of segment/ring arithmetic.
 * 
 * @author stuart, al, graham
 */
public class SegmentArithmetic {

    public static final int KEY_LENGTH = 160; // TODO - this used to be the keylength for jChord and was retrieved from a file

    public static final BigInteger TWO = BigInteger.ONE.add(BigInteger.ONE);

    public static final BigInteger KEYSPACE_SIZE = TWO.pow(KEY_LENGTH);

}
