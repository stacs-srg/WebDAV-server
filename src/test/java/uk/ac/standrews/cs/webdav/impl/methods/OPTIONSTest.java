package uk.ac.standrews.cs.webdav.impl.methods;

import org.junit.Test;
import uk.ac.standrews.cs.webdav.impl.HTTP;

public class OPTIONSTest extends AbstractMethodTest {

	@Test
	public void test1() {

		showResponse(null, 9093, "ASA", "OPTIONS /", makeTestString(9093));
		//showResponse(null, 80, "Apache", "OPTIONS /", makeTestString(80));
	}

	@Test
	public void test2() {

		showResponse(null, 9090, "ASA", "OPTIONS /asa/", makeTestString(9090));
		showResponse(null, 80, "Apache", "OPTIONS /asa/", makeTestString(80));
	}
	
	public String makeTestString(int port) {
		
		String crlf = new String(HTTP.CRLF);
		
		return "OPTIONS / HTTP/1.1" + crlf +
	           "User-Agent: ASA Test Harness" + crlf +
	           "Accept: */*" + crlf +
	           "Content-Length: 0" + crlf +
	           "Connection: close" + crlf +
	           "Host: localhost:" + port + crlf + crlf;
	}

}
