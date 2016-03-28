package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.LockUseException;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.util.UriUtil;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;

import java.io.IOException;
import java.net.URI;

/**
 * @author Ben Catherall, al, graham
 */
public class DELETE extends AbstractHTTPMethod {
	
	public String getMethodName() {
		return HTTP.METHOD_DELETE;
	}
	
	public void execute(Request request, Response response) throws IOException, HTTPException {
		
		URI uri =           request.getUri();
		IDirectory parent = getParent(uri);                          // Get the parent directory of the location to DELETE.
		String base_name =  UriUtil.baseName(uri);                  // Get the name of the location within that directory.
		String if_header =  request.getHeader(HTTP.HEADER_IF);       // Get the If header.
		String lock_token = getLockTokenFromIfHeader(if_header); 
		
		try {
			// If the file is currently locked, check that the token specified in the If header is the right one.
			lock_manager.checkWhetherLockedWithOtherToken(uri, lock_token);
			
			// If a lock token was specified in an If header, the file must be currently locked with the given token (RFC 2518: 9.4).
			if (lock_token != null) {
				lock_manager.checkWhetherLockedWithToken(uri, lock_token);
			}
			
			file_system.deleteObject(parent, base_name);
			
			lock_manager.removeResourceFromMatchingLocks(uri, lock_token);
			
			// TODO if this is a directory, need to remove all locks on objects within it.
		} catch (BindingAbsentException e) {
            throw new HTTPException("Object not found", HTTP.RESPONSE_NOT_FOUND, false);
        } catch (LockUseException e) {
            handleLockException(lock_token, e);
        }
		
		response.setStatusCode(HTTP.RESPONSE_NO_CONTENT);
		response.close();
	}
}
