package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.exceptions.LockUseException;
import uk.ac.standrews.cs.fs.exceptions.*;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.store.impl.localfilebased.InputStreamData;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.UriUtil;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.MIME;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Ben Catherall, al, graham
 */
public class PUT extends AbstractHTTPMethod {
	
	public String getMethodName() {
		return HTTP.METHOD_PUT;
	}

	public void execute(Request request, Response response) throws IOException, HTTPException {
		
//		if (isChunked(request)) {
//            System.out.println("Chunked PUT");
//            System.out.println("has content: " + request.hasContent());
//            System.out.println("content length: " + request.getContentLength());
//            // throw new HTTPException("Chunked puts are not allowed", HTTP.RESPONSE_NOT_IMPLEMENTED);
//
//
//        }
        processRequest(request);

		response.setStatusCode(HTTP.RESPONSE_CREATED);
		response.close();
	}

    // REMOVEME
    public String readFullyAsString(InputStream inputStream, String encoding)
            throws IOException {
        return readFully(inputStream).toString(encoding);
    }

    // REMOVEME
    public byte[] readFullyAsBytes(InputStream inputStream)
            throws IOException {
        return readFully(inputStream).toByteArray();
    }

    // REMOVEME
    private ByteArrayOutputStream readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }



    private void processRequest(Request request) throws HTTPException {

        if (isChunked(request)) {
            processChunkedRequest(request);
        } else {
            processNormalRequest(request);
        }
    }

    private void processNormalRequest(Request request) throws HTTPException {
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
        } catch (LockUseException e) {
            handleLockException(lock_token, e);
        } catch (BindingAbsentException | BindingPresentException |
                PersistenceException | UpdateException e)  {
            throw new HTTPException(e, HTTP.RESPONSE_INTERNAL_SERVER_ERROR, true);
        }
    }

    // TODO - re-write the following method considering the following:
    // a file may have to be created (createNewFile)
    // use a series of appendToFile calls rather than an update followed by a created
    // obviously we have the problem of already existing files that have to be updates
    // in fact, the finder for example, creates an empty file first and then makes another request
    // sending the actual data
    private void processChunkedRequest(Request request) throws HTTPException {
        URI uri =             request.getUri();
        IDirectory parent =   getParent(uri);                                // Get the parent directory of the location to PUT the data.
        String base_name =    UriUtil.baseName(uri);                        // Get the name of the location within that directory.
        String content_type = getContentType(request, base_name);            // Get the content type of the data.
        String if_header =    request.getHeader(HTTP.HEADER_IF);             // Get the If header.
        String lock_token =   getLockTokenFromIfHeader(if_header);

        try {
            boolean firstChunk = true;
            IData data;
            do {
                data = getNextChunkData(request);
                if (data != null) {

                    Diagnostic.trace("Read chunked data in: " + data.getSize() + " bytes ", Diagnostic.RUN);
                    // If a file with the given name already exists, update it, otherwise create a new one.
                    if (parent.contains(base_name) && firstChunk) {
                        updateFile(uri, lock_token, parent, base_name, content_type, data);
                        firstChunk = false;
                    } else if (parent.contains(base_name) && !firstChunk){
                        appendToFile(uri, lock_token, parent, base_name, content_type, data);
                    }

                }

                // Skip HTTP.CRLF
                request.getInputStream().read();
                request.getInputStream().read();
            } while (data != null);

        } catch (LockUseException e) {
            handleLockException(lock_token, e);
        } catch (BindingAbsentException |
                PersistenceException | UpdateException | AppendException e)  {
            throw new HTTPException(e, HTTP.RESPONSE_INTERNAL_SERVER_ERROR, true);
        }catch (IOException e) {
            throw new HTTPException(e, HTTP.RESPONSE_INTERNAL_SERVER_ERROR, true);
        }
    }

    private void updateFile(URI uri, String lock_token, IDirectory parent, String base_name, String content_type, IData data)
            throws LockUseException, PersistenceException, BindingAbsentException, UpdateException {
        // If the file is currently locked, check that the token specified in the If header is the right one.
        lock_manager.checkWhetherLockedWithOtherToken(uri, lock_token);

        // If a lock token was specified in an If header, the file must be currently locked with the given token (RFC 2518: 9.4).
        if (lock_token != null) {
            lock_manager.checkWhetherLockedWithToken(uri, lock_token);
        }

        file_system.updateFile(parent, base_name, content_type, data);
    }

    private void appendToFile(URI uri, String lock_token, IDirectory parent, String base_name, String content_type, IData data)
            throws LockUseException, BindingAbsentException, PersistenceException, AppendException {
        // If the file is currently locked, check that the token specified in the If header is the right one.
        lock_manager.checkWhetherLockedWithOtherToken(uri, lock_token);

        // If a lock token was specified in an If header, the file must be currently locked with the given token (RFC 2518: 9.4).
        if (lock_token != null) {
            lock_manager.checkWhetherLockedWithToken(uri, lock_token);
        }

        file_system.appendToFile(parent, base_name, content_type, data);
    }

    private void createFile(URI uri, String lock_token, IDirectory parent, String base_name, String content_type, IData data)
            throws LockUseException, BindingPresentException, PersistenceException {
        URI parent_uri = UriUtil.parentUri(uri);

        // If the parent directory is currently locked, check that the token specified in the If header is the right one.
        lock_manager.checkWhetherLockedWithOtherToken(parent_uri, lock_token);

        // If a lock token was specified in an If header, the parent directory must be currently locked with the given token (RFC 2518: 9.4).
        if (lock_token != null) {
            lock_manager.checkWhetherLockedWithToken(parent_uri, lock_token);
        }

        file_system.createNewFile(parent, base_name, content_type, data);
    }
	
	private String getContentType(Request request, String base_name) {
		String content_type = request.getHeader(HTTP.HEADER_CONTENT_TYPE);
		if (content_type == null) content_type = MIME.getContentTypeFromFileName(base_name);
		
		return content_type;
	}

    private IData getNextChunkData(Request request) {
        try {
            byte[] sizeBytes = request.stopAtBytes(HTTP.CRLF, 10, 4);
            int size= Integer.parseInt(new String(sizeBytes), 16); // HEX
            if (size != 0) {
                return new InputStreamData(request.getInputStream(), size);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

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
