package is.rebbi.wo.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.webobjects.appserver.WORequest;

public class TestUSHTTPUtilities {

	@Test
	public void cookieHost() {
		String actual;
		String expected;

		expected = "127.0.0.1";
		actual = USHTTPUtilities.cookieHost( fakeReqestWithHostHeader( "127.0.0.1:1200" ) );
		assertEquals( expected, actual );

		expected = "127.0.0.1";
		actual = USHTTPUtilities.cookieHost( fakeReqestWithHostHeader( "127.0.0.1" ) );
		assertEquals( expected, actual );

		expected = null;
		actual = USHTTPUtilities.cookieHost( fakeReqestWithHostHeader( "localhost:1200" ) );
		assertEquals( expected, actual );

		expected = null;
		actual = USHTTPUtilities.cookieHost( fakeReqestWithHostHeader( "localhost" ) );
		assertEquals( expected, actual );

		expected = "www.apple.com";
		actual = USHTTPUtilities.cookieHost( fakeReqestWithHostHeader( "www.apple.com:1200" ) );
		assertEquals( expected, actual );

		expected = "www.apple.com";
		actual = USHTTPUtilities.cookieHost( fakeReqestWithHostHeader( "www.apple.com" ) );
		assertEquals( expected, actual );

		expected = "apple.com";
		actual = USHTTPUtilities.cookieHost( fakeReqestWithHostHeader( "apple.com:1200" ) );
		assertEquals( expected, actual );

		expected = "apple.com";
		actual = USHTTPUtilities.cookieHost( fakeReqestWithHostHeader( "apple.com" ) );
	}

	@Test( expected=IllegalArgumentException.class)
	public void cookieHostThrowsIllegalArgumentExceptionOnNull() {
		USHTTPUtilities.cookieHost( null );
	}

	@Test
	public void cookieHostNullOrEmptyHostReturnsNull() {
		assertEquals( null, USHTTPUtilities.cookieHost( fakeRequest() ) );
	}

	@Test
	public void testipAddressFromRequest() {
		String testAddress = "0.0.0.0";

		WORequest r = fakeRequest();
		r.setHeader( testAddress, "remote_addr" );

		assertEquals( testAddress, USHTTPUtilities.ipAddressFromRequest( r ) );
	}

	private static WORequest fakeRequest() {
		return new WORequest( "GET", "", "HTTP/1.1", null, null, null );
	}

	private static WORequest fakeReqestWithHostHeader( String host ) {
		WORequest r = fakeRequest();
		r.setHeader( host, "host" );
		return r;
	}

	@Test
	public void domainStringFromHostString() {
		String actual = null;

		actual = USHTTPUtilities.domainStringFromHostString( "www.us.is" );
		assertEquals( "us.is", actual );

		actual = USHTTPUtilities.domainStringFromHostString( "www.us.is:1200" );
		assertEquals( "us.is", actual );

		actual = USHTTPUtilities.domainStringFromHostString( "us.is" );
		assertEquals( "us.is", actual );

		actual = USHTTPUtilities.domainStringFromHostString( "us.is:1200" );
		assertEquals( "us.is", actual );

		actual = USHTTPUtilities.domainStringFromHostString( "192.168.1.1" );
		assertEquals( "192.168.1.1", actual );
	}
}