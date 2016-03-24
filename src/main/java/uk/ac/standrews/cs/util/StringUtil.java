/*
 * Created on May 30, 2005 at 3:25:18 PM.
 */
package uk.ac.standrews.cs.util;

/**
 * @author al
 *
 * Utility class to add some Java 1.5 String Utility
 */
public class StringUtil {
    public static boolean contains( String target, String searchfor ) {
        return target.indexOf(searchfor) != -1;
    }
}
