/*
 * Created on Feb 14, 2005 at 11:05:04 PM.
 */
package uk.ac.standrews.cs.util.test.rrtDeployment;

import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;

/**
 * Insert comment explaining purpose of class here.
 *
 * @author graham
 */
public class RRTClientSideTest {

    public static void main(String[] args) {
        
        String server_host = "edradour.dcs.st-and.ac.uk";
        
        try {
            DeployedInterface server_object = (DeployedInterface)RafdaRunTime.getObjectByName(server_host, 52525, "testService");
            
            System.out.println("Service result: " + server_object.getString());
        }
        catch (Exception e) {
            
            System.out.println("Name_GUID_Binding to service failed: " + e.getMessage());
        }
    }
}
