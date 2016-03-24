/*
 * Created on Jun 17, 2005 at 3:37:35 PM.
 */
package uk.ac.standrews.cs.util;

import uk.ac.standrews.cs.persistence.interfaces.IAttributes;

import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author al
 */
public class Attributes implements IAttributes {

    private String attString;
	public static final String EQUALS =      "=";		    // used to separate names and values
	public static final String SEPARATOR =   "^";	        // used to separate attributes
	
    /**
     * @param attString
     */
    public Attributes(String attString) {
        this.attString = attString;
    }
    
    public String toString() {
        return attString;
    }
    
    public void addAttribute( String key, String value ) {
        attString += key + SEPARATOR + value;
    }
    
    /* (non-Javadoc)
     * @see uk.ac.stand.dcs.asa.filesystem.interfaces.Attributes#contains(java.lang.String)
     */
    public boolean contains(String attribute) {
        return StringUtil.contains( attString, attribute );
    }
    
    public String get( String name ) {
        // TODO - this is slow - optimise this!
        Iterator i = iterator();
        while( i.hasNext() ) {
            Attribute next = (Attribute) i.next();
            if( next.getName().equals( name ) ) {
                return next.getValue();
            }
        }
        return null;
    }
    
    public Iterator iterator() {
        return new AttributeIterator();
    }
    
    private class AttributeIterator implements Iterator {

        private StringTokenizer st;

        public AttributeIterator() {
            st = new StringTokenizer(attString, SEPARATOR);
        }
        
        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() { 
        	Error.hardError("unimplemented method");
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return st.hasMoreTokens();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() {
            StringTokenizer namevaluetokenizer = new StringTokenizer(st.nextToken(), Attributes.EQUALS);
            return new Attribute( namevaluetokenizer.nextToken(), namevaluetokenizer.nextToken() );
        }    
    }
}
