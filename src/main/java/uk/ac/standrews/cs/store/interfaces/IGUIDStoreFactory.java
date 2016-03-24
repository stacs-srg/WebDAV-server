/*
 * Created on Sep 9, 2005 at 10:01:22 AM.
 */
package uk.ac.standrews.cs.store.interfaces;


/**
 * Provides an interface to allow the instantiation of a store without knowing its implementation.
 *
 * @author graham
 */
public interface IGUIDStoreFactory {

    /**
     * Creates a new store instance.
     * 
     * @return a new store
     */
    IGUIDStore makeStore();
}