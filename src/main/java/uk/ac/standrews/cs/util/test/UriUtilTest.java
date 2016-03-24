package uk.ac.standrews.cs.util.test;

import junit.framework.TestCase;
import uk.ac.standrews.cs.util.UriUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class UriUtilTest extends TestCase {
	
	public void testUriEncode() {

		assertEquals(UriUtil.uriEncode("my files"), "my%20files");
		assertEquals(UriUtil.uriEncode("a/b/c"), "a/b/c");
		assertEquals(UriUtil.uriEncode("a b/c d/e f"), "a%20b/c%20d/e%20f");
		assertEquals(UriUtil.uriEncode("/P2P Services.Data/rdb copy/"), "/P2P%20Services.Data/rdb%20copy/");
	}

	public void testChildUri() {
		
		try {
			assertEquals(UriUtil.childUri(new URI(""), "abc", false), new URI("/abc"));
			assertEquals(UriUtil.childUri(new URI(""), "abc", true), new URI("/abc/"));
			assertEquals(UriUtil.childUri(new URI("/"), "abc", false), new URI("/abc"));
			assertEquals(UriUtil.childUri(new URI("/"), "abc", true), new URI("/abc/"));
			assertEquals(UriUtil.childUri(new URI("/x"), "abc", false), new URI("/x/abc"));
			assertEquals(UriUtil.childUri(new URI("/x"), "abc", true), new URI("/x/abc/"));
			assertEquals(UriUtil.childUri(new URI("/x/"), "abc", false), new URI("/x/abc"));
			assertEquals(UriUtil.childUri(new URI("/x/"), "abc", true), new URI("/x/abc/"));
			assertEquals(UriUtil.childUri(new URI("/x/"), "my files", true), new URI("/x/my%20files/"));
			assertEquals(UriUtil.childUri(new URI("/my%20files/"), "my file", true), new URI("/my%20files/my%20file/"));
		}
		catch (URISyntaxException e) {
			fail();
		}
	}
	
	public void testParentUri() {
		
		try {
			assertEquals(UriUtil.parentUri(new URI("")), new URI("/"));
			assertEquals(UriUtil.parentUri(new URI("/")), new URI("/"));
			assertEquals(UriUtil.parentUri(new URI("/abc")), new URI("/"));
			assertEquals(UriUtil.parentUri(new URI("/abc/")), new URI("/"));
			assertEquals(UriUtil.parentUri(new URI("/abc/def")), new URI("/abc"));
			assertEquals(UriUtil.parentUri(new URI("/abc/def%20ghi/j%20kl%23")), new URI("/abc/def%20ghi"));
		}
		catch (URISyntaxException e) {
			fail();
		}
	}
	
	public void testBaseName() {
		
		try {
			assertEquals(UriUtil.baseName(new URI("")), "");
			assertEquals(UriUtil.baseName(new URI("/")), "");
			assertEquals(UriUtil.baseName(new URI("/abc")), "abc");
			assertEquals(UriUtil.baseName(new URI("/abc/")), "abc");
			assertEquals(UriUtil.baseName(new URI("/abc/def")), "def");
			assertEquals(UriUtil.baseName(new URI("/abc/def%20ghi/j%20kl%23")), "j kl#");
		}
		catch (URISyntaxException e) {
			fail();
		}
	}
	
	public void testPathElementIterator() {
		
		try {
			Iterator iterator = UriUtil.pathElementIterator(new URI(""));
			assertFalse(iterator.hasNext());
			
			iterator = UriUtil.pathElementIterator(new URI("/"));
			assertFalse(iterator.hasNext());

			iterator = UriUtil.pathElementIterator(new URI("/abc"));
			assertEquals(iterator.next(), "abc");
			assertFalse(iterator.hasNext());

			iterator = UriUtil.pathElementIterator(new URI("/abc/"));
			assertEquals(iterator.next(), "abc");
			assertFalse(iterator.hasNext());
			
			iterator = UriUtil.pathElementIterator(new URI("/abc/def%20ghi/j%20kl%23"));
			assertEquals(iterator.next(), "abc");
			assertEquals(iterator.next(), "def ghi");
			assertEquals(iterator.next(), "j kl#");
			assertFalse(iterator.hasNext());
		}
		catch (URISyntaxException e) {
			fail();
		}
	}
}
