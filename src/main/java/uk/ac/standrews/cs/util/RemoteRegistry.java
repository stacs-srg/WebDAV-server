/*
 * Created on Feb 2, 2005 at 12:56:39 PM.
 */
package uk.ac.standrews.cs.util;

import java.net.InetSocketAddress;


/**
 * Interface for registry that provides binding service to potentially remote objects
 *
 * @author graham
 */
public interface RemoteRegistry {
    
    public abstract Object getService(InetSocketAddress hostAddress, Class interface_type) throws Exception;

    public abstract Object getService(InetSocketAddress hostAddress, Class interface_type, String service_name) throws Exception;

    public abstract Object getService(InetSocketAddress hostAddress, Class interface_type, int retry, int delay) throws Exception;
    
    public abstract Object getService(InetSocketAddress hostAddress, Class interface_type, String service_name, int retry, int delay) throws Exception;
    
    public abstract String getServiceName(Class interface_type);
}