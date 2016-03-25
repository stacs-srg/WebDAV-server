package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;

import java.io.IOException;
import java.net.URI;
import java.nio.file.InvalidPathException;

/**
 * @author Ben Catherall, al, graham
 */
public class HEAD extends AbstractHTTPMethod {
	
	public String getMethodName() {
		return HTTP.METHOD_HEAD;
	}
	
	public void execute(Request request, Response response) throws IOException, HTTPException {
		
		try {
			// Process the source information.
			URI uri = request.getUri();
			
			IAttributedStatefulObject target_object = file_system.resolveObject(uri);
			
			if (target_object == null) throw new InvalidPathException();     // Caught at the end of this method.
			
			if (target_object instanceof IDirectory) {
				
				// If the directory URL has a trailing slash, just set the content type.
				// Otherwise, send a redirect to the proper URL with trailing slash.

				String path_string = uri.getPath();
				
				if (path_string.endsWith("/")) response.setContentType(HTTP.CONTENT_TYPE_HTML);
				else {
					response.sendRedirect(path_string + "/", false);
					return;
				}
			}
			else if (target_object instanceof IFile) {
				
				IFile file = (IFile) target_object;
				
				response.setContentType(getFileContentType(file));
				response.setContentLength(getFileSize(file));
			}
			else Error.hardError("unknown attributed stateful object encountered of type: " + target_object.getClass().getName());
			
			response.setStatusCode(HTTP.RESPONSE_OK);
			response.close();
		}
		catch (InvalidPathException e) { throw new HTTPException("Object '" + request.getUri() + "' not found.", HTTP.RESPONSE_NOT_FOUND, false); }
	}
	
	private long getFileSize(IFile file) {
		return file.reify().getSize();
	}
}
