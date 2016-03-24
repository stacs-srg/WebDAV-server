/*
 * Created on Feb 14, 2005 at 11:05:27 PM.
 */
package uk.ac.standrews.cs.util.test.rrtDeployment;

import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;

import java.net.InetAddress;

/**
 * @author graham
 */
public class RRTServerSideTest {

    public static void main(String[] args) {
        
        try {
            RafdaRunTime.setPort(52525);
            
            System.out.println("RRT port: " + RafdaRunTime.getPort());
            
            RafdaRunTime.setHost(InetAddress.getLocalHost());

            System.out.println("RRT host: " + RafdaRunTime.getHost().getHostAddress());
            
            RafdaRunTime.startConnectionListener();
            
            DeployedInterface server_object = new DeployedClass();
            
            RafdaRunTime.deploy(DeployedInterface.class, server_object, "testService");
            
            System.out.println("Service deployed");
        }
        catch (Exception e) {
            
            System.out.println("Deployment failed: " + e.getMessage());
        }
    }
}
