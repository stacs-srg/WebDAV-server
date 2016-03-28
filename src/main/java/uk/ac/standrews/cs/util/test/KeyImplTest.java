/*
 * Created on Dec 12, 2004 at 5:24:27 PM.
 */
package uk.ac.standrews.cs.util.test;

import junit.framework.TestCase;
import uk.ac.standrews.cs.util.KeyImpl;
import uk.ac.standrews.cs.util.SegmentArithmetic;

import java.math.BigInteger;

/**
 * Test class for KeyImpl.
 * 
 * @author graham
 */
public class KeyImplTest extends TestCase {
    
    private static KeyImpl k1 = new KeyImpl(new BigInteger("-1"));
    private static KeyImpl k2 = new KeyImpl(BigInteger.ZERO);
    private static KeyImpl k3 = new KeyImpl(BigInteger.ONE);
    private static KeyImpl k4 = new KeyImpl(new BigInteger("3247823487234"));
    private static KeyImpl k5 = new KeyImpl(SegmentArithmetic.KEYSPACE_SIZE.subtract(BigInteger.ONE));
    private static KeyImpl k6 = new KeyImpl(SegmentArithmetic.KEYSPACE_SIZE);
    private static KeyImpl k7 = new KeyImpl(SegmentArithmetic.KEYSPACE_SIZE.add(BigInteger.ONE));
    private static KeyImpl k8 = new KeyImpl(SegmentArithmetic.KEYSPACE_SIZE.add(SegmentArithmetic.KEYSPACE_SIZE.add(BigInteger.ONE)));
    private static KeyImpl k9 = new KeyImpl(new BigInteger("-1").multiply(SegmentArithmetic.KEYSPACE_SIZE.add(SegmentArithmetic.KEYSPACE_SIZE)).add(BigInteger.ONE));

    /**
     * Tests whether the key integers are as expected. All should lie in range zero to keyspace_size - 1.
     */
    public void testBigIntegerRepresentation() {
        
        // -1 should wrap to keyspace_size - 1.
        assertEquals(k1.bigIntegerRepresentation(), SegmentArithmetic.KEYSPACE_SIZE.subtract(BigInteger.ONE));
        
        // Original integers were within range, so should remain the same.
        assertEquals(k2.bigIntegerRepresentation(), BigInteger.ZERO);
        assertEquals(k3.bigIntegerRepresentation(), BigInteger.ONE);
        assertEquals(k4.bigIntegerRepresentation(), new BigInteger("3247823487234"));
        assertEquals(k5.bigIntegerRepresentation(), SegmentArithmetic.KEYSPACE_SIZE.subtract(BigInteger.ONE));
        
        // keyspace_size should wrap to 0.
        assertEquals(k6.bigIntegerRepresentation(), BigInteger.ZERO);
        
        // keyspace_size + 1 should wrap to 1.
        assertEquals(k7.bigIntegerRepresentation(), BigInteger.ONE);
        
        // 2 * keyspace_size + 1 should wrap to 1.
        assertEquals(k8.bigIntegerRepresentation(), BigInteger.ONE);
        
        // -2 * keyspace_size + 1 should wrap to 1.
        assertEquals(k9.bigIntegerRepresentation(), BigInteger.ONE);
    }

    /**
     *  Does what is says on the box
     */
    public void testhexPrefixMatch() {
        // first check a few ..
        // checks that zero and one have 39 hex digits in common        
        assertEquals( k2.baseXPrefixMatch(k3,16), 39 );
        // check that FFF..F and one have 0 digits in common
        assertEquals( k1.baseXPrefixMatch(k2,16),0 );
        // check that 2f4315d8102 and one have 1 digits in common
        assertEquals( k4.baseXPrefixMatch(k3,16),29 );
        // next backwards ..
        assertEquals( k3.baseXPrefixMatch(k2,16), 39 );
        // check that FFF..F and one have 0 digits in common
        assertEquals( k2.baseXPrefixMatch(k1,16),0 );
        // check that 2f4315d8102 and one have 1 digits in common
        assertEquals( k3.baseXPrefixMatch(k4,16),29 ); 
        // thats enough for me - al :)
    }
    
    /**
     * Tests whether the string representations are as expected. Should contain hex integers in range zero to keyspace size - 1.
     */
    public void testToString() {
        
        assertEquals(k1.toString(), "ffffffffffffffffffffffffffffffffffffffff");
        assertEquals(k2.toString(), "0000000000000000000000000000000000000000");
        assertEquals(k3.toString(), "0000000000000000000000000000000000000001");
        assertEquals(k4.toString(), "000000000000000000000000000002f4315d8102");
        assertEquals(k5.toString(), "ffffffffffffffffffffffffffffffffffffffff");
        assertEquals(k6.toString(), "0000000000000000000000000000000000000000");
        assertEquals(k7.toString(), "0000000000000000000000000000000000000001");
        assertEquals(k8.toString(), "0000000000000000000000000000000000000001");
        assertEquals(k9.toString(), "0000000000000000000000000000000000000001");
    }

    /**
     * Tests whether the bit lengths of the keys are as expected.
     */
    public void testBitLength() {

        assertEquals(k1.bitLength(), 160);
        assertEquals(k2.bitLength(), 0);
        assertEquals(k3.bitLength(), 1);
        assertEquals(k4.bitLength(), 42);
        assertEquals(k5.bitLength(), 160);
        assertEquals(k6.bitLength(), 0);
        assertEquals(k7.bitLength(), 1);
        assertEquals(k8.bitLength(), 1);
        assertEquals(k9.bitLength(), 1);
    }

    /**
     * Tests whether key comparison works as expected.
     */
    public void testCompareTo() {
        
        // k1 is the largest possible key, keyspace_size - 1.
        assertEquals(k1.compareTo(k2), 1);
        assertEquals(k1.compareTo(k3), 1);
        assertEquals(k1.compareTo(k4), 1);
        
        // k1 = k5.
        assertEquals(k1.compareTo(k5), 0);
        
        // k2 is the smallest possible key, zero.
        assertEquals(k2.compareTo(k1), -1);
        assertEquals(k2.compareTo(k3), -1);
        assertEquals(k2.compareTo(k4), -1);
        
        // k2 = k6.
        assertEquals(k2.compareTo(k6), 0);
        
        // Miscellaneous pairs.
        assertEquals(k4.compareTo(k1), -1);
        assertEquals(k4.compareTo(k2), 1);
        assertEquals(k4.compareTo(k3), 1);
        assertEquals(k4.compareTo(k4), 0);
    }

    /**
     * Tests whether ring distance calculation works as expected.
     */
    public void testRingDistanceTo() {
        
        // Distances clockwise from keyspace_size - 1.
        assertEquals(k1.ringDistanceTo(k2), BigInteger.ONE);
        assertEquals(k1.ringDistanceTo(k3), SegmentArithmetic.TWO);
        assertEquals(k1.ringDistanceTo(k4), new BigInteger("3247823487235"));
        
        // k1 = k5.
        assertEquals(k1.ringDistanceTo(k5), BigInteger.ZERO);
        //assertEquals(k1.ringDistanceTo(k5), SegmentArithmetic.KEYSPACE_SIZE);
        
        // k2 is the smallest possible key, zero.
        assertEquals(k2.ringDistanceTo(k1), SegmentArithmetic.KEYSPACE_SIZE.subtract(BigInteger.ONE));
        assertEquals(k2.ringDistanceTo(k3), BigInteger.ONE);
        assertEquals(k2.ringDistanceTo(k4), new BigInteger("3247823487234"));
        
        // k2 = k6.
        assertEquals(k2.ringDistanceTo(k6), BigInteger.ZERO);
        //assertEquals(k2.ringDistanceTo(k6), SegmentArithmetic.KEYSPACE_SIZE);
        
        // Miscellaneous pairs.
        assertEquals(k4.ringDistanceTo(k1), new BigInteger("1461501637330902918203684832716283016408109055741"));
        assertEquals(k4.ringDistanceTo(k2), new BigInteger("1461501637330902918203684832716283016408109055742"));
        assertEquals(k4.ringDistanceTo(k3), new BigInteger("1461501637330902918203684832716283016408109055743"));
        assertEquals(k4.ringDistanceTo(k4), BigInteger.ZERO);
        //assertEquals(k4.ringDistanceTo(k4), SegmentArithmetic.KEYSPACE_SIZE);
    }

    /**
     * Tests whether ring ordering calculation works as expected.
     */
    public void testFirstCloserThanSecond() {

        assertTrue(k1.firstCloserInRingThanSecond(k2, k3));
        assertTrue(k1.firstCloserInRingThanSecond(k3, k4));
        assertTrue(k1.firstCloserInRingThanSecond(k2, k4));
        assertTrue(k1.firstCloserInRingThanSecond(k1, k3));
        
        assertFalse(k1.firstCloserInRingThanSecond(k3, k2));
        assertFalse(k1.firstCloserInRingThanSecond(k4, k3));
        assertFalse(k1.firstCloserInRingThanSecond(k4, k2));
        assertFalse(k1.firstCloserInRingThanSecond(k1, k1));
        assertFalse(k1.firstCloserInRingThanSecond(k2, k2));

        assertTrue(k2.firstCloserInRingThanSecond(k3, k4));
        assertTrue(k2.firstCloserInRingThanSecond(k4, k1));
        assertTrue(k2.firstCloserInRingThanSecond(k3, k1));
        assertTrue(k2.firstCloserInRingThanSecond(k2, k3));

        assertFalse(k2.firstCloserInRingThanSecond(k4, k3));
        assertFalse(k2.firstCloserInRingThanSecond(k1, k4));
        assertFalse(k2.firstCloserInRingThanSecond(k1, k3));
        assertFalse(k2.firstCloserInRingThanSecond(k2, k2));
        assertFalse(k2.firstCloserInRingThanSecond(k3, k3));

        assertTrue(k4.firstCloserInRingThanSecond(k1, k2));
        assertTrue(k4.firstCloserInRingThanSecond(k2, k3));
        assertTrue(k4.firstCloserInRingThanSecond(k1, k3));
        assertTrue(k4.firstCloserInRingThanSecond(k4, k3));

        assertFalse(k4.firstCloserInRingThanSecond(k2, k1));
        assertFalse(k4.firstCloserInRingThanSecond(k3, k2));
        assertFalse(k4.firstCloserInRingThanSecond(k3, k1));
        assertFalse(k4.firstCloserInRingThanSecond(k4, k4));
        assertFalse(k4.firstCloserInRingThanSecond(k3, k3));
    }
}
