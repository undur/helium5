package is.rebbi.wo.routes;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.util.USHTTPUtilities;

/**
 * Main entry point into the system.
 */

public class RouteAction extends ERXDirectAction {

	public RouteAction( WORequest r ) {
		super( r );
	}

	/**
	 * @return The result of invoking the route with the URL provided.
	 */
	public WOActionResults handlerAction() {
		return RouteTable.defaultRouteTable().handle( WrappedURL.create( url() ), context() );
	}

	/**
	 * @return The requested URL, either from a URL parameter or Apache's 404 handler
	 */
	protected String url() {
		String url = request().stringFormValueForKey( "url" );

		if( url == null ) {
			url = USHTTPUtilities.redirectURL( request() );
		}

		return url;
	}
}