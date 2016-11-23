package uk.ac.standrews.cs.webdav.impl.methods;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.standrews.cs.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.fs.persistence.interfaces.INameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.locking.interfaces.ILockManager;
import uk.ac.standrews.cs.utils.UriUtil;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.*;
import uk.ac.standrews.cs.webdav.util.ISO8601Format;
import uk.ac.standrews.cs.webdav.util.XMLLockPropertiesGen;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Ben Catherall, al, graham
 */
public class PROPFIND extends AbstractHTTPMethod {
	
	// TODO return lock-null resources as members of collections (RFC 2518 7.4).

	private ISO8601Format iso8601format;
	private Set DEFAULT_PROPERTY_NAMES;
	
	public String getMethodName() {
		return HTTP.METHOD_PROPFIND;
	}

	public void init(IFileSystem file_system, ILockManager lock_manager) {
		
		super.init(file_system, lock_manager);
		iso8601format = new ISO8601Format();
		
		// The properties that will be returned by default if no specific properties are requested.
		DEFAULT_PROPERTY_NAMES = new HashSet();
		DEFAULT_PROPERTY_NAMES.add(WebDAV.PROPERTY_CREATION_DATE);
		DEFAULT_PROPERTY_NAMES.add(WebDAV.PROPERTY_DISPLAY_NAME);
		DEFAULT_PROPERTY_NAMES.add(WebDAV.PROPERTY_CONTENT_LENGTH);
		DEFAULT_PROPERTY_NAMES.add(WebDAV.PROPERTY_CONTENT_TYPE);
		DEFAULT_PROPERTY_NAMES.add(WebDAV.PROPERTY_E_TAG);
		DEFAULT_PROPERTY_NAMES.add(WebDAV.PROPERTY_LAST_MODIFIED);
		DEFAULT_PROPERTY_NAMES.add(WebDAV.DAV_LOCKDISCOVERY);
		DEFAULT_PROPERTY_NAMES.add(WebDAV.PROPERTY_RESOURCE_TYPE);
		DEFAULT_PROPERTY_NAMES.add(WebDAV.DAV_SUPPORTEDLOCK);
	}

	/**
	 * All properties returned by Apache for /asa/:
	 * 
	 * creationdate	  Y
	 * getlastmodified   Y
	 * getetag		   Y
	 * supportedlock	 N
	 * lockdiscovery	 N
	 * resourcetype	  Y
	 * getcontenttype	N
	 */
	public void execute(Request request, Response response) throws IOException, HTTPException {
		
		URI uri = request.getUri();
		
		// Read the request contents to see what is requested.
		PropFindRequest requested_property_names = getRequestedPropertyNames(request);
		
		// Get the depth required.
		int depth = getDepth(request);
		
		response.setContentType(HTTP.CONTENT_TYPE_XML);
		response.setStatusCode(HTTP.RESPONSE_MULTI_STATUS);
		
		Element multistatus_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_MULTI_STATUS);
		multistatus_element.setAttribute(WebDAV.DAV_XML_NS_PREFIX, WebDAV.DAV_NS);
		
		// Test whether URI corresponds to extant file system object or not (lock-null resource).
		IAttributedStatefulObject object = file_system.resolveObject(uri);

		if (object == null) {
			
			// Only return properties for non-extant object if the URI has been locked.
			if (lock_manager.locked(uri)) propFindUri(multistatus_element, uri, request, requested_property_names);
			else throw new HTTPException("Object '" + uri + "' not found.", HTTP.RESPONSE_NOT_FOUND, false);
		}
		else propFindObject(multistatus_element, object, uri, depth, request, requested_property_names);
		
		xml_helper.writeXML(multistatus_element, response.getOutputWriter());
		
		response.setChunked(true);
		response.close();
	}

	private PropFindRequest getRequestedPropertyNames(Request request) throws IOException, HTTPException {
		
		if (request.hasContent()) {
			
			/* Examples:
			 
			   <D:propfind xmlns:D="DAV:">
				  <D:prop>
					 <D:getlastmodified/>
					 <D:getcontentlength/>
					 <D:resourcetype/>
				  </D:prop>
			   </D:propfind>
			 
			   <D:propfind xmlns:D="DAV:">
				  <D:allprop/>
			   </D:propfind>
			 
			   <D:propfind xmlns:D="DAV:">
				  <D:propname/>
			   </D:propfind>
			 */
			
			Document d = xml_helper.parse(new LengthInputStream(request.getInputStream(), request.getContentLength()));
			NodeList propfindNL = d.getElementsByTagNameNS(WebDAV.DAV_NS, WebDAV.DAV_PROPFIND);
			
			if (propfindNL.getLength() == 1) {
				
				Node node = propfindNL.item(0).getFirstChild();   // <prop>, <allprop> or <propname> element
				
				node = nextSiblingElement(node);
				
				if (node == null) throw new HTTPException("Invalid PROPFIND request body: <propfind> element contains no <prop>, <allprop> or <propname> elements");
				
				// If <allprop> or <propname> specified, return the element name.
				String node_name = node.getLocalName();
				
				if (node_name.equals(WebDAV.DAV_ALLPROP) || node_name.equals(WebDAV.DAV_PROPNAME)) return new PropFindRequest(node_name);
				else {
					
					Set property_names = new HashSet();
					
					node = node.getFirstChild();
					node = nextSiblingElement(node);
					
					do {
						if (node == null) throw new HTTPException("Invalid PROPFIND request body: <prop> element contains no property element");
						
						String property_name = node.getLocalName();
						property_names.add(property_name);
						
						node = node.getNextSibling();
						node = nextSiblingElement(node);
					}
					while (node != null);
					
					return new PropFindRequest(WebDAV.DAV_PROP, property_names);
				}
			}
			else throw new HTTPException("Invalid PROPFIND request body: zero or multiple <propfind> elements");
			
		}
		else return new PropFindRequest(WebDAV.DAV_ALLPROP);	  // Treat an empty request as a request for all properties.
	}

	private Node nextSiblingElement(Node node) {
		while (node != null && node.getNodeType() != Node.ELEMENT_NODE) node = node.getNextSibling();	 // Skip whitespace.
		return node;
	}

	private int getDepth(Request request) throws HTTPException {
		
		int depth = Integer.MAX_VALUE;
		String depth_header = request.getHeader(HTTP.HEADER_DEPTH);
		
		if (depth_header != null && !depth_header.equals(HTTP.HEADER_TOKEN_INFINITY)) {

			try { depth = Integer.parseInt(depth_header); }
			catch (NumberFormatException e) { throw new HTTPException("Invalid depth header"); }
		}
		
		if (depth != 0 && depth != 1 && depth != Integer.MAX_VALUE) throw new HTTPException("Invalid depth header (must be 0 / 1 / infinity)");
		return depth;
	}

	private void addCreationDateProperty(Element prop_element_OK, Element prop_element_NOT_FOUND, IAttributedStatefulObject object, boolean property_explicitly_requested, boolean value_requested) {
		
		Element property_element;
		
		if (value_requested) {
			
			// Creation date must be specified in ISO8601 format.
			String creation_date_as_ISO8601 = "?";
			try {
				creation_date_as_ISO8601 = iso8601format.format(new Date(object.getCreationTime()));
			} catch (AccessFailureException e) {
				// TODO do something if the getCreationTime fails
				e.printStackTrace();
			}
			
			property_element = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_CREATION_DATE, creation_date_as_ISO8601);
		}
		else property_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_CREATION_DATE);
		
		prop_element_OK.appendChild(property_element); 
	}

	private void addLastModifiedProperty(Element prop_element_OK, Element prop_element_NOT_FOUND, IAttributedStatefulObject object, boolean property_explicitly_requested, boolean value_requested) {
		
		Element property_element;
		
		if (value_requested) {
			
			// Modification date must be specified in RFC2068 format.
			String modification_date_as_RFC2068 = "?";
			try {
				modification_date_as_RFC2068 = HTTP.HTTP_DATE(new Date(object.getModificationTime()));
			} catch (AccessFailureException e) {
				// TODO do something if the getModificationTime fails
				e.printStackTrace();
			}
			
			property_element = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_LAST_MODIFIED, modification_date_as_RFC2068);
		}
		else property_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_LAST_MODIFIED);
		
		prop_element_OK.appendChild(property_element);
	}

	private void addETagProperty(Element prop_element_OK, Element prop_element_NOT_FOUND, IAttributedStatefulObject object, boolean property_explicitly_requested, boolean value_requested) {
		
		Element property_element;
		
		if (value_requested) {
			String tag = quote(object.getGUID().toString());
			property_element = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_E_TAG, tag);
		} else {
			property_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_E_TAG);
		}

		prop_element_OK.appendChild(property_element);
	}

	private void addResourceTypeProperty(Element prop_element_OK, Element prop_element_NOT_FOUND, IAttributedStatefulObject object, boolean property_explicitly_requested, boolean value_requested) {
		
		Element property_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_RESOURCE_TYPE);
		
		if (value_requested) {
			
			if (object instanceof IDirectory) property_element.appendChild(xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_COLLECTION));
		}
			
		prop_element_OK.appendChild(property_element);
	}

	private void addContentLengthProperty(Element prop_element_OK, Element prop_element_NOT_FOUND, IAttributedStatefulObject object, boolean property_explicitly_requested, boolean value_requested) {
		
		if (object instanceof IFile) {
			
			Element property_element;
			
			if (value_requested) {
				
				long file_length = ((IFile) object).reify().getSize();
				
				property_element = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_CONTENT_LENGTH, String.valueOf(file_length));
			}
			else property_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_CONTENT_LENGTH);
			
			prop_element_OK.appendChild(property_element);
		}
		else {
			// Only return a 404 if this property was explicitly requested (as opposed to all properties being returned for an empty request).
			if (property_explicitly_requested) prop_element_NOT_FOUND.appendChild(xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_CONTENT_LENGTH));
		}
	}
	
	private void addSupportedLockProperty(Element prop_element_OK, boolean value_requested) {
		
		Element property_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_SUPPORTEDLOCK);
		
		if (value_requested) {
			
			/* XML to produce:
			
			<!ELEMENT supportedlock (lockentry)* >
			<!ELEMENT lockentry (lockscope, locktype) >
			<!ELEMENT lockinfo (lockscope, locktype, owner?) >
			<!ELEMENT locktype (write) >
			<!ELEMENT write EMPTY >
			<!ELEMENT lockscope (exclusive | shared) >
			<!ELEMENT exclusive EMPTY >
			<!ELEMENT shared EMPTY >
	
			Example:
			
			<d:supportedlock>
				<d:lockentry>
					<d:lockscope>
						<d:exclusive />
					</d:lockscope>
					<d:locktype>
						<d:write />
					</d:locktype>
				</d:lockentry>
				<d:lockentry>
					<d:lockscope>
						<d:shared />
					</d:lockscope>
					<d:locktype>
						<d:write />
					</d:locktype>
				</d:lockentry>
			</d:supportedlock>
			*/
			
			// Always support both exclusive-write and shared-write locks.
			Element exclusive_write_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKENTRY);
			Element exclusive_write_scope_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKSCOPE);
			Element exclusive_write_scope_value_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_EXCLUSIVE);
			exclusive_write_scope_element.appendChild(exclusive_write_scope_value_element);
			exclusive_write_element.appendChild(exclusive_write_scope_element);
			Element exclusive_write_type_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKTYPE);
			Element exclusive_write_type_value_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_WRITE);
			exclusive_write_type_element.appendChild(exclusive_write_type_value_element);
			exclusive_write_element.appendChild(exclusive_write_type_element);
			Element shared_write_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKENTRY);
			Element shared_write_scope_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKSCOPE);
			Element shared_write_scope_value_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_SHARED);
			shared_write_scope_element.appendChild(shared_write_scope_value_element);
			shared_write_element.appendChild(shared_write_scope_element);
			Element shared_write_type_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKTYPE);
			Element shared_write_type_value_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_WRITE);
			shared_write_type_element.appendChild(shared_write_type_value_element);
			shared_write_element.appendChild(shared_write_type_element);
			
			property_element.appendChild(exclusive_write_element);
			property_element.appendChild(shared_write_element);
		}
		
		prop_element_OK.appendChild(property_element);
	}

	private void addLockDiscoveryProperty(Element prop_element_OK, Element prop_element_NOT_FOUND, URI uri, boolean property_explicitly_requested, boolean value_requested) {
		
		if (value_requested) {
			
			Iterator iterator = lock_manager.lockIterator(uri);
			prop_element_OK.appendChild(XMLLockPropertiesGen.createXMLLockProperties(iterator));
		}
		else {
		
			Element property_node = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKDISCOVERY);
			prop_element_OK.appendChild(property_node);
		}
	}

	private void addContentTypeProperty(Element prop_element_OK, Element prop_element_NOT_FOUND, IAttributedStatefulObject object, boolean property_explicitly_requested, boolean value_requested) {
		
		Element property_element;
		
		if (value_requested) {
			
			String content_type;
			if (object instanceof IDirectory) content_type = HTTP.CONTENT_TYPE_HTTPD_UNIX_DIRECTORY;
			else {
				IAttributes attributes = object.getAttributes();
				content_type = attributes.get(FileSystemConstants.CONTENT);
				if (content_type == null) content_type = "CONTENT TYPE NOT FOUND";
			}
	
			property_element = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_CONTENT_TYPE, content_type);
		}
		else property_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_CONTENT_TYPE);
			
		prop_element_OK.appendChild(property_element);
	}
	
	private void addDisplayNameProperty(Element prop_element_OK, Element prop_element_NOT_FOUND, IAttributedStatefulObject object, Request request, boolean property_explicitly_requested, boolean value_requested) {
		
		Element property_element;

		if (value_requested) property_element = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_DISPLAY_NAME, request.getUri().getPath());
		else				    property_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.PROPERTY_DISPLAY_NAME);
		
		prop_element_OK.appendChild(property_element);
	}

	private void addUnknownProperty(String property_name, Element prop_element_NOT_FOUND, boolean property_explicitly_requested) {
		
		// Only return a 404 if this property was explicitly requested (as opposed to all properties being returned for an empty request).
		if (property_explicitly_requested) prop_element_NOT_FOUND.appendChild(xml_helper.createElement(WebDAV.DAV_NS, property_name));
	}

	private void propFindObject(Element multistatus_element, IAttributedStatefulObject object, URI uri, int depth, Request request, PropFindRequest requested_property_names) {
		
		propFind(multistatus_element, object, uri, depth, request, requested_property_names);
	}
	
	private void propFindUri(Element multistatus_element, URI uri, Request request, PropFindRequest requested_property_names) {
		
		propFind(multistatus_element, null, uri, 0, request, requested_property_names);
	}

	private void propFind(Element multistatus_element, IAttributedStatefulObject object, URI uri, int depth, Request request, PropFindRequest requested_property_names) {
		
		/* XML to produce:
		
		<!ELEMENT multistatus (response+, responsedescription?) >
		<!ELEMENT response (href, ((href*, status)|(propstat+)), responsedescription?) >
		<!ELEMENT status (#PCDATA) >
		<!ELEMENT propstat (prop, status, responsedescription?) >
		<!ELEMENT responsedescription (#PCDATA) >
		<!ELEMENT prop ANY >

		Example:
		
			<D:multistatus>
				<D:response>
					<D:href>/asa/</D:href>
					<D:propstat>
						<D:prop>
							<getlastmodified>Fri, 26 Aug 2005 13:05:00 GMT</getlastmodified>
							<D:resourcetype><D:collection/></D:resourcetype>
						</D:prop>
						<D:status>HTTP/1.1 200 OK</D:status>
					</D:propstat>
					<D:propstat>
						<D:prop>
							<getcontentlength/>
						</D:prop>
						<D:status>HTTP/1.1 404 Not Found</D:status>
					</D:propstat>
				</D:response>
			</D:multistatus>
			
		 */
		
		Element response_element = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_RESPONSE);
		Element href_element = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_HREF, uri.toString());
		
		response_element.appendChild(href_element);
		
		if (object != null) addObjectProperties(object, uri, response_element, requested_property_names, request); // This will append one or more <propstat> child elements to the <response> element.
		else                addUriProperties(uri, response_element, requested_property_names, request);
		
		multistatus_element.appendChild(response_element);
		
		// Now traverse children if this is a directory and depth > 0.
		if (depth > 0 && object instanceof IDirectory) {
			
			// Define depth to which children should be traversed in turn.
			int child_depth;
			if (depth == 1) child_depth = 0;
			else            child_depth = Integer.MAX_VALUE;
			
			Iterator directory_entry_iterator = ((IDirectory) object).iterator();
			
			while (directory_entry_iterator.hasNext()) {
				
				INameAttributedPersistentObjectBinding binding = (INameAttributedPersistentObjectBinding) directory_entry_iterator.next();

				if (binding == null) {
					System.err.println("Binding is null for contents in URI: " + uri.toString());
					continue;
				}

				String name = binding.getName();
				IAttributedStatefulObject child = binding.getObject();
				URI child_uri = UriUtil.childUri(uri, name, false);
				
				propFindObject(multistatus_element, child, child_uri, child_depth, request, requested_property_names);
			}
			
		    // Also return info on lock-null resources.
			addLockNullProperties(multistatus_element, object, uri, request, requested_property_names);
		}
	}

	private void addLockNullProperties(Element multistatus_element, IAttributedStatefulObject object, URI uri, Request request, PropFindRequest requested_property_names) {
		
		Iterator uri_iterator = lock_manager.uriIterator();
		
		while (uri_iterator.hasNext()) {
			
			URI locked_uri = (URI) uri_iterator.next();
			
			if (UriUtil.parentUri(locked_uri).equals(uri)) {
				
				// This locked URI has the original URI has parent - check for lock-null resource.
				String base_name = UriUtil.baseName(locked_uri);
				
				// Return info on URI if it is lock-null - no corresponding extant object.
				if (((IDirectory)object).get(base_name) == null)
					propFindUri(multistatus_element, locked_uri, request, requested_property_names);
			}
		}
	}
	
	private void addObjectProperties(IAttributedStatefulObject object, URI uri, Element response_element, PropFindRequest prop_find_request, Request request) {
		
		addProperties(object, uri, response_element, prop_find_request, request);
	}
	
	private void addUriProperties(URI uri, Element response_element, PropFindRequest prop_find_request, Request request) {
		
		addProperties(null, uri, response_element, prop_find_request, request);
	}
	
	private void addProperties(IAttributedStatefulObject object, URI uri, Element response_element, PropFindRequest prop_find_request, Request request) {
		
		Element propstat_element_OK = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_PROPSTAT);
		Element prop_element_OK = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_PROP);
		propstat_element_OK.appendChild(prop_element_OK);
		
		Element propstat_element_NOT_FOUND = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_PROPSTAT);
		Element prop_element_NOT_FOUND = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_PROP);
		propstat_element_NOT_FOUND.appendChild(prop_element_NOT_FOUND);
		
		setStatus(request, propstat_element_OK, HTTP.RESPONSE_OK);
		setStatus(request, propstat_element_NOT_FOUND, HTTP.RESPONSE_NOT_FOUND);
		
		// If the set of requested property names is empty, return them all.
		Set property_names = prop_find_request.requested_property_names;
		boolean properties_explicitly_requested = prop_find_request.request_element.equals(WebDAV.DAV_PROP);
		boolean values_requested = !prop_find_request.request_element.equals(WebDAV.DAV_PROPNAME);
		if (property_names == null || !properties_explicitly_requested) property_names = DEFAULT_PROPERTY_NAMES;
		
		Iterator iterator = property_names.iterator();
		while (iterator.hasNext()) {
			String property_name = (String) iterator.next();
			if (object != null) addObjectProperty(property_name, object, uri, request, prop_element_OK, prop_element_NOT_FOUND, properties_explicitly_requested, values_requested);
			else                addUriProperty(property_name, uri, request, prop_element_OK, prop_element_NOT_FOUND, properties_explicitly_requested, values_requested);
		}
		
		// Only add each <propstat> element if there are some properties with the corresponding code.
		if (prop_element_OK.getFirstChild() != null)		response_element.appendChild(propstat_element_OK);
		if (prop_element_NOT_FOUND.getFirstChild() != null) response_element.appendChild(propstat_element_NOT_FOUND);
	}
	
	private void addObjectProperty(String property_name, IAttributedStatefulObject object, URI uri, Request request, Element prop_element_OK, Element prop_element_NOT_FOUND, boolean property_explicitly_requested, boolean value_requested) {
		
		// Based on object.
		     if (property_name.equals(WebDAV.PROPERTY_CREATION_DATE))  addCreationDateProperty(prop_element_OK, prop_element_NOT_FOUND, object, property_explicitly_requested, value_requested);
		else if (property_name.equals(WebDAV.PROPERTY_DISPLAY_NAME))   addDisplayNameProperty(prop_element_OK, prop_element_NOT_FOUND, object, request, property_explicitly_requested, value_requested);
		else if (property_name.equals(WebDAV.PROPERTY_CONTENT_LENGTH)) addContentLengthProperty(prop_element_OK, prop_element_NOT_FOUND, object, property_explicitly_requested, value_requested);
		else if (property_name.equals(WebDAV.PROPERTY_CONTENT_TYPE))   addContentTypeProperty(prop_element_OK, prop_element_NOT_FOUND, object, property_explicitly_requested, value_requested);
		else if (property_name.equals(WebDAV.PROPERTY_E_TAG))		    addETagProperty(prop_element_OK, prop_element_NOT_FOUND, object, property_explicitly_requested, value_requested);
		else if (property_name.equals(WebDAV.PROPERTY_LAST_MODIFIED))  addLastModifiedProperty(prop_element_OK, prop_element_NOT_FOUND, object, property_explicitly_requested, value_requested);
		else if (property_name.equals(WebDAV.PROPERTY_RESOURCE_TYPE))  addResourceTypeProperty(prop_element_OK, prop_element_NOT_FOUND, object, property_explicitly_requested, value_requested);
		     
		// Based on URI.
		else addUriProperty(property_name, uri, request, prop_element_OK, prop_element_NOT_FOUND, property_explicitly_requested, value_requested);
	}

	private void addUriProperty(String property_name, URI uri, Request request, Element prop_element_OK, Element prop_element_NOT_FOUND, boolean property_explicitly_requested, boolean value_requested) {
		
		// Based on URI.	
		     if (property_name.equals(WebDAV.DAV_LOCKDISCOVERY))	addLockDiscoveryProperty(prop_element_OK, prop_element_NOT_FOUND, uri, property_explicitly_requested, value_requested);
		else if (property_name.equals(WebDAV.DAV_SUPPORTEDLOCK))	addSupportedLockProperty(prop_element_OK, value_requested);
	     
		// Unknown.
		else													    addUnknownProperty(property_name, prop_element_NOT_FOUND, property_explicitly_requested);
	}

	private void setStatus(Request request, Element propstat_element, int code) {
		
		String http_version =	 request.getVersion() == HTTP.HTTP_11 ? HTTP.REQUEST_HTTP_11 : HTTP.REQUEST_HTTP_10;
		String code_description = HTTP.HTTP_BUNDLE_STRING("T" + code);
		String status =		   http_version + " " + code + " " + code_description;
		
		Element status_element = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_STATUS, status);
		propstat_element.appendChild(status_element);
	}

	private String quote(String s) {
		
		return "\"" + s + "\"";
	}
	
	private class PropFindRequest {
		
		protected String request_element;
		protected Set requested_property_names;
		
		protected PropFindRequest(String request_element, Set requested_property_names) {
			this.request_element = request_element;
			this.requested_property_names = requested_property_names;
		}
		
		protected PropFindRequest(String request_element) {
			this(request_element, null);
		}
	}
}
