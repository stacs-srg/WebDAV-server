/*
 * Created on Dec 20, 2004 at 2:27:32 PM.
 */
package uk.ac.standrews.cs.util;

import java.util.Iterator;
import java.util.List;

/**
 * Reverse iterator over a list.
 *
 * @author graham
 */
public class ReverseIterator implements Iterator {
    
    private int index;
    private List list;
    
    public ReverseIterator(List list) {
        
        this.list = list;
        index = list.size() - 1;
    }
    
    public boolean hasNext() {
        return index >= 0;
    }
    
    public Object next() {
        return list.get(index--);
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
