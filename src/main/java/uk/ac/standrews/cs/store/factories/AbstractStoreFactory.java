/*
 * Created on Sep 9, 2005 at 10:07:41 AM.
 */
package uk.ac.standrews.cs.store.factories;

import uk.ac.standrews.cs.store.interfaces.IGUIDStoreFactory;

/**
 * @author stuart, graham
 */
public abstract class AbstractStoreFactory implements IGUIDStoreFactory {

    protected static final String STORE_DIR_PATH_PREFIX = "STORE";
    protected static final String GUID_TO_PID_DIR_NAME = "GUID_TO_PID_DIR";
    
    protected static final String MAC_PROPERTY = "Mac OS X";
    protected static final String WINDOWS_PROPERTY = "Windows";
    protected static final String LINUX_PROPERTY = "Linux";
    
    protected static final String DEFAULT_ROOT_MAC = "";
    protected static final String DEFAULT_ROOT_WINDOWS = "C:";
    protected static final String DEFAULT_ROOT_LINUX = "~";
    protected static final String DEFAULT_ROOT_OTHER = "";
    
    protected static String defaultStoreDirectory() {
    	
        // TODO make this much nicer - hacked for OS specificity at the moment.
        // FIXME - tests write to these directories. We need to make sure that data is then deleted
    	
    	String os_name = System.getProperty("os.name");
    	
        if (os_name.equals(MAC_PROPERTY))          return DEFAULT_ROOT_MAC;
        else if (os_name.equals(WINDOWS_PROPERTY)) return DEFAULT_ROOT_WINDOWS;
        else if (os_name.equals(LINUX_PROPERTY))   return DEFAULT_ROOT_LINUX;
        else                                       return DEFAULT_ROOT_OTHER;
    }
}
