/**
 * Created on Aug 18, 2005 at 6:23:26 PM.
 */
package uk.ac.standrews.cs.webdav.impl.methods;

import uk.ac.standrews.cs.filesystem.absfilesystem.impl.storebased.StoreBasedFileSystem;
import uk.ac.standrews.cs.filesystem.interfaces.IDirectory;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.filesystem.interfaces.IFileSystem;
import uk.ac.standrews.cs.locking.interfaces.ILockManager;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.util.StringUtil;
import uk.ac.standrews.cs.util.UriUtil;
import uk.ac.standrews.cs.webdav.impl.HTTP;
import uk.ac.standrews.cs.webdav.impl.Request;
import uk.ac.standrews.cs.webdav.interfaces.HTTPMethod;
import uk.ac.standrews.cs.webdav.util.XMLHelper;

import java.net.URI;

/**
 * Contains code common to various HTTP methods.
 *
 * @author al, graham
 */
public abstract class AbstractHTTPMethod implements HTTPMethod {
	
	protected IFileSystem file_system;
	protected ILockManager lock_manager;
	protected XMLHelper xml_helper;
	
	private static final String open_token_delimiter =  "<";
	private static final String close_token_delimiter = ">";
	
	public abstract String getMethodName();
	
	public void init(IFileSystem file_system, ILockManager lock_manager) {
		
		this.file_system = file_system;
		this.lock_manager = lock_manager;
		xml_helper = XMLHelper.getInstance();
	}
	
	protected IDirectory getParent(URI uri) {
		
		// Path denotes an element of a sub-directory of the root.
		URI parent_path = UriUtil.parentUri(uri);
		
		// Get the parent directory.
		IAttributedStatefulObject parent_object = file_system.resolveObject(parent_path);
			
		try {
			return (IDirectory) parent_object;
		}
		catch (ClassCastException e) {
			Error.hardExceptionError("parent of " + uri + "not a directory", e);
			return null;
		}
	}
	
	// COPY and MOVE should overwrite unless flag explicitly set to false in header (RFC 2518: 8.8.6/7, 8.9.3).
	protected boolean shouldOverwrite(Request request) {
		
		String overwriteHeader = request.getHeader(HTTP.HEADER_OVERWRITE);
		return !(overwriteHeader != null && StringUtil.contains(overwriteHeader,HTTP.HEADER_TOKEN_F));
	}
	
	protected String getFileContentType(IFile file) {
		
		String content_type = file.getAttributes().get(StoreBasedFileSystem.CONTENT);
		if (content_type == null)
			content_type = HTTP.CONTENT_TYPE_UNKNOWN;
		
		return content_type;
	}
	
	protected String getLockTokenFromLockTokenHeader(Request request) {
		
		// This is only used in UNLOCK (RFC 2518: 9.5) - other lock tokens are obtained from If header.
		return request.getHeader(HTTP.HEADER_LOCKTOKEN);
	}
	
	protected String getLockTokenFromIfHeader(String if_header) {
		
		/*
		  Syntax of If header (RFC 2518: 9.4):
		  
		  If = "If" ":" ( 1*No-tag-list | 1*Tagged-list) 
		  No-tag-list = List 
		  Tagged-list = Resource 1*List 
		  Resource = Coded-URL 
		  List = "(" 1*(["Not"](State-token | "[" entity-tag "]")) ")" 
		  State-token = Coded-URL 
		  Coded-URL = "<" absoluteURI ">" 
		 */
		
		/*
		  Examples from RFC 2518:
	
		  LOCK /workspace/webdav/proposal.doc HTTP/1.1 
		  Host: webdav.sb.aol.com 
		  If: (<opaquelocktoken:e71d4fae-5dec-22d6-fea5-00a0c91e6be4>) 

		  COPY /~fielding/index.html HTTP/1.1 
		  Host: www.ics.uci.edu 
		  Destination: http://www.ics.uci.edu/users/f/fielding/index.html 
		  If: <http://www.ics.uci.edu/users/f/fielding/index.html> 
		  (<opaquelocktoken:f81d4fae-7dec-11d0-a765-00a0c91e6bf6>)


		  MOVE /container/ HTTP/1.1 
		  Host: www.foo.bar 
		  Destination: http://www.foo.bar/othercontainer/ 
		  If: (<opaquelocktoken:fe184f2e-6eec-41d0-c765-01adc56e6bb4>) 
		  (<opaquelocktoken:e454f3f3-acdc-452a-56c7-00a5c91e4b77>)
		 */
		
		if (if_header != null) {
			
			int start =  if_header.indexOf(HTTP.HEADER_OPAQUELOCKTOKEN);
			int finish = if_header.indexOf(">)", start);
			
			if (start > -1 && finish > -1)
				return if_header.substring(start, finish);
		}
		
		return null;
	}
	
	protected String addTokenDelimiters(String token) {
		
		return open_token_delimiter + token + close_token_delimiter;
	}
	
	protected String removeTokenDelimiters(String delimited_token) {
		
		if (delimited_token.startsWith(open_token_delimiter) && delimited_token.endsWith(close_token_delimiter))
			return delimited_token.substring(open_token_delimiter.length(), delimited_token.length() - close_token_delimiter.length());
		
		else
			return delimited_token;
	}
}
