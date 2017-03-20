package uk.ac.standrews.cs.webdav.impl;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * Describes an HTTP response to send to a client
 * <p/>
 * Keep-alive is supported, and can be set via the setKeepAlive method for clients
 * which support it
 * <p/>
 * Chunked transfer encoding can be used to send data where the content length is not
 * known.
 * Chunked encoding can also be turned off by <i>setAllowChunked</i>.
 * <p/>
 * When setting the content length header, you must use the method <i>setContentLength</i>
 * rather than the generic <i>setHeader</i> method.
 * <p/>
 * To send a status response the <i>writeError</i> method may be useful.
 * <p/>
 * Redirects can be sent with the <i>sendRedirect</i> method.
 *
 * @author Ben Catherall, heavily rewritten by al, stuart & graham!
 */
public class Response implements AutoCloseable {
	
    private OutputStream out;
    private Map headers = new HashMap(); // <String, String>
    private ByteArrayOutputStream output_buffer;  // used to buffer output
    private int status_code;
    private Writer writer;
    private boolean allow_chunked = true;
    private boolean chunked = false;
    private int httpVersion = HTTP.HTTP_UNKNOWN;
    private boolean headers_written = false;   // have we written the HTTP headers etc.
    private boolean length_set = false;

    public Response(OutputStream out, int httpVersion) {
        this.out = out;
        this.httpVersion = httpVersion;
    }

    public boolean isAllow_chunked() {
        return allow_chunked;
    }

    /**
     * Sets whether chunked encoding may be used rather than buffering the response.
     */
    public void setAllowChunked(boolean allowChunked) {
        this.allow_chunked = allowChunked;
    }

    /**
     * Sets the HTTP status code of the response
     * <p/>
     * Constants are in the <i>HTTP</i> class.
     */
    public void setStatusCode(int statusCode) {
        this.status_code = statusCode;
    }

    public int getStatus_code() {
        return status_code;
    }

    /**
     * Sets the specified header to the specified value.
     */
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setHeader(String name, long value) {
        setHeader(name, Long.toString(value));
    }

    /**
     * Gets the buffer to which data may be written.
     * This method creates a buffer if needed.
     * For chunked output it creates a new buffer on each call after writing back the last data load.
     * For unchunked output it returns the extant buffer if there is one.
     * If chunked output is required, set_chunked() must be called, and chunking must be allowed.
     */
    public OutputStream getOutputBuffer() throws IOException {
    	
        if (output_buffer == null) output_buffer = new ByteArrayOutputStream();
        else
            if (is_chunked_output()) write_chunk();  // write out the buffer if there was one - this resets the buffer

        return output_buffer; 
    }
    
    /**
     * Returns a reference to a Writer on the standard OutputStream.
     */
    public Writer getOutputWriter() throws IOException {
    	
        if (writer == null) writer = new OutputStreamWriter(getOutputBuffer());
        return writer;
    }

    public Map getHeaders() { //<String, String>
        return headers;
    }

    /**
     * Returns the HTTP version of the request / response.
     *
     * @see HTTP#HTTP_09
     * @see HTTP#HTTP_10
     * @see HTTP#HTTP_11
     */
    public int getHttpVersion() {
        return httpVersion;
    }

    /**
     * Sets the content type header to the type specified.
     */
    public void setContentType(String type) {
        setHeader(HTTP.HEADER_CONTENT_TYPE, type);
    }
    
    public void setContentLength(long size) {    
        length_set = true;
        setHeader(HTTP.HEADER_CONTENT_LENGTH, size);
    }

    /**
     * Inserts standard 'Date', 'Server' headers,
     * and 'Connection' header based on the keep alive status and HTTP version in use
     */
    public void insertStandardHeaders() {
    	
        setHeader(HTTP.HEADER_CONNECTION, HTTP.HEADER_TOKEN_CLOSE);
        setHeader(HTTP.HEADER_DATE, HTTP.HTTP_DATE(new Date(System.currentTimeMillis())));
        setHeader(HTTP.HEADER_SERVER, HTTP.HEADER_SERVER_NAME);
        
        if (is_chunked_output()) setHeader(HTTP.HEADER_TRANSFER_ENCODING, HTTP.HEADER_TOKEN_CHUNKED);
    }

    /**
     * Sends an (error) message to the client.
     * <p/>
     * Text is taken from the HTTP i18n bundle to create response messages.
     */
    public void writeError(int status_code, String message) throws IOException {
    	
        setStatusCode(status_code);
        
        String generalTitle = HTTP.HTTP_BUNDLE_STRING("T" + status_code);
        if (generalTitle == null) generalTitle = "Error " + status_code;

        String generalMessage = HTTP.HTTP_BUNDLE_STRING("M" + status_code);
        if (generalMessage == null) generalMessage = "";

        setAllowChunked(false);
        setContentType(HTTP.CONTENT_TYPE_HTML);
        
        if (message == null) message = "";

        String text = MessageFormat.format(HTTP.HTTP_BUNDLE_STRING("error.template"), new Object[] { generalTitle, generalMessage, message, new Integer( status_code ) } );

        //Writer writer = getOutputWriter();
        Writer writer = new OutputStreamWriter(getOutputBuffer());
        writer.write(text);
        
        close();    // close the response and send output to client
        writer.close();
    }

    /**
     * Sends an HTTP redirect to the specified URI.
     *
     * @param uri       URI to redirect to
     * @param permanent if true a 301 response is sent, otherwise a 302
     */
    public void sendRedirect(String uri, boolean permanent) throws IOException {
    	
        setStatusCode(permanent ? HTTP.RESPONSE_MOVED_PERMENANTLY : HTTP.RESPONSE_MOVED_TEMPORARILY);
        setHeader(HTTP.HEADER_LOCATION, uri);
        
        String generalTitle = HTTP.HTTP_BUNDLE_STRING("T" + status_code);
        if (generalTitle == null)  generalTitle = "Moved "+(permanent ? "permenantly" : "temporarily");

        String generalMessage = HTTP.HTTP_BUNDLE_STRING("M" + status_code);
        if (generalMessage == null) generalMessage = "The object you requested has moved";

        String message = MessageFormat.format(HTTP.HTTP_BUNDLE_STRING("E300"),new Object[] { uri } );
        String text = MessageFormat.format(HTTP.HTTP_BUNDLE_STRING("error.template"), new Object[] { generalTitle, generalMessage, message, new Integer( status_code ) } );

        Writer writer = getOutputWriter();
        writer.write(text);
        
        close();    // close the response and send output to client
        writer.close();
    }

    /**
     * Flush and close the OutputStream, closing the underlying OutputStream if the
     * connection is not keep-alive.
     */
    @Override
    public void close() throws IOException {
    	
        if (writer != null) writer.flush();      // Flush unwritten data to buffer.
        
        writeHeaders();

        if (is_chunked_output()) 
        {
            write_chunk();
            write_chunked_trailer();
        } else { // unchunked 
            write_unchunked();
        }
        
        out.write(HTTP.CRLF);     
        out.close();    
    }
    
    /****************** Private methods ******************/

    private void write_chunk() throws IOException {

        if (output_buffer != null && output_buffer.size() > 0) {
        	
            writeHeaders();             // for safety - these may be written already
            
            // Write Chunk Header
            byte[] size = Integer.toHexString(output_buffer.size()).getBytes();
            out.write(size);
            out.write(HTTP.CRLF);
            output_buffer.writeTo(out);
            output_buffer.reset();       // empty out the buffer
            out.write(HTTP.CRLF);
        }
    }

    private void write_chunked_trailer() throws IOException {
    	
        // Write Chunk Trailer
        out.write((byte) '0');
        out.write(HTTP.CRLF);
    }
        
    private void write_unchunked() throws IOException  {
    	
        if (output_buffer != null && output_buffer.size() != 0)    // have payload - write out
            output_buffer.writeTo(out);
    }
    
    public void setChunked(boolean chunked) {
        this.chunked = chunked;
    }

    /**
     * Writes Response line and HTTP headers to the client (or not if the client only supports HTTP/0.9).
     */
    private void writeHeaders() throws IOException {
    	
        if (!headers_written) {
        	
            headers_written = true;
            
            int http_version = getHttpVersion();
            
            if (http_version > HTTP.HTTP_09) {
            	
                if (http_version == HTTP.HTTP_10) {
                	out.write(HTTP.RESPONSE_HTTP_10);
                }
                if (http_version == HTTP.HTTP_11) {
                	out.write(HTTP.RESPONSE_HTTP_11);
                }

                out.write(HTTP.SPACE);
                byte[] responseCode = Integer.toString(getStatus_code()).getBytes();
                out.write(responseCode);
                out.write(HTTP.SPACE);
                byte[] message = (byte[]) HTTP.RESPONSE_TITLES.get(new Integer(getStatus_code()));
                
                if (message == null) message = responseCode;
                
                out.write(message);
                out.write(HTTP.CRLF);
                
                insertStandardHeaders();
                Set s = getHeaders().keySet();
                Iterator iter = s.iterator();
                
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = (String) getHeaders().get(key);
                    out.write(key.getBytes());
                    out.write(HTTP.HEADER_COLON_SPACE);
                    out.write(value.getBytes());
                    out.write(HTTP.CRLF);
                }
                
                if (!is_chunked_output() && ! length_set ) {
                    long content_length = output_buffer == null ? 0 : output_buffer.size();
                    out.write(HTTP.HEADER_CONTENT_LENGTH.getBytes());
                    out.write(HTTP.HEADER_COLON_SPACE);
                    out.write(Long.toString(content_length).getBytes());
                    out.write(HTTP.CRLF);
                }
                
                out.write(HTTP.CRLF);
            }
        }
    }

    private boolean is_chunked_output() { return allow_chunked && chunked; }
}
