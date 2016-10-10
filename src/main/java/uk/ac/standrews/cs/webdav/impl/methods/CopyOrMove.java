/**
 * Created on Aug 19, 2005 at 12:22:29 PM.
 */
package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.exceptions.LockUseException;
import uk.ac.standrews.cs.fs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.fs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.UriUtil;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Contains code common to copy and move operations.
 *
 * @author graham
 */
public abstract class CopyOrMove extends AbstractHTTPMethod {

    /**
     * Factors out the commonality in the COPY and MOVE methods.
     */
	public void execute(Request request, Response response, boolean do_copy) throws IOException, HTTPException {
		
		URI uri =                    request.getUri();
		IDirectory source_parent =   getParent(uri);                          // Get the parent directory of the source location.
		String source_base_name =    UriUtil.baseName(uri);                  // Get the name of the location within that directory.
		String if_header =           request.getHeader(HTTP.HEADER_IF);       // Get the If header.
		String lock_token =          getLockTokenFromIfHeader(if_header);
		
		Diagnostic.trace("Source name = " + source_base_name, Diagnostic.RUN);
		
		// Deal with the destination location.
		String destination_header = request.getHeader(HTTP.HEADER_DESTINATION);
		if (destination_header == null) throw new HTTPException("No destination specified");
		
		try {
			URI destination_file_path =     new URI(destination_header);                  // Get the file path of the destination location.
			IDirectory destination_parent = getParent(destination_file_path);             // Get the parent directory of that location.
			String destination_base_name =  UriUtil.baseName(destination_file_path);     // Get the name of the location within that directory.
			
			// Should overwrite unless flag explicitly set to false in header.
			// See RFC 2518 8.8.6/7.
			boolean overwrite = shouldOverwrite(request);
			
			// Remember whether destination is non-null, for setting result code.
			boolean destination_not_null = destination_parent.contains(destination_base_name);
			
			// If a lock token was specified in an If header, the file must be currently locked with the given token.
			if (lock_token != null) lock_manager.checkWhetherLockedWithToken(uri, lock_token);
			
			// TODO check if the destination is locked, in which case must have lock token. Ditto for source in case of move.
			
			// Either copy the object or move it.
			if (do_copy) {
				file_system.copyObject(source_parent, source_base_name, destination_parent, destination_base_name, overwrite);
			} else {
				file_system.moveObject(source_parent, source_base_name, destination_parent, destination_base_name, overwrite);
			}
			
			if (destination_not_null) response.setStatusCode(HTTP.RESPONSE_NO_CONTENT);
			else                      response.setStatusCode(HTTP.RESPONSE_CREATED);
			
			response.close();
		}
		catch (LockUseException e) {
            handleLockException(lock_token, e);
		}
		catch (BindingAbsentException e)  {
			throw new HTTPException("Source object not found", HTTP.RESPONSE_NOT_FOUND, true);
		}
		catch (BindingPresentException e) {
			throw new HTTPException("Destination non-null with no-overwrite", HTTP.RESPONSE_PRECONDITION_FAILED, true);
		}
		catch (PersistenceException e) {
			throw new HTTPException("Couldn't create copy", HTTP.RESPONSE_INTERNAL_SERVER_ERROR, true);
		}
		catch (URISyntaxException e) {
			throw new HTTPException("Invalid destination path", HTTP.RESPONSE_BAD_REQUEST, true);
		}
	}
}
