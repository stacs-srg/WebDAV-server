package uk.ac.standrews.cs.webdav.impl;

/**
 * Defines constants relating to the WebDAV protocol
 *
 *
 * @author Ben Catherall, graham
 */
public class WebDAV {

    public static final String DAV_NS =            "DAV:";
    public static final String DAV_NS_PREFIX =     "D";
    public static final String DAV_NS_PREFIX_ =    "D:";
    public static final String DAV_XML_NS_PREFIX = "xmlns:" + DAV_NS_PREFIX;

    public static final String DAV_MULTI_STATUS = "multistatus";
    public static final String DAV_RESPONSE =     "response";
    public static final String DAV_HREF =         "href";
    public static final String DAV_DEPTH =        "depth";
    public static final String DAV_STATUS =       "status";
    public static final String DAV_COLLECTION =   "collection";
    public static final String DAV_SET =          "set";
    public static final String DAV_REMOVE =       "remove";
    public static final String DAV_OWNER =        "owner";
    
    public static final String DAV_PROPFIND =       "propfind";
    public static final String DAV_PROPSTAT =       "propstat";
    public static final String DAV_PROP =           "prop";
    public static final String DAV_ALLPROP =        "allprop";
    public static final String DAV_PROPNAME =       "propname";
    public static final String DAV_PROPERTYUPDATE = "propertyupdate";
    
    public static final String DAV_LOCKINFO =      "lockinfo";
    public static final String DAV_SUPPORTEDLOCK = "supportedlock";
    public static final String DAV_LOCKENTRY =     "lockentry";
    public static final String DAV_LOCKSCOPE =     "lockscope";
    public static final String DAV_TIMEOUT =       "timeout";
    public static final String DAV_LOCKTOKEN =     "locktoken";
    public static final String DAV_LOCKTYPE =      "locktype";
    public static final String DAV_LOCKDISCOVERY = "lockdiscovery";
    public static final String DAV_ACTIVELOCK =    "activelock";
    public static final String DAV_EXCLUSIVE =     "exclusive";
    public static final String DAV_SHARED =        "shared";
    public static final String DAV_WRITE =         "write";

    public static final String PROPERTY_CONTENT_LENGTH = "getcontentlength";
    public static final String PROPERTY_E_TAG =          "getetag";
    public static final String PROPERTY_RESOURCE_TYPE =  "resourcetype";
    public static final String PROPERTY_CREATION_DATE =  "creationdate";
    public static final String PROPERTY_LAST_MODIFIED =  "getlastmodified";
	public static final String PROPERTY_CONTENT_TYPE =   "getcontenttype";
	public static final String PROPERTY_DISPLAY_NAME =   "displayname";
}
