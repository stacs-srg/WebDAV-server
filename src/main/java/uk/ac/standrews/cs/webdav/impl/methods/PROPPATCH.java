package uk.ac.standrews.cs.webdav.impl.methods;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.utils.Error;
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
public class PROPPATCH extends AbstractHTTPMethod {

	public String getMethodName() {
		return HTTP.METHOD_PROPPATCH;
	}

	public void execute(Request request, Response response) throws IOException, HTTPException {
		
		Error.hardError("method not implemented - please tell Graham");
		
		// TODO actually implement the property updates...
		// TODO need to check locks
		
		URI uri = request.getUri();
		IAttributedStatefulObject object = file_system.resolveObject(uri);
		
		if (object == null) throw new HTTPException("Object '" + uri + "' not found.", HTTP.RESPONSE_NOT_FOUND, false);
		
		// Read the request contents to see if the requests asks for any property types.
		if (request.hasContent()) {
			
			Document d = xml_helper.parse(request.getInputStream());
			NodeList propertyupdateNL = d.getElementsByTagNameNS(WebDAV.DAV_NS, WebDAV.DAV_PROPERTYUPDATE);
			
			if (propertyupdateNL.getLength() == 1) {
				
				Node propertyupdate = propertyupdateNL.item(0);
				Node actionNode = propertyupdate.getFirstChild();
				
				while ((actionNode = findNextElement(actionNode, WebDAV.DAV_NS, new String[] { WebDAV.DAV_SET, WebDAV.DAV_REMOVE } )) != null) {
					if (WebDAV.DAV_SET.equals(actionNode.getLocalName())) {
						Node propNode = findNextElement(actionNode.getFirstChild(), WebDAV.DAV_NS, new String[] { WebDAV.DAV_PROP });
						
						if (propNode != null){
							propNode = propNode.getFirstChild();
							
							while ((propNode = findNextElement(propNode)) != null){
								System.out.println("set: "+propNode.getNamespaceURI()+" "+propNode.getLocalName());
								Node propChild = propNode.getFirstChild();
								
								if (propChild.getNodeType() == Node.TEXT_NODE){
									System.out.println("Text node "+propChild.getNodeValue());
								}
								propNode = propNode.getNextSibling();
							}
						}
					}
					else if (WebDAV.DAV_REMOVE.equals(actionNode.getLocalName())) {
						Node propNode = findNextElement(actionNode.getFirstChild(), WebDAV.DAV_NS, new String[] { WebDAV.DAV_PROP } );
						
						if (propNode != null){
							propNode = propNode.getFirstChild();
							
							while ((propNode = findNextElement(propNode)) != null){
								System.out.println("remove: "+propNode.getNamespaceURI()+" "+propNode.getLocalName());
								propNode = propNode.getNextSibling();
							}
						}
					}
					actionNode = actionNode.getNextSibling();
				}
			}
			else throw new HTTPException("Invalid number of propertyupdate messages sent", HTTP.RESPONSE_BAD_REQUEST, false);
		}
		else throw new HTTPException("No data sent", HTTP.RESPONSE_BAD_REQUEST, false);
	}

	private Node findNextElement(Node node, String ns, String[] names) {
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE && ns.equals(node.getNamespaceURI())) {
				for (int i = 0; i< names.length; i++) {
					String name = names[i];
					if (name.equals(node.getLocalName())) {
						return node;
					}
				}
			}
			node = node.getNextSibling();
		}
		return node;
	}

	private Node findNextElement(Node node){
		while (node != null && node.getNodeType() != Node.ELEMENT_NODE){
			node = node.getNextSibling();
		}
		return node;
	}
}
