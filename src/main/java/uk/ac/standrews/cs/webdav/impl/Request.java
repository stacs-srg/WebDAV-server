package uk.ac.standrews.cs.webdav.impl;

import uk.ac.standrews.cs.util.Diagnostic;
import uk.ac.standrews.cs.util.StringUtil;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Describes an HTTP request made by a client
 * <p/>
 * The request is processed first by parsing the request line using the
 * processRequestLine method.  At this point the verb, uri, and version are
 * available, as well as the underlying input stream and output streams
 * should the rest of the request need to be proxied
 * <p/>
 * Headers should then be parsed via the processHeaders method which will
 * make available all request headers
 *
 *
 * @author Ben Catherall, graham
 * @version 2005-03-23
 */
public class Request {

    private static final int EXPECTED_MAX_REQUEST_LINE_LENGTH = 256;

	private static final int MAX_REQUEST_LINE_LENGTH = 8192;

	public static final long CONTENT_LENGTH_CHUNKED = -1;

    private Map headers;			// <String, String> 
    private BufferedInputStream in;
    private OutputStream out;
    private String verb;
    private URI uri;
    private String query;
    private int version;
    private Socket socket;
    private Response response;
    private boolean hasContent;
    private long contentLength = CONTENT_LENGTH_CHUNKED;
    private Map parameterMap = new HashMap(); //<String,String>

    /**
     * Create a Request from the InputStream provided, with response
     * being sent to the specific OutputStream.
     */
    public Request(BufferedInputStream in, OutputStream out) {
        //this.in = new BufferedInputStream(in, 512);
        this.in = in;
        this.out = out;
    }

    /**
     * Sets a reference to the underlying socket if the request has one.
     */
    void setSocket(Socket s) {
        this.socket = s;
    }

    /**
     * Get the response.
     */
    public Response getResponse() {
    	
        if (response == null) response = new Response(out, version);
        return response;
    }

    /**
     * Processes request line, and positions InputStream at next byte after CR/LF.
     */
    public void processRequestLine() throws IOException, HTTPException {
        // read bytes directly from the inputstream
        byte[] bRequestLine = stopAtBytes(HTTP.CRLF, MAX_REQUEST_LINE_LENGTH, EXPECTED_MAX_REQUEST_LINE_LENGTH);
        if (bRequestLine != null) {
            String requestLine = new String(bRequestLine);
            //Diagnostic.trace("Request line: "+requestLine, Diagnostic.FULL);
            // now split
            StringTokenizer tokenizer = new StringTokenizer(requestLine, " ");
            String uri_string;
            
            if (tokenizer.hasMoreTokens()) {
                verb = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    uri_string = tokenizer.nextToken();
                    int index = uri_string.indexOf("?");
                    if (index > -1){
                        query = uri_string.substring(index + 1);
                        // parse query string in to parameters
                        String[] queries = query.split("&");
                        for( int ii = 0; ii < queries.length; ii++ ) {
                            String query = queries[ii];
                            int i = query.indexOf("=");
                            String name = null;
                            String value = null;
                            if (i > -1){
                                name = query.substring(0,i);
                                value = query.substring(i+1);
                            } else {
                                name = query;
                            }
                            String current = (String) parameterMap.get(name);
                            if (current != null){
                                current += "," + value;
                            } else {
                                current = value;
                            }
                            parameterMap.put(name,current);
                        }
                        uri_string = uri_string.substring(0,index);
                    }
                    if (tokenizer.hasMoreTokens()) {
                        String sVersion = tokenizer.nextToken();
                        
                        if (sVersion.equals(HTTP.REQUEST_HTTP_11))      version = HTTP.HTTP_11;
                        else if (sVersion.equals(HTTP.REQUEST_HTTP_10)) version = HTTP.HTTP_10;
                        else if (sVersion.equals(HTTP.REQUEST_HTTP_09)) version = HTTP.HTTP_09;
                        else {
                            // unknown version
                        	
                        	// TODO This occurs with MS Word doing Save As to Network Place
                            throw new HTTPException("Bad request line (unknown HTTP version "+sVersion+")", HTTP.RESPONSE_VERSION_NOT_SUPPORTED);
                        }
                        if (version > HTTP.HTTP_UNKNOWN && tokenizer.hasMoreTokens()) {
                            throw new HTTPException("Bad request line (too many components in request line)");
                        }
                    } else {
                        // no version
                        version = HTTP.HTTP_09;
                    }
                } else {
                    // no uri specified error
                    throw new HTTPException("Bad request line (no uri specified)");
                }
            } else {
                // no request specified error
                throw new HTTPException("Bad request line (nothing specified)");
            }
            
            try {
	            uri = new URI(uri_string);
            } catch (URISyntaxException e) {
	            throw new HTTPException("Malformed URI: " + uri_string);
            }
            if (version == HTTP.HTTP_09) {
                throw new HTTPException("HTTP/0.9 clients are not supported", HTTP.RESPONSE_VERSION_NOT_SUPPORTED);
            }
        } else {
            throw new HTTPException("No request line terminator found within safe limits");
        }
        //Diagnostic.trace( "Request for "+verb+" "+uri+" ? "+query+" "+paramterMap, Diagnostic.FULL );
    }

    /**
     * Processes request line and positions InputStream at start of request content (which may be EOF).
     */
    public void processHeaders() throws IOException, HTTPException {
        if (headers != null) {
            throw new IllegalStateException("Already processed headers");
        }
        headers = new HashMap();	// <String, String>
        String lastName = null;
        boolean finished = false;
        do {
            byte[] line = stopAtBytes(HTTP.CRLF,8192, 256);
            //Diagnostic.trace( "Readin bytes: " + new String(line), Diagnostic.FULL);
            if (line != null) {
                String sLine = new String(line);
                if (sLine.length() == 0) {
                    finished = true;
                } else {
                    if (sLine.charAt(0) == ' ' || sLine.charAt(0) == '\t') {
                        // continuation from previous header
                        if (lastName == null) {
                            throw new HTTPException("First header line is an invalid continuation", HTTP.RESPONSE_BAD_REQUEST);
                        } else {
                            String value = (String) headers.get(lastName);
                            headers.put(lastName, value + " " + sLine.trim());
                            //Diagnostic.trace("Header "+lastName+": "+value, Diagnostic.FULL);
                        }
                    } else {

                        int c = sLine.indexOf(':');
                        if (c > -1) {
                            String name = sLine.substring(0, c).trim().toLowerCase();
                            String value = sLine.substring(c + 1).trim();
                            headers.put(name, value);
                            lastName = name;
                            //Diagnostic.trace("Header" + name+": "+value, Diagnostic.FULL);
                        } else {
                            // invalid header
                            throw new HTTPException("Request contains invalid header(s) ["+sLine+"]", HTTP.RESPONSE_BAD_REQUEST);
                        }
                    }
                }
            } else {
                // header greater than 512 bytes
                finished = true;
                throw new HTTPException("Request contains header(s) over the maximum length", HTTP.RESPONSE_BAD_REQUEST);
            }
        } while (!finished);

        // work out if we need to add any filters
        // is there any content at all?
        // if there is a content length that is not 0
        // or there is chunked transfer encoding
        String header;
        if ((header = getHeader(HTTP.HEADER_CONTENT_LENGTH)) != null){
            try {
                contentLength = Long.parseLong(header);
                if (contentLength > 0){
                    hasContent = true;
                }
            } catch (NumberFormatException e){
                throw new HTTPException("Invalid content length sent");
            }
        } else if ((header = getHeader(HTTP.HEADER_TRANSFER_ENCODING)) != null){
            if (StringUtil.contains(header.toLowerCase(),HTTP.HEADER_TOKEN_CHUNKED)){
                hasContent = true;
            }
        }

        String userAgentHeader = null;
        if ((userAgentHeader = getHeader(HTTP.HEADER_USER_AGENT)) != null){
            for (int ii = 0; ii < HTTP.NON_CHUNKED_AGENTS.length; ii++ ){
                String agent = HTTP.NON_CHUNKED_AGENTS[ii];
                //Diagnostic.trace("Checking user agent '"+userAgentHeader+"' against '"+agent+"'", Diagnostic.FULL);
                if (StringUtil.contains(userAgentHeader,agent)){
                    //Diagnostic.trace("Agent "+userAgentHeader+" is non chunked ("+agent+")", Diagnostic.FULL);
                    getResponse().setAllowChunked(false);
                    break;
                }
            }
        }
    }

    /**
     * Returns true if the request has a body.
     */
    public boolean hasContent() {
        return hasContent;
    }

    private byte[] stopAtBytes(byte[] lookFor, int maxSearch, int expected) throws IOException {
    	
        //Diagnostic.trace("Looking for block of length "+lookFor.length+" maxsearch "+maxSearch+" expected "+expected, Diagnostic.RUN );
        byte[] buffer = new byte[expected];
        in.mark(maxSearch);
        int found = -1;
        int read = 0;
        int search = 0;
        
        while (found == -1) {
            int more = in.read(buffer, read, buffer.length - read);
            
            if (more == -1) break;
            
            for (int i = search; i < read + more - lookFor.length + 1 && found == -1; i++) {
                if (buffer[i] == lookFor[0]) {
                    if (lookFor.length == 1) found = i; // found the whole string  
                    else {
                        for (int j = 1; j < lookFor.length; j++) {
                            if (buffer[j + i] == lookFor[j]) {
                                if (j == lookFor.length - 1) {
                                    found = i;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            read += more;
            
            if (found == -1 && read >= buffer.length) {
            	
                // make the buffer bigger
                if (read >= maxSearch) break;

                int newSize = Math.min(buffer.length * 2, maxSearch);
                byte[] bigger = new byte[newSize];
                System.arraycopy(buffer, 0, bigger, 0, buffer.length);
                buffer = bigger;
            }
        }
        
        byte[] out = null;
        
        if (found > -1) {
        	
            out = new byte[found];
            System.arraycopy(buffer, 0, out, 0, found);
            in.reset();
            in.skip(found + lookFor.length);
        }
        else Diagnostic.trace("Returning null", Diagnostic.RUN);
        
        return out;
    }

    /**
     * Get request verb (e.g. GET).
     */
    public String getVerb() {
        return verb;
    }

    /**
     * Gets the request uri.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Gets the HTTP version.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Gets the querystring portion of the request (data after the ? in the uri).
     */
    public String getQueryString() {
        return query;
    }

    /**
     * Returns a map of query string parameters sent with the request.
     */
    public Map getParameters() {	// <String, String>
        return parameterMap;
    }

    /**
     * Returns true if a query string parameter exists with the name supplied.
     */
    public boolean hasParameter(String key){
        return parameterMap.containsKey(key);
    }

    /**
     * Returns the value of the query string parameter with the name supplied.
     * 
     * @return the value of the parameter, or null if the parameter does not exist
     */
    public String getParameter(String key){
        return (String) parameterMap.get(key);
    }

    /**
     * Returns an {@link InputStream} of the request data (after transformations) to be read.
     */
    public InputStream getInputStream(){
        return in;
    }

    /**
     * Gets the associated socket if any.
     *
     * @return associated socket or null if no socket is associated
     */
    Socket getSocket() {
        return socket;
    }

    /**
     * Gets the specified header (key case).
     */
    public String getHeader(String name) {
        return (String) headers.get(name.toLowerCase());
    }

    /**
     * Checks if the request has a header by the name specified.
     *
     * @return header value, or null if no header found
     */
    public boolean hasHeader(String name) {
        return headers.containsKey(name.toLowerCase());
    }

    public long getContentLength() {
        return contentLength;
    }
}
