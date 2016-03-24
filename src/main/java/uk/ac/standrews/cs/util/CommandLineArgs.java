/**
 * Created on Aug 10, 2005 at 8:28:58 PM.
 */
package uk.ac.standrews.cs.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for manipulating command line arguments.
 *
 * @author graham
 */
public class CommandLineArgs {

	/**
	 * Parses the given command line arguments into a map from flags to values.
	 * Each argument is assumed to be of the form
	 * 
	 *   -xvalue
	 *   
	 * which would result in a map entry from "-x" to "value"
	 * 
	 * @param args the command line arguments
	 * @return a map from flags to values
	 */
	public static Map parseCommandLineArgs(String[] args) {
	    
	    Map map = new HashMap();
	    
	    for (int i = 0; i < args.length; i++) {
	        
	        if (args[i].length() > 1) {
	            
	            String flag = args[i].substring(0, 2);
	            String value = args[i].substring(2);
	            map.put(flag, value);
	        }
	    }
	    
	    return map;
	}

	public static String getArg(String[] args, String arg_name) {

		return (String) parseCommandLineArgs(args).get(arg_name);
	}
}
