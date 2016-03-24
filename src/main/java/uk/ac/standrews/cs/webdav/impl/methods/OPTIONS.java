package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.locking.interfaces.ILockManager;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;
import uk.ac.standrews.cs.webdav.interfaces.HTTPMethod;

import java.io.IOException;
import java.util.Iterator;

/**
 * Implementation of the HTTP OPTIONS method
 *
 * @author Ben Catherall, al, graham
 */
public class OPTIONS extends AbstractHTTPMethod {

    private String supportedMethods;

    public String getMethodName() {
        return HTTP.METHOD_OPTIONS;
    }

    public void execute(Request request, Response response) throws IOException, HTTPException {
    	
        response.setStatusCode(HTTP.RESPONSE_OK);
        
        response.setHeader(HTTP.HEADER_ALLOW, supportedMethods);
        response.setHeader(HTTP.HEADER_DAV, "1,2");
        response.setHeader(HTTP.HEADER_MS_AUTHOR_VIA, HTTP.HEADER_DAV);
        
        response.setContentType(HTTP.CONTENT_TYPE_TEXT_PLAIN);      // Like Apache.
        response.close();
    }

     public void init(IFileSystem file_system, ILockManager lock_manager) {

        StringBuffer methods = new StringBuffer();
        Iterator methodIterator = HTTP.METHODS.values().iterator();
        
        while (methodIterator.hasNext()) {
        	
            HTTPMethod next = (HTTPMethod) methodIterator.next();
            methods.append(next.getMethodName());
            
            if (methodIterator.hasNext()) methods.append(", ");
        }
        supportedMethods = methods.toString();
    }

    public boolean isSlaveMethod() {
        return true;
    }
}
