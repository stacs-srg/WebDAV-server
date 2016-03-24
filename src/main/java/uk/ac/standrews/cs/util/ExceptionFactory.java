/*
 * Created on Jan 27, 2005 at 5:00:39 PM.
 */
package uk.ac.standrews.cs.util;

/**
 * @author graham
 */
public class ExceptionFactory {

    public static Exception makeLabelledException(final Exception e) {
        
        final String caller_name = Diagnostic.getMethodInCallChain(2);
        
        return new Exception() {
            
            public String getMessage() {
                return caller_name + " - " + e.getMessage();
            }
            
            public StackTraceElement[] getStackTrace() {
                return e.getStackTrace();
            }
            
            public void printStackTrace() {
                e.printStackTrace();
            }
        };
    }
    
    public static Exception makeLabelledException(String message) {
        
        String caller_name = Diagnostic.getMethodInCallChain(2);
        
        return new Exception(caller_name + " - " + message);
    }
}
