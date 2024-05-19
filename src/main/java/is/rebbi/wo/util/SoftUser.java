package is.rebbi.wo.util;

import java.util.UUID;

import com.webobjects.appserver.WOCookie;
import com.webobjects.appserver.WOCookie.SameSite;
import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXApplication;

/**
 * A user which might not still have a database representation.
 */

public class SoftUser {

	/**
	 * The key used in WORequest's and WOResponse's userInfo to identify the user.
	 */
	private static final String USER_KEY = "softUser";
	private static final String USER_ID_COOKIE_NAME = USER_KEY;
	private static final String USER_ID_COOKIE_PATH = "/";
	private static final int USER_ID_COOKIE_LIFETIME_IN_MONTHS = 3;

	/**
	 * Unique identifier for this user
	 */
	private String _uuid;

	public SoftUser() {
		this( UUID.randomUUID().toString() );
	}

	public SoftUser( String uuid ) {
		setUuid( uuid );
	}

	/**
	 * @return Unique identifier for this user.
	 */
	public String uuid() {
		return _uuid;
	}

	private void setUuid( String value ) {
		_uuid = value;
	}

	/**
	 * @return True if the given message contains a user cookie.
	 */
	public static boolean hasUserCookie( WOMessage message ) {
		for( WOCookie cookie : message.cookies() ) {
			if( USER_ID_COOKIE_NAME.equals( cookie.name() ) ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * We attempt to fetch a user based on information retrieved from a cookie.
	 * If no cookie exists, we create a new user object. Hopefully for eventual insertion :-).
	 */
	public static SoftUser fromRequest( WORequest request ) {

		if( request != null ) {
			SoftUser softUser = (SoftUser)request.userInfoForKey( USER_KEY );

			if( softUser == null ) {
				String uuid = userIDFromRequest( request );
				softUser = get( uuid );
				request.setUserInfoForKey( softUser, USER_KEY );
			}

			return softUser;
		}

		return null;
	}

	public static SoftUser get( String uuid ) {
		SoftUser softUser = null;

		if( uuid != null ) {
			softUser = new SoftUser( uuid );
		}
		else {
			softUser = new SoftUser();
		}

		return softUser;
	}

	/**
	 * Writes the value cookie to the response.
	 */
	public void assign( WORequest request, WOResponse response ) {
		if( response != null ) {
			String domain = USHTTPUtilities.domain( request );
			WOCookie c = createCookieWithUserID( uuid(), domain, USER_ID_COOKIE_LIFETIME_IN_MONTHS );
			response.addCookie( c );
		}
	}

	/**
	 * @return A cookie with the user ID.
	 */
	private static WOCookie createCookieWithUserID( String userID, String domain, Integer lifetime ) {
		NSTimestamp expires = new NSTimestamp().timestampByAddingGregorianUnits( 0, lifetime, 0, 0, 0, 0 );
		WOCookie cookie = new WOCookie( USER_ID_COOKIE_NAME, userID.toString(), USER_ID_COOKIE_PATH, domain, expires, false );
		cookie.setSameSite( SameSite.LAX );
		return cookie;
	}

	public static void deleteUserCookie( WORequest request, WOResponse response ) {
		String domain = USHTTPUtilities.domain( request );
		WOCookie cookie = createCookieWithUserID( "", domain, -1 );
		response.addCookie( cookie );
	}

	/**
	 * Attempts to read a user id from a request.
	 */
	private static String userIDFromRequest( WORequest request ) {
		return request.cookieValueForKey( USER_ID_COOKIE_NAME );
	}

	@Override
	public String toString() {
		return "SoftUser [_userID=" + _uuid + "]";
	}

	/**
	 * Manages the setting and getting of user information during the request/response loop.
	 */
	public static class Manager {

		public static Manager instance = new Manager();
		private static ThreadLocal<WORequest> _currentRequest = new ThreadLocal<>();

		public void willDispatchRequest( NSNotification notification ) {
			WORequest r = (WORequest)notification.object();
			_currentRequest.set( r );
		}

		public void didDispatchRequest( NSNotification notification ) {
			WOResponse response = (WOResponse)notification.object();
			WORequest request = _currentRequest.get();

			SoftUser softUser = SoftUser.fromRequest( request );

			if( !SoftUser.hasUserCookie( response ) && softUser != null ) {
				softUser.assign( request, response );
			}

			USHTTPUtilities.resetCookieHeaderInResponse( response );
		}

		public static void register() {
			NSSelector<Void> willSel = new NSSelector<>( "willDispatchRequest", new Class[] { NSNotification.class } );
			NSNotificationCenter.defaultCenter().addObserver( instance, willSel, ERXApplication.ApplicationWillDispatchRequestNotification, null );

			NSSelector<Void> didSel = new NSSelector<>( "didDispatchRequest", new Class[] { NSNotification.class } );
			NSNotificationCenter.defaultCenter().addObserver( instance, didSel, ERXApplication.ApplicationDidDispatchRequestNotification, null );
		}
	}
}