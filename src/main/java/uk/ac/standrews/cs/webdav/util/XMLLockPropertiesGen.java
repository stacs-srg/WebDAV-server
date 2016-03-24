package uk.ac.standrews.cs.webdav.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import uk.ac.standrews.cs.locking.impl.LockDepth;
import uk.ac.standrews.cs.locking.impl.LockScope;
import uk.ac.standrews.cs.locking.interfaces.ILock;
import uk.ac.standrews.cs.locking.interfaces.IResourceLockInfo;
import uk.ac.standrews.cs.webdav.impl.WebDAV;

import java.util.Iterator;

/**
 * This class produces XML properties for a lock or set of locks.
 * 
 * @author al, graham
 */
public class XMLLockPropertiesGen {

	/**
	 * @param lock - a lock to be rendered in XML
	 * @return an XML Element containing stuff like:
	 * <D:lockdiscovery>
	 *  <D:activelock>
	 *      <D:locktype><D:write/></D:locktype>
	 *          <D:lockscope><D:exclusive/></D:lockscope>
	 *              <D:depth>0</D:depth>
	 *              <D:owner>Jane Smith</D:owner>
	 *              <D:timeout>Infinite</D:timeout>
	 *              <D:locktoken>
	 *                  <D:href>
	 *                      opaquelocktoken:f81de2ad-7f3d-a1b2-4f3c-00a0c91a9d76
	 *                  </D:href>
	 *              </D:locktoken>
	 *  </D:activelock>
	 *  <D:activelock>
	 *      <D:locktype><D:write/></D:locktype>
	 *          <D:lockscope><D:exclusive/></D:lockscope>
	 *              <D:depth>0</D:depth>
	 *              <D:owner>Jane Smith</D:owner>
	 *              <D:timeout>60</D:timeout>
	 *              <D:locktoken>
	 *                  <D:href>
	 *                      opaquelocktoken:15263252-2736-1bb8-7121-00a0c91a9d76
	 *                  </D:href>
	 *              </D:locktoken>
	 *  </D:activelock>
	 * </D:lockdiscovery>
	 */
	public static Element createXMLLockProperties(ILock lock) {

		XMLHelper xml_helper = XMLHelper.getInstance();

		Element lockdiscoveryEL = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKDISCOVERY);
		
		appendElementsForSingleLock(lock, lockdiscoveryEL);
		
		return lockdiscoveryEL;
	}

	/**
	 * @param iterator over ILock instances
	 */
	public static Element createXMLLockProperties(Iterator iterator) {
		
		XMLHelper xml_helper = XMLHelper.getInstance();
		
		Element lockdiscoveryEL = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKDISCOVERY);
		
		while (iterator.hasNext()) {
			
			ILock lock = (ILock)iterator.next();
			appendElementsForSingleLock(lock, lockdiscoveryEL);
		}
		
		return lockdiscoveryEL;
	}

	public static void appendElementsForSingleLock(ILock lock, Element parent) {

		XMLHelper xml_helper = XMLHelper.getInstance();

		String lock_owner = lock.getOwner();

		Iterator iterator = lock.resourceIterator();
		while (iterator.hasNext()) {

			IResourceLockInfo resource_lock_info = (IResourceLockInfo) iterator.next();
			
			Node activelockEL =  xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_ACTIVELOCK);
			Node lockTypeEL =    xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKTYPE);
			Node lockTypeValue = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_WRITE);
			Node lockScopeEL =   xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKSCOPE);
			Node timeOutEL =     xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_TIMEOUT, "Infinite");

			String elementName;
			if (resource_lock_info.getScope().equals(LockScope.LOCK_SCOPE_EXCLUSIVE))
				elementName = WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_EXCLUSIVE;
			else
				elementName = WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_SHARED;
			
			Node lockScopeValue = xml_helper.createElement(WebDAV.DAV_NS, elementName);

			String lock_depth_text;
			if (resource_lock_info.getDepth() == LockDepth.LOCK_DEPTH_INFINITY)
				lock_depth_text = "infinity";
			else
				lock_depth_text = "0";

			Node lockdepthEL = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_DEPTH, lock_depth_text);
			Node locktokenEL = xml_helper.createElement(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_LOCKTOKEN);
			String token = resource_lock_info.getLockToken();
			Node hrefEL = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_HREF, token);

			activelockEL.appendChild(lockTypeEL);
			lockTypeEL.appendChild(lockTypeValue);
			activelockEL.appendChild(lockScopeEL);
			lockScopeEL.appendChild(lockScopeValue);
			activelockEL.appendChild(lockdepthEL);
			if (lock_owner != null) { // this is optional - see spec above
				Node lockownerEL = xml_helper.createElementWithText(WebDAV.DAV_NS, WebDAV.DAV_NS_PREFIX_ + WebDAV.DAV_OWNER, lock_owner);
				activelockEL.appendChild(lockownerEL);
			}
			activelockEL.appendChild(timeOutEL);
			activelockEL.appendChild(locktokenEL);
			locktokenEL.appendChild(hrefEL);
			parent.appendChild(activelockEL);
		}
	}
}
