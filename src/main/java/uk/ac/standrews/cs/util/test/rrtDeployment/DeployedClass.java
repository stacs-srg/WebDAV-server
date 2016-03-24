/*
 * Created on Feb 15, 2005 at 9:14:30 AM.
 */
package uk.ac.standrews.cs.util.test.rrtDeployment;

/**
 * Insert comment explaining purpose of class here.
 *
 * @author graham
 */
public class DeployedClass implements DeployedInterface {

    public String getString() {
        
        System.out.println("call to getString");
        
        return "successful";
    }
}
