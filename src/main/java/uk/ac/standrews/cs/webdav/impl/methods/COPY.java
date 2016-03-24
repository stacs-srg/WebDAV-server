package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;

import java.io.IOException;

/**
 * @author Ben Catherall, al, graham
 */
public class COPY extends CopyOrMove {

    public String getMethodName() {
        return HTTP.METHOD_COPY;
    }
    
    public void execute(Request request, Response response) throws IOException, HTTPException {
    	
    	// Use the generic code in the superclass.
    	execute(request, response, true);
    }
}
