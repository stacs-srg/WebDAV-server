package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.exceptions.LockUseException;
import uk.ac.standrews.cs.fs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.utils.UriUtil;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;

import java.io.IOException;
import java.net.URI;

/**
 * @author Ben Catherall, al, graham
 */
public class MKCOL extends AbstractHTTPMethod {
	
	public String getMethodName() {
		return HTTP.METHOD_MKCOL;
	}
	
	public void execute(Request request, Response response) throws IOException, HTTPException {
		
		URI uri = request.getUri();
		
		IDirectory parent = getParent(uri);
		String name =       UriUtil.baseName(uri);
		String if_header =  request.getHeader(HTTP.HEADER_IF);             // Get the If header.
		String lock_token = getLockTokenFromIfHeader(if_header);
		
		try {
			// If the directory is currently locked, check that the token specified in the If header is the right one.
			lock_manager.checkWhetherLockedWithOtherToken(uri, lock_token);
			
			// If a lock token was specified in an If header, the directory must be currently locked with the given token (RFC 2518: 9.4).
			if (lock_token != null) lock_manager.checkWhetherLockedWithToken(uri, lock_token);
			
			file_system.createNewDirectory(parent, name);
		}
		catch (LockUseException e) {
			handleLockException(lock_token, e);
		}
		catch (BindingPresentException e) {
            throw new HTTPException("collection already exists",   HTTP.RESPONSE_METHOD_NOT_ALLOWED, true);
        }
		catch (PersistenceException e) {
            throw new HTTPException("could not create collection", HTTP.RESPONSE_INTERNAL_SERVER_ERROR, true);
        }
		
		response.setStatusCode(HTTP.RESPONSE_CREATED);
	}
}
