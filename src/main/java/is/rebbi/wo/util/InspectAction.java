package is.rebbi.wo.util;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import is.rebbi.wo.components.admin.USLoginPage;

/**
 * Main entry point into the system.
 */

@Deprecated
public class InspectAction extends RouteAction {

	public InspectAction( WORequest r ) {
		super( r );
	}

	public static WOActionResults response404( String url ) {
		return USHTTPUtilities.statusResponse( 404, "Nothing found at: " + url );
	}

	public WOActionResults loginAction() {
		return pageWithName( USLoginPage.class );
	}
}