/*
 * Created on 10-Jan-2005
 */
package uk.ac.standrews.cs.interfaces;

import java.net.InetAddress;

/**
 * @author stuart
 */
public interface IDistanceCalculator {
    
	/**
	 * Calculates the distance between two nodes.
	 * 
	 * @param address1 the IP address for the first node which we are calculating distance between
	 * @param address2  the IP address for the second node which we are calculating distance between
	 * @return the normalised (wrt number of nodes) distance between the two nodes in real space
	 * or a negative value if the distance between the two specified node cannot be calculated.
	 */
	public double distance(InetAddress address1, InetAddress address2);
}