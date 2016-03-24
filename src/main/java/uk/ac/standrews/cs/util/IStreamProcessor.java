/**
 * Created on Jul 1, 2005 at 1:23:11 PM.
 */
package uk.ac.standrews.cs.util;

/**
 * Interface implemented by objects processing output stream from remote process.
 *
 * @author graham
 * @see Processes
 */
public interface IStreamProcessor {

	/**
	 * Processes a byte output by the process.
	 * 
	 * @param byte_value the byte to be processed
	 * @return true if the byte should then be passed on for further processing
	 */
	public boolean processByte(int byte_value);
}
