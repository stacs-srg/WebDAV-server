package uk.ac.standrews.cs.interfaces;

import java.math.BigInteger;

/**
 * Interface defining keys.
 *
 * @author sja7, al, stuart, graham
 */
public interface IKey extends Comparable {
    
    /**
     * @return a BigInteger representation of this key
     */
    BigInteger bigIntegerRepresentation();
    
    /**
     * @return a string representation of this key
     */
    String toString();
    
    String toString(int radix);
    
    /**
     * @param other the other key to compare
     * @return the number of characters of shared prefix with this key in base X
     */
    int baseXPrefixMatch(IKey other, int radix);

    /**
     * @param index a legal index into the key
     * @return the character at that index, or '!' if the index is illegal
     */
    // public char hexCharAtIndex( int index );
    char charAtIndexBaseX(int index, int radix);
    
    /**
     * Returns a string representing the fraction of the key space represented by this key.
     * 
     * @return a string representing the fraction of the key space represented by this key
     */
    String toStringAsKeyspaceFraction();
    
    /**
     * @return the number of bits in the minimal two's-complement representation of this key, excluding sign bit
     */
    int bitLength();
    
    /**
     * @return the number of bits in the minimal two's-complement representation of the biggest key possible
     */
    int maxBitLength();
    
    /**
     * Calculates the distance from this key to 'end', moving clockwise around the ring.
     * 
     * @param end a key to which the distance is to be calculated
     * @return the ring distance between this key and 'end'
     */
    BigInteger ringDistanceTo(IKey end);

    /**
     * Determines whether 'first' is closer to this key than 'second' in absolute terms.
     * 
     * @param first the first key to be compared
     * @param second the second key to be compared
     * @return true if 'first' is closer in ring distance to this key than 'second'
     */
    boolean firstNumericallyCloserThanSecond(IKey first, IKey second);
    
    /**
     * Determines whether 'first' is closer to this key than 'second', moving clockwise round the ring.
     * 
     * @param first the first key to be compared
     * @param second the second key to be compared
     * @return true if 'first' is closer in ring distance to this key than 'second'
     */
    boolean firstCloserInRingThanSecond(IKey first, IKey second);
    
}
