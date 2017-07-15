package is.rebbi.wo.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSDictionary;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.components.admin.USLoginPage;
import is.rebbi.wo.search.USSearchAction;
import is.rebbi.wo.urls.USStaticURLs;
import is.rebbi.wo.urls.handlers.URLHandler;
import is.rebbi.wo.urls.handlers.URLHandlerDataObject;
import is.rebbi.wo.urls.handlers.URLHandlerList;

/**
 * Main entry point into the system.
 */

public class InspectAction extends ERXDirectAction {

	private static final Logger logger = LoggerFactory.getLogger( InspectAction.class );

	private static final String SEARCH_PREFIX = "/search/";
	public static final String PASSWORD_RESET_REQUEST_PREFIX = "/passwordResetRequest/";

	private static List<URLHandler> _urlHandlers;

	public InspectAction( WORequest r ) {
		super( r );
	}

	public static List<URLHandler> urlHandlers() {
		if( _urlHandlers == null ) {
			_urlHandlers = new ArrayList<>();
			_urlHandlers.add( new URLHandlerDataObject() );
			_urlHandlers.add( new URLHandlerList() );
		}

		return _urlHandlers;
	}

	/**
	 * @return A page for inspecting the specified object.
	 */
	public WOActionResults handlerAction() {
		String url = url();

		logger.info( "Handling URL: {}", url );

		String redirectURL = USStaticURLs.url( url, context() );

		if( redirectURL != null ) {
			return USHTTPUtilities.redirectTemporary( redirectURL );
		}

		for( URLHandler urlHandler : urlHandlers() ) {
			if( url.startsWith( urlHandler.prefix() ) ) {
				System.out.println( "Found URL handler!" );
				return urlHandler.execute().apply( url, context() );
			}
		}

		if( url.startsWith( SEARCH_PREFIX ) ) {
			String afterPrefix = url.substring( SEARCH_PREFIX.length() );
			logger.info( "searchString: " + afterPrefix );
			String directActionName = USSearchAction.class.getSimpleName() + "/search";
			NSDictionary<String, Object> params = new NSDictionary<>( afterPrefix, "searchString_field" );
			String searchURL = context().directActionURLForActionNamed( directActionName, params );
			return USHTTPUtilities.redirectTemporary( searchURL );
		}

		if( url.startsWith( PASSWORD_RESET_REQUEST_PREFIX ) ) {
			String afterPrefix = url.substring( PASSWORD_RESET_REQUEST_PREFIX.length() );
			NSDictionary<String, Object> params = new NSDictionary<>( afterPrefix, "key" );
			String searchURL = context().directActionURLForActionNamed( "SWPasswordResetAction" /* FIXME: SWPasswordResetAction.class.getSimpleName() */, params );
			return USHTTPUtilities.redirectTemporary( searchURL );
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