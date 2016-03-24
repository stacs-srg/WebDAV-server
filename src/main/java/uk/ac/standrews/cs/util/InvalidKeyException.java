/*
 * Created on 18-Nov-2004 at 10:36:54.
 */
package uk.ac.standrews.cs.util;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class InvalidKeyException extends Exception {

    /**
     * 
     */
    public InvalidKeyException() {
        super();
    }

    /**
     * @param arg0
     */
    public InvalidKeyException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public InvalidKeyException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public InvalidKeyException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
