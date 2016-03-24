package uk.ac.standrews.cs.util;

import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IKey;
import uk.ac.standrews.cs.interfaces.IPID;

import java.math.BigInteger;

/**
 * Implementation of key.
 * 
 * @author stuart, al, graham, sja7
 */
public class KeyImpl implements IPID, IGUID {

    //********************** Constants ***********************
    
    public static int keylength = 160;
    
    /**
     * The radix used in converting the key's value to a string.
     */
    public static final int DEFAULT_TO_STRING_RADIX = 16;
    
    /**
     * The length of the key's value in digits.
     */
    public static final int DEFAULT_TO_STRING_LENGTH = 40;

    //************************ Fields ************************

    public BigInteger key_value;

    //********************* Constructors *********************
    
    /**
     * Default constructor for use in deserialization.
     */
    public KeyImpl () {
    	/* This constructor deliberately blank... */
    }
    
    /**
     * Creates a new key using the given value modulo the key space size.
     * 
     * @param key_value the value of the key
     */
    public KeyImpl(BigInteger key_value) {
        try {
            this.key_value = key_value.remainder(SegmentArithmetic.KEYSPACE_SIZE);
            
            // Allow for negative key value.
            if (this.key_value.compareTo(BigInteger.ZERO) < 0)
                this.key_value = this.key_value.add(SegmentArithmetic.KEYSPACE_SIZE);
            
        } catch (Exception e) {
            Error.exceptionError("error in constructing key from big integer " + key_value, e);
        }
    }

    /**
     * Creates a new key using a string representation of a BigInteger to base DEFAULT_TO_STRING_RADIX.
     *
     * @param s the string value of the key
     * @see #DEFAULT_TO_STRING_RADIX
     */
    public KeyImpl(String s) {
        this(new BigInteger(s, DEFAULT_TO_STRING_RADIX));
    }

    //*********************** Key Methods ************************

    /**
     * Returns the representation of this key.
     *
     * @return the representation of this key
     */
    public BigInteger bigIntegerRepresentation() {

        return key_value;
    }

    /**
     * Returns a string representation of the key value.
     *
     * @return a string representation of the key value using the default radix and length
     */
    public String toString() {

        return toString(DEFAULT_TO_STRING_RADIX, DEFAULT_TO_STRING_LENGTH);
    }

    /**
     * Returns a string representation of the key value.
     *
     * @param radix the radix
     * @return a string representation of the key value using the given radix
     */
    public String toString(int radix) {

        int bits_per_digit = RadixMethods.bitsNeededTORepresent(radix);
        int toStringLength = keylength / bits_per_digit;

        return toString(radix, toStringLength);
    }

    /**
     * Returns a string representation of the key value.
     *
     * @param radix the radix
     * @param stringLength the length to which the key representation should be padded
     * @return a string representation of the key value using the given radix
     */
    public String toString(int radix, int stringLength) {

        StringBuffer result = new StringBuffer(key_value.toString(radix));
        while (result.length() < stringLength) result.insert(0, '0');
        return result.toString();
    }

    /**
     * Returns a string representing the fraction of the key space represented by this key.
     *
     * @return a string representing the fraction of the key space represented by this key
     */
    public String toStringAsKeyspaceFraction() {

        return key_value.multiply(new BigInteger("100")).divide(SegmentArithmetic.KEYSPACE_SIZE) + "%";
    }

    public int baseXPrefixMatch(IKey other, int radix ) {
        String thisChar = this.toString(radix);
        String otherChar = ((KeyImpl) other).toString(radix); // TODO assumes other uses same impl - BAD
        int matching = 0;
        while( matching < thisChar.length() && thisChar.charAt(matching) == otherChar.charAt(matching) ) {
            matching++;
        }
        return matching;
    }

    public char charAtIndexBaseX( int index, int radix ) {

        String thisChar = this.toString(radix);

        if( index >= 0 && index < thisChar.length() ) {
            return thisChar.charAt(index);
        }
        else {
            Error.error("Error extracting hex characters from key ");
            return '!';
        }
    }

    /**
     * Returns the bit length of the key.
     *
     * @return the number of bits in the minimal two's-complement representation of the key value, excluding a sign bit.
     */
    public int bitLength() {

        return key_value.bitLength();
    }

    /**
     * @return the number of bits in the minimal two's-complement representation of the biggest key possible
     */
    public int maxBitLength() {
        return keylength;
    }

    /**
     * Compares this key with another.
     *
     * @param o the key to compare
     * @return -1, 0, or 1 if the argument key is greater, equal to, or less
     *         than this node, respectively
     */
    public int compareTo(Object o) {
        try {
            IKey k = (IKey) o;

            return key_value.compareTo(k.bigIntegerRepresentation());

        } catch (ClassCastException e) {

            Error.exceptionError("ClassCastException: " + o.getClass().getName(), e);
            return 0;
        }
    }

    /**
     * Compares this key with another.
     *
     * @param o the key to compare
     * @return true if the argument key's representation is equal to that of this node
     */
    public boolean equals(Object o) {

        try {
            IKey k = (IKey) o;

            return key_value.equals(k.bigIntegerRepresentation());

        } catch (ClassCastException e) {

            Error.exceptionError("ClassCastException: " + o.getClass().getName(), e);
            return false;
        }
    }
    
    // TODO should the following methods be moved to SegmentArithmetic?
   
    /**
     * Calculates the distance from this key to 'end', moving clockwise around the ring.
     * 
     * @param end a key to which the distance is to be calculated
     * @return the ring distance between this key and 'end'
     */
    public BigInteger ringDistanceTo(IKey end) {
        
        BigInteger distance = end.bigIntegerRepresentation().subtract(key_value);
        
        // Check for ring wrap-around, indicated by negative distance.
        // TODO should test be <=? Should distance from key to itself be 0 or full ring?
        if (distance.compareTo(BigInteger.ZERO) < 0) {
            
            // The segment wraps around, so add the keyspace size.
            return distance.add(SegmentArithmetic.KEYSPACE_SIZE);
        }
        
        // No wrap-around.
        return distance;
    }
    
    public boolean firstNumericallyCloserThanSecond(IKey first, IKey second) {
        BigInteger dToFirst = this.key_value.subtract(first.bigIntegerRepresentation()).abs();
        BigInteger dToSecond = this.key_value.subtract(second.bigIntegerRepresentation()).abs();
        return dToFirst.compareTo(dToSecond) < 0;
    }
    
    /**
     * Determines whether 'first' is closer to this key than 'second',
     * moving clockwise round the ring.
     * 
     * @param first the first key to be compared
     * @param second the second key to be compared
     * @return true if 'first' is closer in ring distance to this key than 'second'
     */
    public boolean firstCloserInRingThanSecond(IKey first, IKey second) {      
        BigInteger distanceToFirst = ringDistanceTo(first);
        BigInteger distanceToSecond = ringDistanceTo(second);
        
        return distanceToFirst.compareTo(distanceToSecond) < 0;
    }
    
    public int hashCode(){
        return toString().hashCode();
    }
}