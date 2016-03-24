/*
 * Created on Jan 28, 2005 at 2:50:07 PM.
 */
package uk.ac.standrews.cs.interfaces;

import java.net.InetSocketAddress;


/**
 * Interface defining failure suspectors.
 *
 * @author graham
 */
public interface IFailureSuspector {

    public boolean hasFailed(IP2PNode node);

    public boolean hasFailed(InetSocketAddress rep);
}
