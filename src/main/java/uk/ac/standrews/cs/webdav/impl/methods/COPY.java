package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.stand.dcs.asa.storage.webdav.exceptions.HTTPException;
import uk.ac.stand.dcs.asa.storage.webdav.impl.HTTP;
import uk.ac.stand.dcs.asa.storage.webdav.impl.Request;
import uk.ac.stand.dcs.asa.storage.webdav.impl.Response;

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
