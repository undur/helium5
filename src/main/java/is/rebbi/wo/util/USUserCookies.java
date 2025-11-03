package is.rebbi.wo.util;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOCookie;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSTimestamp;

public class USUserCookies {

	private static final String COOKIE_NAME = "helium-" + WOApplication.application().name().toLowerCase() + "-userid";

	public static String cookieValue( WORequest request ) {
		return request.cookieValueForKey( COOKIE_NAME );
	}

	public static void addCookie( WORequest request, WOResponse response, String value ) {
		NSTimestamp expires = new NSTimestamp().timestampByAddingGregorianUnits( 0, 1, 0, 0, 0, 0 );
		WOCookie cookie = new WOCookie( COOKIE_NAME, value, "/", USHTTPUtilities.cookieHost( request ), expires, false );
		response.addCookie( cookie );
	}

	public static void deleteCookie( WORequest request, WOResponse response ) {
		NSTimestamp expires = new NSTimestamp( 1900, 1, 1, 0, 0, 0, null );
		WOCookie cookie = new WOCookie( COOKIE_NAME, "bye-bye", "/", USHTTPUtilities.cookieHost( request ), expires, false );
		response.addCookie( cookie );
	}
}