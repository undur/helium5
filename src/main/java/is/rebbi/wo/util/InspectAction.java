package is.rebbi.wo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.components.admin.USLoginPage;
import is.rebbi.wo.routes.RouteTable;
import is.rebbi.wo.urls.handlers.URLHandlerDataObject;
import is.rebbi.wo.urls.handlers.URLHandlerList;
import is.rebbi.wo.urls.handlers.URLHandlerLogin;
import is.rebbi.wo.urls.handlers.URLHandlerSearch;

/**
 * Main entry point into the system.
 */

public class InspectAction extends ERXDirectAction {

	private static final Logger logger = LoggerFactory.getLogger( InspectAction.class );

	private RouteTable _routeTable;

	private RouteTable routeTable() {
		if( _routeTable == null ) {
			_routeTable.addURLHandler( "/i/", URLHandlerDataObject.class );
			_routeTable.addURLHandler( "/l/", URLHandlerList.class );
			_routeTable.addURLHandler( "/search/", URLHandlerSearch.class );
			_routeTable.addURLHandler( "/login", URLHandlerLogin.class );
		}

		return _routeTable;
	}

	public InspectAction( WORequest r ) {
		super( r );
	}

	/**
	 * @return A page for inspecting the specified object.
	 */
	public WOActionResults handlerAction() {
		return routeTable().handlerInstanceForURL( url(), context() ).generateResponse();

// 		FIXME: Here the 404 should indeed be returned.
//		return response404( url() );
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

	public static WOActionResults response404( String url ) {
		return USHTTPUtilities.statusResponse( 404, "Nothing found at: " + url );
	}

	public WOActionResults loginAction() {
		return pageWithName( USLoginPage.class );
	}
}