package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.exceptions.LockUseException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.*;

import java.io.IOException;
import java.net.URI;

/**
 * @author Ben Catherall, al, graham
 */
public class PUT extends AbstractHTTPMethod {
	
	public String getMethodName() {
		return HTTP.METHOD_PUT;
	}

	public void execute(Request request, Response response) throws IOException, HTTPException {
		
		if (isChunked(request))
            throw new HTTPException("Chunked posts are not allowed", HTTP.RESPONSE_NOT_IMPLEMENTED);

        processRequest(request);

		response.setStatusCode(HTTP.RESPONSE_CREATED);
		response.close();
	}

    private void processRequest(Request request) throws HTTPException {
        URI uri =             request.getUri();
        IDirectory parent =   getParent(uri);                                // Get the parent directory of the location to PUT the data.
        String base_name =    UriUtil.baseName(uri);                        // Get the name of the location within that directory.
        String content_type = getContentType(request, base_name);            // Get the content type of the data.
        IData data =          getData(request);                              // Get the data to be PUT.
        String if_header =    request.getHeader(HTTP.HEADER_IF);             // Get the If header.
        String lock_token =   getLockTokenFromIfHeader(if_header);

        Diagnostic.trace("Read in: " + data.getSize() + " bytes ", Diagnostic.RUN);

        try {
            // If a file with the given name already exists, update it, otherwise create a new one.
            if (parent.contains(base_name)) {
                updateFile(uri, lock_token, parent, base_name, content_type, data);
            }
            else {
                createFile(uri, lock_token, parent, base_name, content_type, data);
            }
        }
        catch (LockUseException e) {
            if (lock_token == null)
                throw new HTTPException(e, HTTP.RESPONSE_LOCKED, true); // No lock supplied in header.
            else
                throw new HTTPException(e, HTTP.RESPONSE_PRECONDITION_FAILED, true);
        }   // Lock supplied but the wrong one, or the resource wasn't actually locked.
        catch (BindingAbsentException | BindingPresentException |
                PersistenceException | UpdateException e)  {
            throw new HTTPException(e, HTTP.RESPONSE_INTERNAL_SERVER_ERROR, true);
        }

    }

    private void updateFile(URI uri, String lock_token, IDirectory parent, String base_name, String content_type, IData data)
            throws LockUseException, PersistenceException, BindingAbsentException, UpdateException {
        // If the file is currently locked, check that the token specified in the If header is the right one.
        lock_manager.checkWhetherLockedWithOtherToken(uri, lock_token);

        // If a lock token was specified in an If header, the file must be currently locked with the given token (RFC 2518: 9.4).
        if (lock_token != null) lock_manager.checkWhetherLockedWithToken(uri, lock_token);

        file_system.updateFile(parent, base_name, content_type, data);
    }

    private void createFile(URI uri, String lock_token, IDirectory parent, String base_name, String content_type, IData data)
            throws LockUseException, BindingPresentException, PersistenceException {
        URI parent_uri = UriUtil.parentUri(uri);

        // If the parent directory is currently locked, check that the token specified in the If header is the right one.
        lock_manager.checkWhetherLockedWithOtherToken(parent_uri, lock_token);

        // If a lock token was specified in an If header, the parent directory must be currently locked with the given token (RFC 2518: 9.4).
        if (lock_token != null) lock_manager.checkWhetherLockedWithToken(parent_uri, lock_token);

        file_system.createNewFile(parent, base_name, content_type, data);
    }
	
	private String getContentType(Request request, String base_name) {
		String content_type = request.getHeader(HTTP.HEADER_CONTENT_TYPE);
		if (content_type == null) content_type = MIME.getContentTypeFromFileName(base_name);
		
		return content_type;
	}
	
	private IData getData(Request request) {
		int size = getContentSize(request);
		return new InputStreamData(request.getInputStream(), size);
	}

	private int getContentSize(Request request) {
		return (int) (request.hasContent() ? request.getContentLength() : 0);
	}
	
	private boolean isChunked(Request request) {
		return request.hasContent() && request.getContentLength() == Request.CONTENT_LENGTH_CHUNKED;
	}
}
