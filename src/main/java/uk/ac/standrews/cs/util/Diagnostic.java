package uk.ac.standrews.cs.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Provides support for various diagnostic output.</p>
 * 
 * <p>A global threshold diagnostic level may be set by the user; the default value
 * is NONE, the highest level. Each call to produce diagnostic output is
 * parameterised by a diagnostic level. The output is only actually generated if
 * the given level is higher than or equal to the current global threshold
 * level. For example, if the global threshold is set to FULL then all output
 * will be generated, while if the global threshold is set to NONE then only
 * calls that also specify the level NONE will produce output.</p>
 * 
 * <p>The ordering of levels is FULL < INIT < RUN < RUNALL < RESULT < FINAL < NONE.</p>
 * 
 * @author al, graham
 */
public class Diagnostic {

    /**
     * The lowest diagnostic level.
     */
    public static Diagnostic FULL =   new Diagnostic(0, "FULL");

    /**
     * An intermediate diagnostic level.
     */
    public static Diagnostic INIT =   new Diagnostic(1, "INIT");

    /**
     * An intermediate diagnostic level.
     */
    public static Diagnostic RUN =    new Diagnostic(2, "RUN");

    /**
     * An intermediate diagnostic level.
     */
    public static Diagnostic RUNALL = new Diagnostic(3, "RUNALL");

    /**
     * An intermediate diagnostic level.
     */
    public static Diagnostic RESULT = new Diagnostic(4, "RESULT");

    /**
     * An intermediate diagnostic level.
     */
    public static Diagnostic FINAL =  new Diagnostic(5, "FINAL");

    /**
     * The highest diagnostic level.
     */
    public static Diagnostic NONE =   new Diagnostic(6, "NONE");

    /********************************************************************************************************************************************/
	
    private static boolean local_reporting = true;
    private static Diagnostic threshold;
    private static final Diagnostic instance = new Diagnostic();
    private static final String DIAGNOSTIC_CLASS_NAME = Diagnostic.class.getName();
    private static final String ERROR_CLASS_NAME = Error.class.getName();

    private final int level_value;
    private final String level_description;

    /**
     * Sets the global threshold diagnostic level.
     *
     * @param level the new level
     */
    public static void setLevel(Diagnostic level) {

        threshold = level;
    }

    /**
     * Gets the current global threshold diagnostic level.
     *
     * @return the current level
     */
    public static Diagnostic getLevel() {

        return threshold;
    }

    /**
     * Tests the current reporting threshold.
     *
     * @param level a reporting level
     * @return true if the given level is greater than or equal to the current reporting threshold
     */
    public static boolean aboveTraceThreshold(Diagnostic level) {

        return level.level_value >= threshold.level_value;
    }

    /********************************************************************************************************************************************/

    /**
     * Outputs trace information if the specified level is equal or higher to the current reporting threshold.
     *
     * @param level the trace level
     */
    public static void trace(Diagnostic level) {

        outputTrace(getMethodInCallChain(), level, true);
    }

	/**
     * Outputs trace information.
     *
	 * @param msg a descriptive message
	 */
	public static void trace(String msg) {

	    outputTrace(getMethodInCallChain() + " : " + msg, true);
	}

	/**
     * Outputs trace information if the specified level is equal or higher to the current reporting threshold.
     *
	 * @param msg a descriptive message
	 * @param level the trace level
	 */
	public static void trace(String msg, Diagnostic level) {

	    outputTrace(getMethodInCallChain() + " : " + msg, level, true);
	}

	/**
     * Outputs trace information if the specified level is equal or higher to the current reporting threshold.
     *
	 * @param msg1 a descriptive message
	 * @param msg2 another message
	 * @param level the trace level
	 */
	public static void trace(String msg1, String msg2, Diagnostic level) {

        outputTrace(getMethodInCallChain() + " : " + msg1 + msg2, level, true);
	}

    /**
     * Outputs trace information if the specified level is equal or higher to the current reporting threshold,
     * without a trailing newline.
     *
     * @param module the module from which this call has been made
     * @param level the trace level
     */
    public static void traceNoLn(String module, Diagnostic level) {

        outputTrace(module, level, false);
    }

    /**
     * Outputs trace information if the specified level is equal or higher to the current reporting threshold,
     * without including the source location.
     *
	 * @param msg a descriptive message
     * @param level the trace level
     */
    public static void traceNoSource(String msg, Diagnostic level) {

        outputTrace(msg, level, true);
    }

    /**
     * Outputs trace information if the specified level is equal or higher to the current reporting threshold,
     * without a trailing newline, and without including the source location.
     *
	 * @param msg a descriptive message
     * @param level the trace level
     */
    public static void traceNoSourceNoLn(String msg, Diagnostic level) {

        outputTrace(msg, level, false);
    }

    /********************************************************************************************************************************************/

    /**
     * Toggles whether diagnostic messages should be output to local standard output.
     *
     * @param local_reporting true if messages should be output locally
     */
    public static void setLocalErrorReporting(boolean local_reporting) {

        Diagnostic.local_reporting = local_reporting;
    }

    /**
     * Returns information on the most recent user method in the current call chain.
     *
     * @return returns information on the most recent user method in the current call chain
     */
    public static String getMethodInCallChain() {

        // Get a stack trace.
        StackTraceElement[] trace = new Exception().getStackTrace();

        // Ignore calls within Diagnostic or Error class.
        // Start from 1 - depth 0 is this method.
        for (int i = 1; i < trace.length; i++) {

            StackTraceElement call = trace[i];
            String calling_class_name = call.getClassName();

            if (!calling_class_name.equals(DIAGNOSTIC_CLASS_NAME) && !calling_class_name.equals(ERROR_CLASS_NAME))
                return calling_class_name + "::" + call.getMethodName();
        }

        return "";
    }

    /**
     * Returns information on one of the methods in the current call chain.
     *
     * @param depth the depth in the current chain, where 1 corresponds to the method calling this one.
     * @return a string containing the class and method name of the corresponding call
     */
    public static String getMethodInCallChain(int depth) {

        // Get a stack trace.
        StackTraceElement[] trace = new Exception().getStackTrace();

        if (trace.length > depth) return trace[depth].getClassName() + "::" + trace[depth].getMethodName();
        else return "";
    }

    /**
     * Prints a stack trace.
     */
    public static void printStackTrace() {

        // Get a stack trace.
        StackTraceElement[] trace = new Exception().getStackTrace();

        // Ignore calls within Diagnostic or Error class.
        // Start from 1 - depth 0 is this method.
        for (int i = 1; i < trace.length; i++) {

            StackTraceElement call = trace[i];
            String calling_class_name = call.getClassName();

            if (!calling_class_name.equals(DIAGNOSTIC_CLASS_NAME) && !calling_class_name.equals(ERROR_CLASS_NAME))
                System.out.println(calling_class_name + "::" + call.getMethodName() + " line " + call.getLineNumber());
        }
    }

    /**
     * @see Object#toString()
     */
    public String toString() {

        return level_description;
    }

    /********************************************************************************************************************************************/

	private Diagnostic() {

	    this(0, "");
		threshold = NONE;
	}

    private Diagnostic(int level_value, String level_description) {

        this.level_value = level_value;
        this.level_description = level_description;
    }

    /********************************************************************************************************************************************/

    private static void outputTrace(String message, Diagnostic level, boolean new_line) {

    	// Synchronise with respect to the Error methods too.
    	synchronized (Error.class) {
	        if (level.level_value >= threshold.level_value)
                outputTrace(message, new_line);
    	}
    }

    private static void outputTrace(String message, boolean new_line) {

    	// Synchronise with respect to the Error methods too.
    	synchronized (Error.class) {
            
            if (local_reporting) {
                if (new_line) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
                    Date date = new Date();
                    System.out.print(dateFormat.format(date) + " :: ");
                }
                System.out.print(message);
                if (new_line)
                    System.out.println();
            }
    	}
    }
}
