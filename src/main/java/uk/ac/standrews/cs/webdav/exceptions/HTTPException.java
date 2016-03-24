package uk.ac.standrews.cs.webdav.exceptions;

import uk.ac.standrews.cs.webdav.impl.HTTP;

/**
 * An exception which the HTTP server will attempt to send to the client
 * <p/>
 * The status code should be set to the desired HTTP status code to send to the client
 * If the exception is fatal (set by default) the HTTP server will close the connection
 * after sending the exception.
 * <p/>
 * If information has already been sent to the client, no attempt should be made
 * to send it to the client as it will probably not make sense
 *
 * @author Ben Catherall
 * @version 2005-03-23
 */
public class HTTPException extends Exception {

    protected int statusCode = HTTP.RESPONSE_BAD_REQUEST;
    protected boolean fatal = true;

    public HTTPException() {
        super();
    }

    public HTTPException(String message) {
        super(message);
    }

    public HTTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public HTTPException(Throwable cause) {
        super(cause);
    }

    public HTTPException(String message, int statusCode) {
        super(message);
        setStatusCode(statusCode);
    }

    public HTTPException(int statusCode, boolean fatal) {
        setStatusCode(statusCode);
        setFatal(fatal);
    }

    public HTTPException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HTTPException(String message, int statusCode, boolean fatal) {
        super(message);
        setStatusCode(statusCode);
        setFatal(fatal);
    }

    public HTTPException(Throwable throwable, int statusCode, boolean fatal){
        super(throwable.toString());
        setStatusCode(statusCode);
        setFatal(fatal);
    }

    public HTTPException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isFatal() {
        return fatal;
    }

    public void setFatal(boolean fatal) {
        this.fatal = fatal;
    }
}
