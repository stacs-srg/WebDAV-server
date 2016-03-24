/*
 * Created on 19-Jan-2005 at 10:29:05.
 */
package uk.ac.standrews.cs.interfaces;

import java.net.InetSocketAddress;


/**
 * @author al
 */
public interface INodeFactory {
    
    public IP2PNode makeNode(InetSocketAddress hostAddress, IKey key);
}