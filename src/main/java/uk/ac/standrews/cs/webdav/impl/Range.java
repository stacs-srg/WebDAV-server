/*
 * Created on May 31, 2005 at 4:08:12 PM.
 */
package uk.ac.standrews.cs.webdav.impl;

import uk.ac.standrews.cs.util.Error;

/**
 * Represents a range - duh
 * 
 * @author al
 */
public class Range implements Comparable {

    public long start;
    public long finish;

    /**
     * @param start the start value
     * @param finish the end value
     */   public Range(long start, long finish) {
        this.start = start;
        this.finish = finish;
    }

    /**
     * @param start the start value
     * @param size the size of the range
     */
    public Range(long start, Integer size) {
        this.start = start;
        this.finish = start + size.longValue();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     */
    public int compareTo(Object o) {
        if( ! ( o instanceof Range ) ) {
            Error.hardError( "Cannot compare object with range" );
            return 0;
        }
        Range other = (Range) o;
        if( this.start == other.start ) {           // starts equal
            if( this.finish == other.finish ) {           // ends equal
                return 0;
            } else if( this.finish < other.finish ) {     
                return -1;
            } else {
                return +1;
            }
        }
        if( this.start < other.start ) {
            return -1;
        }
        else {
            return +1;
        }
    }
}
