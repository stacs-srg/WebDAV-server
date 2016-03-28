/*
 * Created on Dec 6, 2004 at 9:38:51 PM.
 */
package uk.ac.standrews.cs.util;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author graham
 *
 * Test class for util.Assert
 */
public class AssertTest extends TestCase {

    @Test
    public void testAssertion() {
        
        Assert.assertion(true, "asserting true");
    }
}
