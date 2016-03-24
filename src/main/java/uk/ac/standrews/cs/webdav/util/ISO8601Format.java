/**
 * Created on Aug 29, 2005 at 10:36:57 AM.
 */
package uk.ac.standrews.cs.webdav.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 *
 * @author graham
 */
public class ISO8601Format {
	
    private SimpleDateFormat simple_date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public String format(Date date) {
		
		String s = simple_date_format.format(date);
		
		// Convert to ISO8601 by inserting a colon between the hours and minutes in the timezone offset.
		int len = s.length();
		return s.substring(0, len - 2) + ":" + s.substring(len - 2, len);
	}
}
