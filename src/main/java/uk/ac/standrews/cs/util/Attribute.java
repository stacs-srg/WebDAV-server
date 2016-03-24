/*
 * Created on Jun 30, 2005 at 4:48:44 PM.
 */
package uk.ac.standrews.cs.util;

import uk.ac.standrews.cs.persistence.interfaces.IAttribute;

public class Attribute implements IAttribute {

    private String name;
    private String value;

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
