package uk.ac.standrews.cs.webdav.impl.methods;

import org.w3c.dom.Element;
import uk.ac.standrews.cs.exceptions.LockUseException;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;
import uk.ac.standrews.cs.webdav.impl.WebDAV;

import java.io.IOException;
import java.net.URI;

/**
 * @author Ben Catherall, al, graham
 */
public class UNLOCK extends AbstractHTTPMethod {
	
	public String getMethodName() {
		return HTTP.METHOD_UNLOCK;
	}
	
	public void execute(Request request, Response response) throws IOException, HTTPException {

		// Process the source information.
		URI uri = request.getUri();
		
		// Get the Lock-Token from the header
		String lock_token = getLockTokenFromLockTokenHeader(request);
		
		response.setContentType(HTTP.CONTENT_TYPE_XML);
		response.setStatusCode(HTTP.RESPONSE_MULTI_STATUS);
		
		Element multiStatusEL = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_MULTI_STATUS);
		multiStatusEL.setAttribute(WebDAV.DAV_XML_NS_PREFIX, WebDAV.DAV_NS);
		
		String token_without_delimiters = removeTokenDelimiters(lock_token);
		
		try {
			lock_manager.checkWhetherLockedWithToken(uri, token_without_delimiters);
		} catch (LockUseException e) {
			throw new HTTPException(e, HTTP.RESPONSE_PRECONDITION_FAILED, true);
		}
		
		lock_manager.removeResourceFromMatchingLocks(uri, token_without_delimiters);
		
		response.setStatusCode(HTTP.RESPONSE_NO_CONTENT);
		response.close();
	}
}
