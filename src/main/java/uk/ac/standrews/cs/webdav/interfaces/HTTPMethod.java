package uk.ac.standrews.cs.webdav.interfaces;

import uk.ac.standrews.cs.locking.interfaces.ILockManager;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;

import java.io.IOException;

/**
 * Handler for particular HTTP method.
 *
 * @author Ben Catherall, graham
 */
public interface HTTPMethod {

    /**
     * Returns the HTTP verb for the method (e.g. GET, POST etc).
     *
     * @return the name of the method
     */
    String getMethodName();

    /**
     * Initialises this instance.
     * 
     * @param file_system the abstract file system being used
     * @param lock_manager the lock manager being used
     */
    void init(IFileSystem file_system, ILockManager lock_manager);

    /**
     * Executes the HTTP method.
     * 
     * @param request the HTTP request
     * @param response the HTTP response to be filled in
     * 
     * @throws IOException if there was an error accessing the file system
     * @throws HTTPException if an HTTP error occurred
     */
    void execute(Request request, Response response) throws IOException, HTTPException;
}
