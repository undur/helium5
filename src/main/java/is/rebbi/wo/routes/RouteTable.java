package is.rebbi.wo.routes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.urls.handlers.URLHandler;
import is.rebbi.wo.urls.handlers.URLHandlerDataObject;
import is.rebbi.wo.urls.handlers.URLHandlerList;
import is.rebbi.wo.urls.handlers.URLHandlerLogin;
import is.rebbi.wo.urls.handlers.URLHandlerSearch;

/**
 * Contains a list of handlers for URLs
 *
 * Routing allows a couple of types of wildcards
 *
 * - Named parameters are prefixed with a colon
 * - Positional parameters are a star
 */

public class RouteTable {

	private static final Logger logger = LoggerFactory.getLogger( RouteTable.class );

	private Map<String, Class<? extends URLHandler>> _urlHandlers = new HashMap<>();

	private static RouteTable _defaultRouteTable;

	public static RouteTable defaultRouteTable() {
		if( _defaultRouteTable == null ) {
			_defaultRouteTable = new RouteTable();
			_defaultRouteTable.map( "/i/", URLHandlerDataObject.class );
			_defaultRouteTable.map( "/l/", URLHandlerList.class );
			_defaultRouteTable.map( "/search/", URLHandlerSearch.class );
			_defaultRouteTable.map( "/login", URLHandlerLogin.class );
		}

		return _defaultRouteTable;
	}

	public void map( final String pattern, final Class<? extends URLHandler> handlerClass ) {
		_urlHandlers.put( pattern, handlerClass );
	}

	private Map<String, Class<? extends URLHandler>> urlHandlers() {
		return _urlHandlers;
	}

	public Class<? extends URLHandler> handlerClassForURL( final String url ) {

		for( String pattern : urlHandlers().keySet() ) {
			if( url.startsWith( pattern ) ) {
				return urlHandlers().get( pattern );
			}
		}

		throw new RuntimeException( "Unhandleable URL: " + url );
	}

	public URLHandler handlerInstanceForURL( final String url, final WOContext context ) {
		logger.info( "Handling URL: {}", url );

		try {
			Class<? extends URLHandler> handlerClass = handlerClassForURL( url );
			Constructor<? extends URLHandler> constructor = handlerClass.getConstructor( String.class, WOContext.class );
			return constructor.newInstance( url, context );
		}
		catch( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new RuntimeException( "Failed to instantiate URL handler" );
		}
	}
}