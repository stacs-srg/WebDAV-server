/*
 * Created on Jan 27, 2005 at 5:11:17 PM.
 */
package uk.ac.standrews.cs.util.test;

import junit.framework.TestCase;
import uk.ac.standrews.cs.util.ExceptionFactory;

/**
 * Test class for ExceptionFactory.
 *
 * @author graham
 */
public class ExceptionFactoryTest extends TestCase {

    public void testExceptionAutoSource() {
        
        Exception e1 = new Exception("test");
        Exception e2 = ExceptionFactory.makeLabelledException(e1);
        
        assertEquals("uk.ac.stand.dcs.asa.util.test.ExceptionFactoryTest::testExceptionAutoSource - test", e2.getMessage());
    }
}
