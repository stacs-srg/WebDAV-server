/*
 * Created on Dec 15, 2004 at 10:56:29 PM.
 */
package uk.ac.standrews.cs.util.test;

import junit.framework.TestCase;
import uk.ac.standrews.cs.interfaces.IKey;
import uk.ac.standrews.cs.util.SHA1KeyFactory;

import java.net.InetSocketAddress;

/**
 * Test class for KeyFactory.
 *
 * @author graham
 */
public class KeyFactoryTest extends TestCase {

    /*
     * Class under test for Key generateKey()
     */
    public void testGenerateKey() {
        
        IKey k1 = SHA1KeyFactory.generateKey();
        IKey k2 = SHA1KeyFactory.generateKey();
    	
        // Key generated by SHA1 from string "null".
        assertEquals("2be88ca4242c76e8253ac62474851065032d6833", k1.toString());

        // Subsequent calls should return equal but non-identical keys.
        assertNotSame(k1, k2);
        assertEquals(0, k1.compareTo(k2));
    }

    /*
     * Class under test for Key generateKey(String)
     */
    public void testGenerateKeyWithString() {
        
        IKey k1 = SHA1KeyFactory.generateKey("null");
        IKey k2 = SHA1KeyFactory.generateKey("quick brown fox");
        IKey k3 = SHA1KeyFactory.generateKey("quick brown fox");
    	
        // Key generated by SHA1 from string "null".
        assertEquals("2be88ca4242c76e8253ac62474851065032d6833", k1.toString());

        // Key generated by SHA1 from string "quick brown fox".
        assertEquals("a9762606f9e33e452f06b4562e253efb6038b512", k2.toString());
        
        // Subsequent calls should return equal keys.
        assertTrue(k2.compareTo(k3) == 0);
        assertFalse(k1.compareTo(k2) == 0);
    }

    /*
     * Class under test for Key generateKey(INetSocketAddress)
     */
    public void testGenerateKeyWithIPAddress() {
        IKey k1 = SHA1KeyFactory.generateKey(new InetSocketAddress("138.251.195.25", 80));
        IKey k2 = SHA1KeyFactory.generateKey(new InetSocketAddress("212.58.224.56", 1024));

        // Keys generated by SHA1 from addresses above.
        assertEquals("195e24370036a4062b0d325c03153a2b9fc9c92e", k1.toString());
        assertEquals("6280b63d4e1f488235bce11c30c6d3130141fc07", k2.toString());
    }
}
