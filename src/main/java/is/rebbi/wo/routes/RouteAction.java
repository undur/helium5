package is.rebbi.wo.routes;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.util.USHTTPUtilities;

/**
 * A Direct Action class that takes care of passing requests on to a RouteHandler.
 *
 * URLs are be passed to the handler action either by query parameter ("url")
 * or are retrieved from the redirect_url header passed in by Apache's 404 handler.
 */

public class RouteAction extends ERXDirectAction {

	public RouteAction( WORequest r ) {
		super( r );
	}

	/**
	 * @return The result of invoking the route mathing the provided URL.
	 */
	public WOActionResults handlerAction() {
		return RouteTable.defaultRouteTable().handle( url(), context() );
	}

	/**
	 * @return The requested URL
	 *
	 * Either
	 *  - from the "URL"query parameter (usually used for development)
	 *  - or from the redirect_url header provided by Apache's 404 handler
	 */
	protected String url() {
		String url = request().stringFormValueForKey( "url" );

		if( url == null ) {
			url = USHTTPUtilities.redirectURL( request() );
		}

		return url;
	}
}