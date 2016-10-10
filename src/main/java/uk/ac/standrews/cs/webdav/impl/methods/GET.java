package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.filesystem.exceptions.InvalidPathException;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.persistence.interfaces.INameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.UriUtil;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.impl.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Implementation of the HTTP GET method.
 *
 * @author Ben Catherall, al, graham
 */
public class GET extends AbstractHTTPMethod {
	
	public String getMethodName() {
		return HTTP.METHOD_GET;
	}
	
	public void execute(Request request, Response response) throws IOException, HTTPException {
		
		try {
			// Process the source information.
			URI uri = request.getUri();
			
			IAttributedStatefulObject target_object = file_system.resolveObject(uri);
			
			if (target_object == null) {
				throw new InvalidPathException();     // Caught at the end of this method.
			}

			if (target_object instanceof IDirectory) {
                processDirectory(request, response, target_object);
			} else if (target_object instanceof IFile) {
                processFile(request, response, target_object, uri);
			} else {
                Error.hardError("unknown attributed stateful object encountered of type: " + target_object.getClass().getName());
            }
			
			response.setStatusCode(HTTP.RESPONSE_OK);
			response.close();
		} catch (InvalidPathException e) {
            throw new HTTPException("Object '" + request.getUri() + "' not found.", HTTP.RESPONSE_NOT_FOUND, false);
        }
	}

    private void processDirectory(Request request, Response response, IAttributedStatefulObject target_object) throws IOException {
        // If the directory URI has a trailing slash, send a directory listing.
        // Otherwise, send a redirect to the proper URL with trailing slash.

        String path_string = request.getUri().getPath();

        if (path_string.endsWith("/")) {
            Diagnostic.trace("sending directory listing", Diagnostic.RUN);
            sendDirectoryListing(request, response, (IDirectory) target_object);
        } else {
            Diagnostic.trace("sending redirect", Diagnostic.RUN);
            response.sendRedirect(path_string + "/", false);
            return;
        }
    }

    private void processFile(Request request, Response response, IAttributedStatefulObject target_object, URI uri) throws IOException {
        IFile file = (IFile) target_object;

        if (request.hasParameter(HTTP.PARAMETER_INFO)) {
            Diagnostic.trace("sending file information", Diagnostic.RUN);
            sendFileInformation(request, response, file, UriUtil.baseName(uri));
            response.close();
            return;
        }

        Diagnostic.trace("Sending file information", Diagnostic.RUN );
        executeGet(request, response, file);
    }
	
	private void executeGet(Request request, Response response, IFile file) throws IOException {
		
		// TODO obsolete comment?
		// need to do some mime stuff for this
		// might end up needing it for posts or something?
		// hopefully not!
		Diagnostic.trace("Executing get for file", Diagnostic.RUN);
		
		// output the headers and size information
		Diagnostic.trace("Attributes for file = " + file.getAttributes(), Diagnostic.RUN);
		
		String content_type = getFileContentType(file);
		Diagnostic.trace("Got attribute content = " + content_type, Diagnostic.RUN );
		
		response.setHeader(HTTP.HEADER_CONTENT_TYPE, content_type);
		response.setStatusCode(HTTP.RESPONSE_OK);
		
		OutputStream out = response.getOutputBuffer();
		IData data = file.reify();
		byte[] bytes = data.getState();
		
		Diagnostic.trace("Sending " + bytes.length, Diagnostic.RUN);
		out.write(bytes);
	}
	
	private void sendDirectoryListing(Request request, Response response, IDirectory collection) throws IOException {
		
		response.setStatusCode(HTTP.RESPONSE_OK);
		Writer writer = response.getOutputWriter();
		String decoded = URLDecoder.decode(request.getUri().getPath(), "UTF-8");
		writer.write("<html><head><title>Index of " + decoded +
				"</title><link rel=\"stylesheet\" href=\"http://www-os.dcs.st-and.ac.uk/asa/ASA/asa.css\"></head><body><h1>Index of " +
				decoded +
		        "</h1><pre>\r\n");
		
		int nameWidth = 30;
		Iterator iter = collection.iterator();
		while( iter.hasNext() ) {
			Diagnostic.trace( "Iterating throuigh directory....", Diagnostic.RUN );
			INameAttributedPersistentObjectBinding binding = (INameAttributedPersistentObjectBinding)iter.next();
			Diagnostic.trace( "Found name=" + binding.getName(), Diagnostic.RUN );
			String name = binding.getName();
			if (name.length() > nameWidth) nameWidth = name.length();
		}
		
		writer.write("<img src=\"http://www.dcs.st-and.ac.uk/icons/blank.gif\" alt=\"     \"> " +
				"Name"  +	// there was a Format.makeWidth in 
		        "  Last Modified      Size       Information\r\n");
		writer.write("<hr>\r\n");
		
		if (collection.getParent() != null) {
			writer.write("<img src=\"http://www.dcs.st-and.ac.uk/icons/back.gif\"> <a href=\"../\">Parent Directory</a>\r\n");
		}
		
		Iterator iter2 = collection.iterator();
		while( iter2.hasNext() ) {        
			INameAttributedPersistentObjectBinding binding = (INameAttributedPersistentObjectBinding) iter2.next();
			IAttributedStatefulObject child = binding.getObject();
			String fname = binding.getName();
			/// pretty things like icons, dates, last modifieds, sizes etc..
			if (child instanceof IDirectory) {
				fname += "/";
			}
			String lastmodifiedDate;
			long lastmodified = 0;
			try {
				lastmodified = child.getModificationTime();
			} catch (AccessFailureException e) {
				// TODO do something if the getModificationTime fails
				e.printStackTrace();
			}
			if( lastmodified == 0 ) {
				lastmodifiedDate = "?";
			} else {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
				lastmodifiedDate = simpleDateFormat.format(new Date(lastmodified));
			}
			String size = "-";
			if (child instanceof IFile) {
				long lSize = ((IFile) child).reify().getSize();
				size = (new Long( lSize )).toString(); //Format.formatFileSize(lSize);
			}
			//size = Format.makeWidth(size, 10);
			
			String icon = "generic";
			if (child instanceof IDirectory) {
				icon = "folder";
			}
			String pad = "    ";
			writer.write("<img src=\"http://www.dcs.st-and.ac.uk/icons/" + icon + ".gif\"> <a href=\"" +
					fname +
					"\">" +
					fname +
					"</a>" +
					pad +
					"  " +
					lastmodifiedDate +
					"  " +
					size +
			        "\r\n");
		}
		writer.write("</pre>");
		
		if (request.hasParameter(HTTP.PARAMETER_INFO)) {
			writer.write("<hr><br><h2>DirectoryImpl information</h2><table border='0'>");
			DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
			try {
				sendRow(writer, "Created", format.format(new Date(collection.getCreationTime())));
			} catch (AccessFailureException e) {
				// TODO do something if the getCreationTime fails
				e.printStackTrace();
			}
			try {
				sendRow(writer, "Last modified", format.format(new Date(collection.getModificationTime())));
			} catch (AccessFailureException e) {
				// TODO do something if the getModificationTime fails
				e.printStackTrace();
			}
		}
		writer.flush();
		writer.close();
	}
	
	private void sendFileInformation(Request request, Response response, IFile file, String name) throws IOException {
		
		Writer writer = response.getOutputWriter();
		
		writer.write("<html><head>" +
				"<title>ASA Webdav System status</title>" +
				"<link rel='stylesheet' href='http://panda/asa/ASA/asa.css'>" + 
		"</head><body><h1>File information</h1>");
		writer.write("<table border='0' width='100%' cellspacing='1' cellpadding='3'><tr><td width='200'></td><td></td></tr>");
		sendRow(writer, "Name", name);
		sendRow(writer, "Size", String.valueOf( file.reify().getSize() ));
		DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		try {
			sendRow(writer, "Created", format.format(new Date(file.getCreationTime())));
		} catch (AccessFailureException e) {
			// TODO do something if the getCreationTime fails
			e.printStackTrace();
		}
		try {
			sendRow(writer, "Last modified", format.format(new Date(file.getModificationTime())));
		} catch (AccessFailureException e) {
			// TODO do something if the getModificationTime fails
			e.printStackTrace();
		}
		String content;
		if ((content = file.getAttributes().get(FileSystemConstants.CONTENT)) != null) { // FIXME - there should be no depencency on a specific file system
			sendRow(writer, "Content Type", content);
		}
		writer.write("</table>");
		
		if (file.reify().getSize() > 0) {
			
			writer.write("<h2>Fragments omitted</h2>");
			writer.write("</body></html>");
		}
	}
	
	private void sendRow(Writer writer, String left, String right) throws IOException {
		writer.write("<tr><td class='left'>" + left + "</td><td class='right'><b>" + right + "</b></td></tr>");
	}
}
