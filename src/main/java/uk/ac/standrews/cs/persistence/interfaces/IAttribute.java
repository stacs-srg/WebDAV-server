package uk.ac.standrews.cs.persistence.interfaces;

/**
 * An attribute comprising a string key-value pair.
 *
 * @author graham
 */
public interface IAttribute {

	/**
	 * Returns the name of the attribute.
	 * 
	 * @return the name of the attribute
	 */
	String getName();

	/**
	 * Returns the value of the attribute.
	 * 
	 * @return the value of the attribute
	 */
	String getValue();
}