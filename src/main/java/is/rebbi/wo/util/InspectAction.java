package is.rebbi.wo.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.components.admin.USLoginPage;
import is.rebbi.wo.urls.handlers.URLHandler;
import is.rebbi.wo.urls.handlers.URLHandlerDataObject;
import is.rebbi.wo.urls.handlers.URLHandlerList;
import is.rebbi.wo.urls.handlers.URLHandlerLogin;
import is.rebbi.wo.urls.handlers.URLHandlerSearch;

/**
 * Main entry point into the system.
 */

public class InspectAction extends ERXDirectAction {

	private static final Logger logger = LoggerFactory.getLogger( InspectAction.class );

	private static Map<String, Class<? extends URLHandler>> _urlHandlers;

	public InspectAction( WORequest r ) {
		super( r );
	}

	private static Map<String, Class<? extends URLHandler>> urlHandlers() {
		if( _urlHandlers == null ) {
			_urlHandlers = new HashMap<>();
			_urlHandlers.put( "/i/", URLHandlerDataObject.class );
			_urlHandlers.put( "/l/", URLHandlerList.class );
			_urlHandlers.put( "/search/", URLHandlerSearch.class );
			_urlHandlers.put( "/login", URLHandlerLogin.class );
		}

		return _urlHandlers;
	}

	/**
	 * @return A page for inspecting the specified object.
	 */
	public WOActionResults handlerAction() {
		String url = url();

		logger.info( "Handling URL: {}", url );

		for( String pattern : urlHandlers().keySet() ) {
			if( url.startsWith( pattern ) ) {
				try {
					Constructor<? extends URLHandler> constructor = urlHandlers().get( pattern ).getConstructor( String.class, WOContext.class );
					URLHandler urlHandler = constructor.newInstance( url, context() );
					return urlHandler.generateResponse();
				}
				catch( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
					e.printStackTrace();
				}
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