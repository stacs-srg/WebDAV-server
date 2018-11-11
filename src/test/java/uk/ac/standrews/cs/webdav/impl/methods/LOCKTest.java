package uk.ac.standrews.cs.webdav.impl.methods;

import org.junit.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;

import static org.junit.Assert.assertTrue;

public class LOCKTest extends AbstractMethodTest {

	@Test
    public void testNonExtantURI() {
        String uri = "/" + (GUIDFactory.generateRandomGUID()).toString();

        System.out.println("Should be able to lock a non-extant URI with extant parent - result should be 201 CREATED.\n");

        String request = makeTestString(TEST_PORT, uri);
        String response = processRequest(request, null, TEST_PORT);
        showResponse(response, TEST_PORT, TEST_LABEL, "LOCK " + uri);

        assertTrue(response.startsWith("HTTP/1.1 201 Created\r\n")); // TODO - mock response
    }

	@Test
	public void test1() {
		
		String uri = "/" + (GUIDFactory.generateRandomGUID()).toString();

		System.out.println("Should be able to lock a non-extant URI with extant parent - result should be 200 OK.\n");
		showResponse(null, 9093, "ASA", "LOCK " + uri, makeTestString(9093, uri));
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Should successfully return lockdiscovery and supportedlock properties (lock-null - locked but not extant).\n");
		showResponse(null, 9093, "ASA", "PROPFIND " + uri, makePropfindRequestHeader(9093, "0", uri, makeRequestBodyAllProps()));
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String uri2 = "/" + (GUIDFactory.generateRandomGUID()).toString() + "/" + (GUIDFactory.generateRandomGUID()).toString();

		System.out.println("Shouldn't be able to lock a non-extant URI with non-extant parent - result should be 412 precondition failed.\n");
		showResponse(null, 9093, "ASA", "LOCK " + uri2, makeTestString(9093, uri2));
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Should return not found (not locked, not extant).\n");
		showResponse(null, 9093, "ASA", "PROPFIND /asdlhjaskcn", makePropfindRequestHeader(9093, "0", "/asdlhjaskcn", makeRequestBodyAllProps()));
		
		//showResponse(null, 80, "Apache OSX", "LOCK /" + file_name, makeTestString(80, "/test/" + file_name));
		//showResponse("systems.dcs.st-and.ac.uk", 80, "Apache Linux", "LOCK /" + file_name, makeTestString(80, "/graham/" + file_name));
	}

    @Test
	public void test2() {
		
		String random_name = GUIDFactory.generateRandomGUID().toString();
		String uri = "/" + random_name;

		System.out.println("Should be able to lock a non-existent URI - result should be 200 OK.\n");
		showResponse(null, 9093, "ASA", "LOCK " + uri, makeTestString(9093, uri));
		
		System.out.println("Should contain " + random_name + ".\n");
		showResponse(null, 9093, "ASA", "PROPFIND /", makePropfindRequestHeader(9093, "infinity", "/", makeRequestBodyAllProps()));
	}
	
	public String makeTestString(int port, String uri) {
		
		String lock_spec = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> " + LF +
						   "<D:lockinfo xmlns:D='DAV:'>" + LF +
						   "<D:lockscope><D:exclusive/></D:lockscope>" + LF +
						   "<D:locktype><D:write/></D:locktype>" + LF +
						   "<D:owner>" + LF +
						   "<D:href>ASA Test</D:href>" + LF +
						   "</D:owner>" + LF +
						   "</D:lockinfo>" + LF;
		
		return "LOCK " + uri + " HTTP/1.1" + CRLF +
			   "User-Agent: ASA Test Harness" + CRLF +
			   "Content-Type: text/xml; charset=\"utf-8\"" + CRLF +
			   "Content-Length: " + lock_spec.length() + CRLF +
			   "Connection: close" + CRLF +
			   "Host: localhost:" + port + CRLF + CRLF +
			   lock_spec;
	}
	
	public String makePropfindRequestHeader(int port, String depth, String uri, String body) {
		
		String depth_header = "Depth: " + depth + CRLF;
		
		return "PROPFIND " + uri + " HTTP/1.1" + CRLF +
			   "User-Agent: ASA Test Harness" + CRLF +
			   "Accept: */*" + CRLF +
			   depth_header +
			   "Content-Length: " + body.length() + CRLF +
			   "Connection: close" + CRLF +
			   "Host: localhost:" + port + CRLF + CRLF +
			   body;
	}
	
	public String makeRequestBodyAllProps() {
		
		// All properties requested explicitly.
		
		return "<?xml version=" + quote("1.0") + " encoding=" + quote("utf-8") + "?>" + LF +
			   "<D:propfind xmlns:D=" + quote("DAV:") + ">" +  LF +
			   "<D:allprop/>" + LF +
			   "</D:propfind>" + LF;
	}
	
	public String quote(String s) {
		
		return "\"" + s + "\"";
	}
}
