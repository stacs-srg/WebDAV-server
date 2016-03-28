/*
 * Created on May 23, 2005 at 1:44:55 PM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.usecase;

import uk.ac.standrews.cs.interfaces.IGUID;
import uk.ac.standrews.cs.interfaces.IPID;
import uk.ac.standrews.cs.persistence.impl.ReflectivePersistentObject;
import uk.ac.standrews.cs.persistence.interfaces.IData;

/**
 * @author al
 */
public abstract class Person extends ReflectivePersistentObject {

    private int age;
    private String name;
    /**
     * 
     */
    public Person( int age, String name ) {
        super();
        this.age = age;
        this.name = name;
    }
    
    public Person(IGUID guid, IPID pid, IData thedata ) {
        super(thedata, pid, guid);       
    }
    
    public int getAge() {
        return age;
    }

    public String getName() {
    	return name;
    }
    
}
