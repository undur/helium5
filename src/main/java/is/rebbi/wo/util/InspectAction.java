package is.rebbi.wo.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.components.admin.USLoginPage;
import is.rebbi.wo.urls.handlers.URLHandler;
import is.rebbi.wo.urls.handlers.URLHandlerDataObject;
import is.rebbi.wo.urls.handlers.URLHandlerList;
import is.rebbi.wo.urls.handlers.URLHandlerLogin;
import is.rebbi.wo.urls.handlers.URLHandlerPasswordReset;
import is.rebbi.wo.urls.handlers.URLHandlerSearch;

/**
 * Main entry point into the system.
 */

public class InspectAction extends ERXDirectAction {

	private static final Logger logger = LoggerFactory.getLogger( InspectAction.class );

	private static List<URLHandler> _urlHandlers;

	public InspectAction( WORequest r ) {
		super( r );
	}

	private static List<URLHandler> urlHandlers() {
		if( _urlHandlers == null ) {
			_urlHandlers = new ArrayList<>();
			_urlHandlers.add( new URLHandlerDataObject() );
			_urlHandlers.add( new URLHandlerList() );
			_urlHandlers.add( new URLHandlerSearch() );
			_urlHandlers.add( new URLHandlerPasswordReset() );
			_urlHandlers.add( new URLHandlerLogin() );
		}

		return _urlHandlers;
	}

	/**
	 * @return A page for inspecting the specified object.
	 */
	public WOActionResults handlerAction() {
		String url = url();

		logger.info( "Handling URL: {}", url );

		for( URLHandler urlHandler : urlHandlers() ) {
			if( url.startsWith( urlHandler.prefix() ) ) {
				return urlHandler.execute().apply( url, context() );
			}
		}

		return response404( url() );
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