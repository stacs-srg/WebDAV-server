package uk.ac.standrews.cs.webdav.impl.methods.test;


public class PROPFINDTest extends AbstractMethodTest {

	/**
	 * Construct new test instance
	 *
	 * @param name the test name
	 */
	public PROPFINDTest(String name) {
		super(name);
	}
	
	public void test1() {
		
		// No specific properties specified, so should return all.

		//showResponse(null, 80, "Apache", "PROPFIND /asa/ - no properties specified", makeRequestHeader(80, "/asa/", "0", ""));
		showResponse(null, 9093, "ASA", "PROPFIND / - no properties specified", makeRequestHeader(9093, "/", "0", ""));
	}
	
	public void test2() {
		
		String creation_date_request_body = makeCreationDateRequestBody();

		showResponse(null, 80, "Apache", "PROPFIND /asa/", makeRequestHeader(80, "/asa/", "0", creation_date_request_body));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/", makeRequestHeader(9090, "/asa/", "0", creation_date_request_body));
	}
	
	public void test3() {
		
		String request_body = makeOSXRequestBody();

		showResponse(null, 80, "Apache", "PROPFIND /asa/", makeRequestHeader(80, "/asa/", "0", request_body));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/", makeRequestHeader(9090, "/asa/", "0", request_body));
	}
	
	public void test4() {
		
		String request_body = makeRequestBodyAllProperties();

		showResponse(null, 80, "Apache", "PROPFIND /asa/", makeRequestHeader(80, "/asa/", "0", request_body));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/", makeRequestHeader(9090, "/asa/", "0", request_body));
	}
	
	// Test response for missing resource.
	public void test5() {
		
		String request_body = makeOSXRequestBody();

		showResponse(null, 80, "Apache", "PROPFIND /asa/fish.doc", makeRequestHeader(80, "/asa/fish.doc", "0", request_body));
		showResponse(null, 9090, "ASA", "PROPFIND /fish.doc", makeRequestHeader(9090, "/fish.doc", "0", request_body));
	}
	
	public void test6() {
		
		// No specific properties specified, so should return all.

		showResponse(null, 80, "Apache", "PROPFIND /asa/file1.doc - no properties specified", makeRequestHeader(80, "/asa/file1.doc", "0", ""));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/file1.doc - no properties specified", makeRequestHeader(9090, "/asa/file1.doc", "0", ""));
	}
	
	public void test7() {
		
		// All properties requested.

		String request_body = makeRequestBodyAllProperties();

		showResponse(null, 80, "Apache", "PROPFIND /asa/file1.doc - no properties specified", makeRequestHeader(80, "/asa/file1.doc", "0", request_body));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/file1.doc - no properties specified", makeRequestHeader(9090, "/asa/file1.doc", "0", request_body));
	}
	
	public void test8() {
		
		// No specific properties requested, depth infinity.

		showResponse(null, 80, "Apache", "PROPFIND /asa/ - no properties specified, depth infinity", makeRequestHeader(80, "/asa/", "infinity", ""));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/ - no properties specified, depth infinity", makeRequestHeader(9090, "/asa/", "infinity", ""));
	}
	
	public void test9() {
		
		// No specific properties requested, depth 1.

		showResponse(null, 80, "Apache", "PROPFIND /asa/ - no properties specified, depth 1", makeRequestHeader(80, "/asa/", "1", ""));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/ - no properties specified, depth 1", makeRequestHeader(9090, "/asa/", "1", ""));
	}
	
	public void test10() {
		
		// No specific properties requested, no depth specified.

		showResponse(null, 80, "Apache", "PROPFIND /asa/ - no properties specified, no depth", makeRequestHeader(80, "/asa/", "", ""));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/ - no properties specified, no depth", makeRequestHeader(9090, "/asa/", "", ""));
	}
	
	public void test11() {
		
		// All properties explicitly requested, depth 1.

		String request_body = makeRequestBodyAllProps();

		showResponse(null, 80, "Apache", "PROPFIND /asa/ - all properties requested, depth 1", makeRequestHeader(80, "/asa/", "1", request_body));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/ - all properties requested, depth 1", makeRequestHeader(9090, "/asa/", "1", request_body));
	}
	
	public void test12() {
		
		// All property names explicitly requested, depth 1.

		String request_body = makeRequestBodyAllNames();

		showResponse(null, 80, "Apache", "PROPFIND /asa/ - all property names requested, depth 1", makeRequestHeader(80, "/asa/", "1", request_body));
		showResponse(null, 9090, "ASA", "PROPFIND /asa/ - all property names requested, depth 1", makeRequestHeader(9090, "/asa/", "1", request_body));
	}
	
	public String makeRequestHeader(int port, String uri, String depth, String body) {
		
		String depth_header = "";
		if (!depth.equals("")) depth_header = "Depth: " + depth + CRLF;
		
		return "PROPFIND " + uri + " HTTP/1.1" + CRLF +
	           "User-Agent: ASA Test Harness" + CRLF +
	           "Accept: */*" + CRLF +
	           depth_header +
	           "Content-Length: " + body.length() + CRLF +
	           "Connection: close" + CRLF +
	           "Host: localhost:" + port + CRLF + CRLF +
	           body;
	}
	
	public String makeCreationDateRequestBody() {
		
		return "<?xml version=" + quote("1.0") + " encoding=" + quote("utf-8") + "?>" + LF +
		       "<D:propfind xmlns:D=" + quote("DAV:") + ">" +  LF +
	           "<D:prop>" + LF +
		       "<D:creationdate/>" + LF +
		       "</D:prop>" + LF +
		       "</D:propfind>" + LF;
	}
	
	public String makeOSXRequestBody() {
		
		// Requested by OSX WebDAV client on startup.
		
		return "<?xml version=" + quote("1.0") + " encoding=" + quote("utf-8") + "?>" + LF +
		       "<D:propfind xmlns:D=" + quote("DAV:") + ">" +  LF +
	           "<D:prop>" + LF +
		       "<D:getlastmodified/>" + LF +
		       "<D:getcontentlength/>" + LF +
		       "<D:resourcetype/>" + LF +
		       "</D:prop>" + LF +
		       "</D:propfind>" + LF;
	}
	
	public String makeRequestBodyAllProps() {
		
		// All properties requested explicitly.
		
		return "<?xml version=" + quote("1.0") + " encoding=" + quote("utf-8") + "?>" + LF +
		       "<D:propfind xmlns:D=" + quote("DAV:") + ">" +  LF +
	           "<D:allprop/>" + LF +
		       "</D:propfind>" + LF;
	}
	
	public String makeRequestBodyAllNames() {
		
		// All property names requested explicitly.
		
		return "<?xml version=" + quote("1.0") + " encoding=" + quote("utf-8") + "?>" + LF +
		       "<D:propfind xmlns:D=" + quote("DAV:") + ">" +  LF +
	           "<D:propname/>" + LF +
		       "</D:propfind>" + LF;
	}
	
	public String makeRequestBodyAllProperties() {
		
		// Combination of all properties defined in WebDAV spec or observed to be requested by OSX and Windows WebDAV clients.
		
		return "<?xml version=" + quote("1.0") + " encoding=" + quote("utf-8") + "?>" + LF +
		       "<D:propfind xmlns:D=" + quote("DAV:") + ">" +  LF +
	           "<D:prop>" + LF +
	           
	           // WebDAV
		       "<D:creationdate/>" + LF +
		       "<D:displayname/>" + LF +
		       "<D:getcontentlanguage/>" + LF +
		       "<D:getcontentlength/>" + LF +
		       "<D:getcontenttype/>" + LF +
		       "<D:getetag/>" + LF +
		       "<D:getlastmodified/>" + LF +
		       "<D:lockdiscovery/>" + LF +
		       "<D:resourcetype/>" + LF +
		       "<D:source/>" + LF +
 
	           // Apple
		       "<D:quota/>" + LF +
		       "<D:quotaused/>" + LF +
		       "<D:appledoubleheader/>" + LF +
		   	
		   	   // Windows:
		       "<D:name/>" + LF +
		       "<D:parentname/>" + LF +
		       "<D:href/>" + LF +
		       "<D:ishidden/>" + LF +
		       "<D:isreadonly/>" + LF +
		       "<D:contentclass/>" + LF +
		       "<D:lastaccessed/>" + LF +
		       "<D:iscollection/>" + LF +
		       "<D:isstructureddocument/>" + LF +
		       "<D:defaultdocument/>" + LF +
		       "<D:isroot/>" + LF +
		       
		       "</D:prop>" + LF +
		       "</D:propfind>" + LF;
	}
	
	public String quote(String s) {
		
		return "\"" + s + "\"";
	}

	public static void main(String[] args) {
		
		new PROPFINDTest("test").test1();
	}
}
