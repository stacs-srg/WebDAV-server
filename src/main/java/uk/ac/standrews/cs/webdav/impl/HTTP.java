package uk.ac.standrews.cs.webdav.impl;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpMethodParams;
import uk.ac.standrews.cs.utils.Error;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.impl.methods.*;
import uk.ac.standrews.cs.webdav.interfaces.HTTPMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HTTP constants and static helper functions for parsing URIs, headers, etc
 * <p/>
 * Initialises the internationalized message bundles
 * <p/>
 * Acts as registry for HTTP method classes
 *
 * @author Ben Catherall
 * @version 2005-03-29
 * @see #METHODS
 * @see #BUNDLE
 */
public class HTTP {

    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HTTP.class.getName());

    public static final int HTTP_UNKNOWN = 0;
    public static final int HTTP_09 = 1;
    public static final int HTTP_10 = 2;
    public static final int HTTP_11 = 3;

    public static final String REQUEST_HTTP_11 = "HTTP/1.1";
    public static final String REQUEST_HTTP_10 = "HTTP/1.0";
    public static final String REQUEST_HTTP_09 = "HTTP/0.9";

    public static final int RESPONSE_OK =                    200;
    public static final int RESPONSE_CREATED =               201;
    public static final int RESPONSE_NO_CONTENT =            204;
    public static final int RESPONSE_PARTIAL_CONTENT =       206;
    public static final int RESPONSE_MULTI_STATUS =          207;
    public static final int RESPONSE_MOVED_PERMENANTLY =     301;
    public static final int RESPONSE_MOVED_TEMPORARILY =     302;
    public static final int RESPONSE_BAD_REQUEST =           400;
    public static final int RESPONSE_FORBIDDEN =             403;
    public static final int RESPONSE_NOT_FOUND =             404;
    public static final int RESPONSE_METHOD_NOT_ALLOWED =    405;
    public static final int RESPONSE_CONFLICT =              409;
    public static final int RESPONSE_PRECONDITION_FAILED =   412;
    public static final int RESPONSE_LOCKED =                423;
    public static final int RESPONSE_INTERNAL_SERVER_ERROR = 500;
    public static final int RESPONSE_NOT_IMPLEMENTED =       501;
    public static final int RESPONSE_SERVICE_UNAVAILABLE =   503;
    public static final int RESPONSE_VERSION_NOT_SUPPORTED = 505;
    public static final int RESPONSE_INSUFFICIENT_STORAGE =  507;

    public static final String PARAMETER_INFO = "info";

    public static final String HEADER_CONTENT_LENGTH =    "Content-Length";
    public static final String HEADER_CONNECTION =        "Connection";
    public static final String HEADER_TOKEN_CLOSE =       "close";
    public static final String HEADER_TOKEN_KEEP_ALIVE =  "keep-alive";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEADER_TOKEN_CHUNKED =     "chunked";
    public static final String HEADER_DATE =              "Date";
    public static final String HEADER_HOST =              "Host";
    public static final String HEADER_DESTINATION =       "Destination";
    public static final String HEADER_SERVER =            "Server";
    public static final String HEADER_USER_AGENT =        "User-Agent";
    public static final String HEADER_CONTENT_TYPE =      "Content-Type";
    public static final String HEADER_LOCATION =          "Location";
    public static final String HEADER_LOCKTOKEN =         "Lock-Token";
    public static final String HEADER_IF =                "If";
    public static final String HEADER_DAV =               "DAV";
    public static final String HEADER_ALLOW =             "Allow";
    public static final String HEADER_DEPTH =             "Depth";
    public static final String HEADER_OVERWRITE =         "Overwrite";
    public static final String HEADER_TOKEN_F =           "F";
    public static final String HEADER_TOKEN_T =           "T";
    public static final String HEADER_TOKEN_INFINITY =    "infinity";
    public static final String HEADER_MS_AUTHOR_VIA =     "MS-Author-Via";
    public static final String HEADER_OPAQUELOCKTOKEN =   "opaquelocktoken";
    public static final String HEADER_SERVER_NAME =       BUNDLE.getString("server.name");

    public static final String CONTENT_TYPE_TEXT_PLAIN =           "text/plain";
    public static final String CONTENT_TYPE_HTML =                 "text/html";
    public static final String CONTENT_TYPE_XML =                  "text/xml; charset=\"utf-8\"";
    public static final String CONTENT_TYPE_UNKNOWN =              "application/octet-stream";
    public static final String CONTENT_TYPE_HTTPD_UNIX_DIRECTORY = "httpd/unix-directory";

    public static final String METHOD_GET =       "GET";
    public static final String METHOD_HEAD =      "HEAD";
    public static final String METHOD_PUT =       "PUT";
    public static final String METHOD_MKCOL =     "MKCOL";
    public static final String METHOD_DELETE =    "DELETE";
    public static final String METHOD_MOVE =      "MOVE";
    public static final String METHOD_COPY =      "COPY";
    public static final String METHOD_LOCK =      "LOCK";
    public static final String METHOD_UNLOCK =    "UNLOCK";
    public static final String METHOD_OPTIONS =   "OPTIONS";
    public static final String METHOD_PROPFIND =  "PROPFIND";
    public static final String METHOD_PROPPATCH = "PROPPATCH";
    public static final String METHOD_POST =      "POST";

    public static final Map RESPONSE_TITLES = new HashMap();	// <Integer, byte[]>
    public static final Map RESPONSE_MESSAGES = new HashMap();	// <Integer, byte[]>

    public static final DateFormat HEADER_DATE_FORMAT;
    
    public static final Map METHODS = new HashMap();	// <String, HTTPMethod>
    public static String[] NON_CHUNKED_AGENTS;
    public static HttpClient HTTP_CLIENT;

    private static final char STATUS_CODE_CHAR = 'T';
    private static final char STATUS_MESSSAGE_CHAR = 'M';

    static {
        Enumeration elements = BUNDLE.getKeys();	// of <String>
        while (elements.hasMoreElements()) {
            String key = (String) elements.nextElement();
            if (key.charAt(0) == STATUS_CODE_CHAR) {
                RESPONSE_TITLES.put(new Integer(key.substring(1)), BUNDLE.getString(key).getBytes());
            } else if (key.charAt(0) == STATUS_MESSSAGE_CHAR) {
                RESPONSE_MESSAGES.put(new Integer(key.substring(1)), BUNDLE.getString(key).getBytes());
            }
        }

        Class[] methods = {
            GET.class,
            OPTIONS.class,
            PROPFIND.class,
            MKCOL.class,
            PUT.class,
            PROPPATCH.class,
            DELETE.class,
            COPY.class,
            MOVE.class,
            HEAD.class,
            LOCK.class,
            UNLOCK.class
        };

        for(int i = 0; i < methods.length; i++) {
            Class clazz = methods[i];
            try {
                HTTPMethod method = (HTTPMethod) clazz.newInstance();
                METHODS.put(method.getMethodName(), method);
            } catch (InstantiationException | IllegalAccessException e) {
                Error.exceptionError("While initialising HTTP method of class " + clazz, e);
            }
        }

        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HTTP_CLIENT = new HttpClient(connectionManager);
        HTTP_CLIENT.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0,false));

        NON_CHUNKED_AGENTS = BUNDLE.getString("non.chunked.agents").split(",");
        
        HEADER_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        HEADER_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static final byte[] CRLF = {13, 10}; // \r\n
    public static final byte SPACE = (byte) 32;
    public static final byte[] HEADER_COLON_SPACE = {(byte) ':', (byte) ' '};
    public static final byte COLON = (byte) ':';
    public static final byte[] RESPONSE_HTTP_10 = REQUEST_HTTP_10.getBytes();
    public static final byte[] RESPONSE_HTTP_11 = REQUEST_HTTP_11.getBytes();

    public static String HTTP_DATE(Date date) {
        return HEADER_DATE_FORMAT.format(date);
    }

    public static HTTPMethod HTTP_METHOD(String name) throws HTTPException {
        HTTPMethod m = (HTTPMethod) METHODS.get(name);
        if (m == null) {
            throw new HTTPException("Method '" + name + "' not supported", RESPONSE_NOT_IMPLEMENTED);
        }
        return m;
    }

    public static String HTTP_BUNDLE_STRING(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return "key:" + key;
        }
    }

	public static List parseByteRanges(String sRanges) throws HTTPException {	// returns List<Range>
        StringTokenizer commaTokenizer = new StringTokenizer(sRanges, ",");
        List ranges = new LinkedList();			// <Range>
        while (commaTokenizer.hasMoreTokens()) {
            String sRange = commaTokenizer.nextToken();
            int pos = sRange.indexOf('-');
            try {
                String sStart = sRange.substring(0, pos);
                String sFinish = sRange.substring(pos + 1);
                Range range = new Range(Long.parseLong(sStart), Long.parseLong(sFinish));
                ranges.add(range);
            } catch (IndexOutOfBoundsException e) {
                throw new HTTPException("Invalid byte ranges header", RESPONSE_BAD_REQUEST);
            } catch (NumberFormatException e) {
                throw new HTTPException("Invalid byte ranges header", RESPONSE_BAD_REQUEST);
            }
        }
        if (ranges.size() == 0) {
            throw new HTTPException("Invalid byte ranges header (no range specified)", RESPONSE_BAD_REQUEST);
        }
        return ranges;
    }
}
