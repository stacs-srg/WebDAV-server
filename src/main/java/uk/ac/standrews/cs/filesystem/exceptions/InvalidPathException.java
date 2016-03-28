/**
 * Created on Aug 18, 2005 at 1:31:17 PM.
 */
package uk.ac.standrews.cs.filesystem.exceptions;

/**
 * 
 *
 * @author graham
 */
public class InvalidPathException extends Exception {

	public InvalidPathException(String msg) {
		super(msg);
	}

	public InvalidPathException() {
		super();
	}
}
