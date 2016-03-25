package uk.ac.standrews.cs.webdav.impl.methods;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.standrews.cs.exceptions.LockException;
import uk.ac.standrews.cs.locking.impl.LockDepth;
import uk.ac.standrews.cs.locking.impl.LockScope;
import uk.ac.standrews.cs.locking.impl.LockType;
import uk.ac.standrews.cs.locking.interfaces.ILock;
import uk.ac.standrews.cs.locking.interfaces.IResourceLockInfo;
import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.StringUtil;
import uk.ac.standrews.cs.util.UriUtil;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.*;
import uk.ac.standrews.cs.webdav.util.XMLLockPropertiesGen;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

/**
 * @author Ben Catherall, al, graham
 */
public class LOCK extends AbstractHTTPMethod {
	
	public String getMethodName() {
		return HTTP.METHOD_LOCK ;
	}
	
	public void execute(Request request, Response response) throws IOException, HTTPException {
		try {
			
			// Read the contents to see what locks are requested.
			if (request.hasContent()) {
				
				// Check that the parent of the specified URI exists in the file system - assuming that for lock-null resource the parent must already exist.
				URI uri = request.getUri();
				
				URI parent_uri = UriUtil.parentUri(uri);
				if (file_system.resolveObject(parent_uri) == null) throw new HTTPException("Parent of lock-null request does not exist", HTTP.RESPONSE_PRECONDITION_FAILED, true);;
				
				// Parse the contents.
				Document document;
				try {
					document = xml_helper.parse(new LengthInputStream( request.getInputStream(), request.getContentLength()));
				}
				catch (IOException e) { throw new HTTPException(e.getMessage(), HTTP.RESPONSE_BAD_REQUEST); }
				
				// Taken from RFC 2518:
				//
				// <!ELEMENT lockinfo (lockscope, locktype, owner?) >
				// <!ELEMENT locktype (write) >
				// <!ELEMENT write EMPTY >
				// <!ELEMENT lockscope (exclusive | shared) >
				// <!ELEMENT exclusive EMPTY >
				// <!ELEMENT shared EMPTY >
				// <!ELEMENT owner ANY >
				
				NodeList lockinfoNL = document.getElementsByTagNameNS(WebDAV.DAV_NS, WebDAV.DAV_LOCKINFO);
				if( lockinfoNL.getLength() != 1 ) throw new HTTPException("More than 1 " + WebDAV.DAV_LOCKINFO + " nodes found", HTTP.RESPONSE_BAD_REQUEST, true);
				
				Node lockInfoNode = lockinfoNL.item(0); // this is the root node
				NodeList lockInfoChildren = lockInfoNode.getChildNodes();
				int listLength = lockInfoChildren.getLength();
				
				String lock_type = null;
				String lock_scope = null;
				String lock_owner = null;
				
				// Iterate through the elements.
				for (int i = 0; i < listLength; i++ ) {
					
					Node next_element = lockInfoChildren.item(i);
					
					if (next_element.getNodeType() == Node.ELEMENT_NODE) { // skip over white space elements
						
						if (next_element.getLocalName().equals(WebDAV.DAV_LOCKTYPE)) {   // mandatory field - <!ELEMENT locktype (write) >
							
							if (lock_type == null) lock_type = extractLockType(next_element);
							else                   throw new HTTPException("Multiple locktype elements", HTTP.RESPONSE_BAD_REQUEST, true);
						}
						else if (next_element.getLocalName().equals(WebDAV.DAV_LOCKSCOPE)) {   // mandatory - <!ELEMENT lockscope (exclusive | shared) >
							
							if (lock_scope == null) lock_scope = extractLockScope(next_element);
							else                    throw new HTTPException("Multiple lockscope elements", HTTP.RESPONSE_BAD_REQUEST, true);
							
						}
						else if (next_element.getLocalName().equals(WebDAV.DAV_OWNER)) { //optional <!ELEMENT owner ANY >
							
							if (lock_owner == null) lock_owner = extractLockOwner(lock_owner, next_element);
							else                    throw new HTTPException("Multiple owner elements", HTTP.RESPONSE_BAD_REQUEST, true);
						}
					}
				} // end for loop
				
				if (lock_type == null || lock_scope == null) {
					throw new HTTPException("Mandatory fields locktype and lockscope not supplied", HTTP.RESPONSE_BAD_REQUEST, true);
				}
				
				// Get the depth required.
				int depth = Integer.MAX_VALUE;
				String depthHeader = request.getHeader(HTTP.HEADER_DEPTH);
				
				if (depthHeader != null) {
					
					// Check for infinity.
					if (!StringUtil.contains(depthHeader.toLowerCase(), HTTP.HEADER_TOKEN_INFINITY))
						
						try { depth = Integer.parseInt(depthHeader); }
						catch (NumberFormatException e) { throw new HTTPException("Invalid depth header", HTTP.RESPONSE_BAD_REQUEST, true); }
				}
				
				if (depth != 0 && depth != Integer.MAX_VALUE) {
					throw new HTTPException("Invalid depth header (must be 0 or infinity)", HTTP.RESPONSE_BAD_REQUEST);
				}
				
				// Ignore the timeout header - from RFC 2518:
				//
				// Clients may include Timeout headers in their LOCK requests.
				// However, the server is not required to honor or even consider these requests.
				
				ILock acquiredLock = lock(depth, lock_type, lock_scope, lock_owner, uri);
				
				// Get the token for the first (and only) locked resource.
				Iterator iterator = acquiredLock.resourceIterator();
				String token = ((IResourceLockInfo)(iterator.next())).getLockToken();
				
				response.setHeader(HTTP.HEADER_LOCKTOKEN, addTokenDelimiters(token)); // Don't need delimiters according to spec, but Apache does it.
				
				response.setContentType(HTTP.CONTENT_TYPE_XML);
				response.setStatusCode(HTTP.RESPONSE_MULTI_STATUS);
				response.setStatusCode(HTTP.RESPONSE_OK);
				
				Element properties = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_PROP );
				properties.setAttribute(WebDAV.DAV_XML_NS_PREFIX, WebDAV.DAV_NS);
				
				Element lockDiscovery = XMLLockPropertiesGen.createXMLLockProperties(acquiredLock);
				
				properties.appendChild(lockDiscovery);
				
				xml_helper.writeXML(properties, response.getOutputWriter());
				response.close();
			}
			else throw new HTTPException("No data sent", HTTP.RESPONSE_BAD_REQUEST, false);
		}
		catch (LockException e) { throw new HTTPException("Lock refused: " + e.getMessage(), HTTP.RESPONSE_LOCKED, true); }
	}

	private String extractLockScope(Node next_element) throws HTTPException {

		return extractLockProperty(next_element, "lock scope", "Lock scope neither shared nor exclusive", new Predicate() {
			public boolean holds(String lock_scope) { return lock_scope.equals(WebDAV.DAV_EXCLUSIVE) && ! lock_scope.equals(WebDAV.DAV_SHARED); }
		});
    }

	private String extractLockType(Node next_element) throws HTTPException {
		
		return extractLockProperty(next_element, "lock type", "unrecognised lock type", new Predicate() {
			public boolean holds(String lock_type) { return lock_type.equals( WebDAV.DAV_WRITE); }
		});
	}
	
	private String extractLockOwner(String lock_owner, Node next_element) throws HTTPException {
		
	    if (next_element.hasChildNodes()) {							 // must be at least 1 child
	    	
	    	NodeList lockChildren = next_element.getChildNodes();
	    	int lockChildrenLength = lockChildren.getLength();
	    	
	    	switch (lockChildrenLength) {
	    		
	    		case 0: {
	    			throw new HTTPException("No child nodes found in owner element", HTTP.RESPONSE_BAD_REQUEST, true);
	    			// Al's paranoia - never reached
	    		}
	    		
	    		case 1: {
	    			
	    			// This is the case on Windows, where the <owner> element contains just the user name.
	    			Node lockChild = lockChildren.item(0);
	    			lock_owner = lockChild.getNodeValue();
	    			
	    			Diagnostic.trace("Set lock owner to: " + lock_owner, Diagnostic.RUN);
	    			break;
	    		}
	    		
	    		default: {
	    			
	    			// Cope with ANY content in <owner> element - on Mac contains 3 child nodes including <href>.
	    			
	    			StringBuffer buffer = new StringBuffer();
	    			
	    			for (int x = 0; x < lockChildrenLength; x++) {
	    				Node child = lockChildren.item(x);
	    				
	    				// Ignore the child if it's not an element.
	    				if (child instanceof Element) {
	    					
	    					String flattened_child = xml_helper.flattenXML((Element) child);
	    					buffer.append(flattened_child);
	    				}
	    			}
	    			lock_owner = buffer.toString();
	    			
	    			Diagnostic.trace("Set lock owner to: " + lock_owner, Diagnostic.RUN);
	    		}
	    	}
	    }
	    return lock_owner;
    }

	private String extractLockProperty(Node next_element, String element_name, String predicate_error_message, Predicate predicate) throws HTTPException {
		
		if (next_element.hasChildNodes()) {							 // must be at least 1 child
			
			NodeList lockChildren = next_element.getChildNodes();
			int lockChildrenLength = lockChildren.getLength();
			
			for (int j = 0; j < lockChildrenLength; j++) {
				Node lockChild = lockChildren.item(j);
				
				if (lockChild.getNodeType() == Node.ELEMENT_NODE) { // skip over white space elements
					
					String property = lockChild.getLocalName();
					if (! predicate.holds(property)) throw new HTTPException(predicate_error_message, HTTP.RESPONSE_BAD_REQUEST, true);
					return property;
				}
			}
			throw new HTTPException("No content found in " + element_name + " element", HTTP.RESPONSE_BAD_REQUEST, true);
		}
		else throw new HTTPException("No content found in " + element_name + " element", HTTP.RESPONSE_BAD_REQUEST, true);
	}
	
	private ILock lock(int lock_depth, String lock_type, String lock_scope, String lock_owner, URI resource) throws LockException, HTTPException {
		
		LockScope scope;
		LockType type;
		LockDepth depth;
		
		if (lock_scope.equals(WebDAV.DAV_EXCLUSIVE))	scope = LockScope.LOCK_SCOPE_EXCLUSIVE;
		else if (lock_scope.equals( WebDAV.DAV_SHARED)) scope = LockScope.LOCK_SCOPE_SHARED;
		else throw new HTTPException("Lock scope neither shared nor exclusive", HTTP.RESPONSE_BAD_REQUEST, true);
		
		if (lock_type.equals(WebDAV.DAV_WRITE)) type = LockType.LOCK_TYPE_WRITE;
		else throw new HTTPException("Lock type not write", HTTP.RESPONSE_BAD_REQUEST, true);
		
		if (lock_depth == 0) depth = LockDepth.LOCK_DEPTH_ZERO;
		else if (lock_depth == Integer.MAX_VALUE) depth = LockDepth.LOCK_DEPTH_INFINITY;
		else throw new HTTPException("Lock depth neither zero nor infinity", HTTP.RESPONSE_BAD_REQUEST, true);
		
		ILock new_lock = lock_manager.newLock(lock_owner, HTTP.HEADER_OPAQUELOCKTOKEN + ":");
		lock_manager.addResourceToLock(new_lock, resource, scope, type, depth);
		
		return new_lock;
	}
	
	interface Predicate {
		
		boolean holds(String property);
	}
}
