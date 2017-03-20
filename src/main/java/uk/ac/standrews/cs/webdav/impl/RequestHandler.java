/*
 * Created on Aug 2, 2005 at 4:07:11 PM.
 */
package uk.ac.standrews.cs.webdav.impl;

import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.locking.interfaces.ILockManager;
import uk.ac.standrews.cs.utils.Diagnostic;
import uk.ac.standrews.cs.utils.Error;
import uk.ac.standrews.cs.webdav.exceptions.HTTPException;
import uk.ac.standrews.cs.webdav.interfaces.HTTPMethod;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {

	private static final int INPUT_BUFFER_SIZE = 2048;
	private Socket socket;
	private IFileSystem file_system;
	private ILockManager lock_manager;

	public RequestHandler(Socket socket, IFileSystem file_system, ILockManager lock_manager) {
		this.socket = socket;
		this.file_system = file_system;
		this.lock_manager = lock_manager;
	}

	public void run() {
		// Diagnostic.trace( "Thread started " + Thread.currentThread().hashCode(), Diagnostic.INIT );
        Request request = null;
		try {
            request = getRequest();
			request.setSocket(socket);

            try {
                runRequest(request);
            } catch (IOException e) {
                tryToCloseSocket(e);
            } catch (HTTPException e) {
                httpExceptionResponse(request, e);

                Diagnostic.trace("************* Completed Request: " + request.getVerb() + " " + request.getUri() + " [thread " + Thread.currentThread().hashCode() + "]", Diagnostic.RUN);
            } catch (RuntimeException e) {
                // Absorb any unchecked exceptions.
                Error.exceptionError("While processing request", e);
                internalServerErrorResponse(request, e);
            }

        } catch (IOException e) {
            Error.exceptionError("While setting up client socket streams", e);
        } finally {
            //Diagnostic.trace( "Thread completed " + Thread.currentThread().hashCode(), Diagnostic.RUN );
            closeConnectedSocket();
        }

	}

    private Request getRequest() throws IOException {
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        BufferedInputStream bufferedIn = new BufferedInputStream(in, INPUT_BUFFER_SIZE);

        return new Request(bufferedIn, out);
    }

    private void runRequest(Request request) throws IOException, HTTPException {
        // Partly functioning support for keep-alive of the socket connection removed on 23/8/05 from:
        //
        // uk.ac.stand.dcs.asa.storage.webdav.impl.RequestHandler
        // uk.ac.stand.dcs.asa.storage.webdav.impl.Response

        request.processRequestLine();
        Diagnostic.trace("********** Request: " + request.getVerb() + " " + request.getUri() + " [thread " + Thread.currentThread().hashCode() + "]", Diagnostic.RUN);

        // Decide what action to take.
        HTTPMethod method = HTTP.HTTP_METHOD(request.getVerb());

        method.init(file_system, lock_manager);
        request.processHeaders();
        try(Response response = request.getResponse()) {
            method.execute(request, response);
        }

        Diagnostic.trace("************* Completed Request: " + request.getVerb() + " " + request.getUri() + " [thread " + Thread.currentThread().hashCode() + "]", Diagnostic.RUN);
    }

	private void closeConnectedSocket() {
		try {
			if (socket != null && socket.isConnected())
				closeSocket();
		} catch (IOException e) {
			Error.exceptionError("While trying to close socket", e);
		}
	}

    private void tryToCloseSocket(IOException e) {
        // Comms error.
        // No point in setting the error code in the response if we can't send it...
        try {
            Error.exceptionError("While processing request from " + socket.getInetAddress().getHostName(), e);
            closeSocket();
        } catch (IOException ioe) {
            Error.exceptionError("IOException while closing socket", ioe);
        }
    }

	private void closeSocket() throws IOException {
		socket.close();
		socket = null;
	}

	// HTTP related error thrown by HTTP method, so translate to appropriate response code.
	private void httpExceptionResponse(Request request, HTTPException e)  {
		Response response = request.getResponse();
        try {
            response.writeError(e.getStatusCode(), e.getMessage());
        } catch (IOException e1) {
            Error.exceptionError("While processing http exception response", e);
        }
    }

	private void internalServerErrorResponse(Request request, RuntimeException e) {
		Response response = request.getResponse();
        try {
            response.writeError(HTTP.RESPONSE_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IOException e1) {
            Error.exceptionError("While processing internal server error response", e);
        }
    }
}
