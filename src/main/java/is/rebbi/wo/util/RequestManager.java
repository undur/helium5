package is.rebbi.wo.util;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

/**
 * Updates statistics and fixes cookies.
 */

public class RequestManager {

	private static RequestManager _singleton;

	/**
	 * Number of successfully completed requests performed since application launch.
	 */
	private int _numberOfServedRequestsSinceStartup = 0;

	/**
	 * It's a singleton.
	 */
	private RequestManager() {}

	/**
	 * Creates our default transaction manager.
	 */
	public static RequestManager singleton() {
		if( _singleton == null ) {
			_singleton = new RequestManager();
		}

		return _singleton;
	}

	/**
	 * Registers the transaction manager so it starts listening and watching transactions.
	 */
	public static void register() {
		NSSelector<RequestManager> selector1 = new NSSelector<>( "willDispatchRequest", new Class[] { NSNotification.class } );
		NSNotificationCenter.defaultCenter().addObserver( singleton(), selector1, WOApplication.ApplicationWillDispatchRequestNotification, null );

		NSSelector<RequestManager> selector2 = new NSSelector<>( "didDispatchRequest", new Class[] { NSNotification.class } );
		NSNotificationCenter.defaultCenter().addObserver( singleton(), selector2, WOApplication.ApplicationDidDispatchRequestNotification, null );
	}

	public void willDispatchRequest( NSNotification notification ) {
		WORequest request = (WORequest)notification.object();
		USHTTPUtilities.removeNullCookies( request );
	}

	public void didDispatchRequest( NSNotification notification ) {
		WOResponse response = (WOResponse)notification.object();
		USHTTPUtilities.resetCookieHeaderInResponse( response );
		incrementNumberOfRequestsSinceStartup();
	}

	public int numberOfRequestsSinceStartup() {
		return _numberOfServedRequestsSinceStartup;
	}

	public void incrementNumberOfRequestsSinceStartup() {
		_numberOfServedRequestsSinceStartup++;
	}
}