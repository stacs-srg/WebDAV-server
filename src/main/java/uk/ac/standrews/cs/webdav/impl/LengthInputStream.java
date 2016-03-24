/*
 * Created on Jun 30, 2005 at 3:33:49 PM.
 */
package uk.ac.standrews.cs.webdav.impl;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author ben
 *
 *  InputStream that reads up to the length of the request, as given in the Content-Length HTTP header
 */
public class LengthInputStream extends InputStream {
    protected long left;
    private InputStream is;

    public LengthInputStream(InputStream inputStream, long contentLength) {
        this.is = inputStream;
        left = contentLength;
    }

    public int read() throws IOException {
        if (left <= 0) return -1;
        int r = is.read();
        if (r == -1) {
            // reached EOF before we expected to
            left = 0;
        } else {
            left--;
        }
        return r;
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (left <= 0) return -1;
        if (len > left) len = (int) left;
        int r = is.read(b, off, len);
        if (r == -1) {
            // reached EOF before we expected to
            left = 0;
        } else {
            left -= r;
        }
        return r;
    }

    public void close() throws IOException {
        if (left > 0) {
            // skip what is left
            long s = is.skip(left);
            if (s < left) {
                // didnt skip as much as we expected, but the stream has closed
                // so it shouldnt matter
            }
            left = 0;
        }
    }
}
