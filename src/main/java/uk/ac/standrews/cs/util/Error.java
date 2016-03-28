package uk.ac.standrews.cs.util;

/**
 * Provides support for various error message output.
 * 
 * Based on code from the com.findnearyou package (C)
 * A. Dearle & R.Connor.
 * 
 * @author al, graham
 */
public class Error {

    private static boolean localReporting = true;
    private static final Error instance = new Error();

    /********************************************************************************************************************************************/
    
    /**
     * Outputs an error message on the event bus and to standard error, if enabled.
     * 
	 * @param msg a descriptive message
     */
    public static synchronized void error(String msg) {
        
        String module = Diagnostic.getMethodInCallChain();
        
        if (module.length() > 0) errorExplicitSource(module, msg);
        else errorNoSource(msg);
    }

    /**
     * Outputs an error message to standard error, if enabled.
     * 
	 * @param msg a descriptive message
     */
    public static synchronized void errorNoSource(String msg) {
        if (localReporting) System.err.println("Error: " + msg);
    }

    /**
     * Outputs an error message to standard error, without sending on the event bus.
     * 
	 * @param msg a descriptive message
     */
    public static synchronized void errorNoEvent(String msg) {
        
        String module = Diagnostic.getMethodInCallChain();
        
        errorExplicitSourceNoEvent(module, msg);
    }
    
    /**
     * Outputs an error message to standard error regardless of whether local
     * reporting is enabled or not. The message is displayed without displaying
     * the source of the error, without the "Error:" prefix and without sending
     * on the event bus.
     * 
     * @param msg a descriptive message
     */
    public static synchronized void errorExplicitLocalReport(String msg) {
        System.err.println(msg);
    }
    
    /**
     * Outputs an error message to standard error regardless of whether local
     * reporting is enabled or not, and performs a system exit. The message is
     * displayed without displaying the source of the error, without the
     * "Error:" prefix and without sending on the event bus.
     * 
     * @param msg a descriptive message
     */
    public static synchronized void hardErrorExplicitLocalReport(String msg) {
        errorExplicitLocalReport(msg);
        hardExit();
    }

    /********************************************************************************************************************************************/
	
    /**
     * Outputs an error message, and performs a system exit.
     * 
	 * @param msg a descriptive message
     */
    public static synchronized void hardError(String msg) {
        
        String module = Diagnostic.getMethodInCallChain();
        
        if (module.length() > 0) hardErrorExplicitSource(module, msg);
        else hardErrorNoSource(msg);
    }
    
    /**
     * Outputs an error message, and performs a system exit.
     * 
	 * @param msg a descriptive message
     */
    public static synchronized void hardErrorNoSource(String msg) {
        
        errorNoSource(msg);
        hardExit();
    }

    /**
     * Outputs an error message to standard error, without sending on the event bus, and performs a system exit.
     * 
	 * @param msg a descriptive message
     */
    public static synchronized void hardErrorNoEvent(String msg) {
        
        String module = Diagnostic.getMethodInCallChain();
        System.err.println(formatError(module, msg));
        hardExit();
    }

    /********************************************************************************************************************************************/

    /**
     * Outputs details of an exception, followed by a stack trace.
     * 
	 * @param msg a descriptive message
     * @param e the exception
     */
    public static synchronized void exceptionError(String msg, Exception e) {
        
        String module = Diagnostic.getMethodInCallChain();
        errorExplicitSource(module, msg + " Exception: " + e.getMessage());       
        e.printStackTrace();
    }

    /**
     * Outputs details of an exception, followed by a stack trace, without sending on the event bus.
     * 
	 * @param msg a descriptive message
     * @param e the exception
     */
	public static synchronized void exceptionErrorNoEvent(String msg, Exception e) {
		
        String module = Diagnostic.getMethodInCallChain();
        errorExplicitSourceNoEvent(module, msg + " Exception: " + e.getMessage());       
        e.printStackTrace();
	}

    /**
     * Outputs details of an exception, followed by a stack trace, and performs a system exit.
     * 
	 * @param msg a descriptive message
     * @param e the exception
     */
    public static synchronized void hardExceptionError(String msg, Exception e) {
        
        exceptionError(msg, e);
        hardExit();    
    }

    /********************************************************************************************************************************************/

    public static void enableLocalErrorReporting() {
        
        localReporting = true;
    }

    public static void disableLocalErrorReporting() {
        
        localReporting = false;
    }

    /********************************************************************************************************************************************/
	
    private Error() {}

    /********************************************************************************************************************************************/
	
    /**
     * Outputs an error message on the event bus and to standard error, if enabled.
     * 
     * @param module the module from which this call has been made
	 * @param msg a descriptive message
     */
    private static void errorExplicitSource(String module, String msg) {
        
        errorNoSource(formatError(module, msg));
    }
    
    /**
     * Outputs an error message to standard error, without sending on the event bus.
     * 
     * @param module the module from which this call has been made
	 * @param msg a descriptive message
     */
    private static void errorExplicitSourceNoEvent(String module, String msg) {
        
        System.err.println(formatError(module, msg));
    }
    
    /**
     * Outputs an error message, and performs a system exit.
     * 
     * @param module the module from which this call has been made
	 * @param msg a descriptive message
     */
    private static void hardErrorExplicitSource(String module, String msg) {
        
        errorExplicitSource(module, msg);
        hardExit();
    }

    private static void hardExit() {
        
        System.exit(-1);
    }

    private static String formatError(String module, String msg) {
        
        return module + " : " + msg;
    }
}
