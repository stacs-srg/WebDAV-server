/*
 * Created on 23-Nov-2004 at 17:22:07.
 */
package uk.ac.standrews.cs.util;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class DataUnavailableException extends Exception {

    /**
     * 
     */
    public DataUnavailableException() {
        super();
    }

    /**
     * @param arg0
     */
    public DataUnavailableException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public DataUnavailableException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public DataUnavailableException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
