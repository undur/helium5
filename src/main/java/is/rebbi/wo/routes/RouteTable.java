package is.rebbi.wo.routes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.search.components.USSearchPage;
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

	private Map<String, RouteHandler> _routeHandlers = new HashMap<>();

	private static RouteTable _defaultRouteTable;

	public static RouteTable defaultRouteTable() {
		if( _defaultRouteTable == null ) {
			_defaultRouteTable = new RouteTable();
			_defaultRouteTable.map( "/i/", URLHandlerDataObject.class );
			_defaultRouteTable.map( "/l/", URLHandlerList.class );
			_defaultRouteTable.map( "/search/", URLHandlerSearch.class );
			_defaultRouteTable.map( "/login", URLHandlerLogin.class );
			_defaultRouteTable.map( "/smu", (url,context) -> { return ERXApplication.erxApplication().pageWithName( USSearchPage.class, context ); } );
			_defaultRouteTable.mapComponent( "/bla", USSearchPage.class );
		}

		return _defaultRouteTable;
	}

	public static abstract class RouteHandler {
		public abstract WOActionResults handle( String url, WOContext context );
	}

	public static class URLHandlerRouteHandler extends RouteHandler {

		public Class<? extends URLHandler> _urlHandlerClass;

		public URLHandlerRouteHandler( Class<? extends URLHandler> urlHandlerClass ) {
			_urlHandlerClass = urlHandlerClass;
		}

		@Override
		public WOActionResults handle( final String url, final WOContext context ) {
			return RouteTable.defaultRouteTable().handlerInstance( _urlHandlerClass, url, context ).generateResponse();
		}
	}

	public static class BiFunctionHandler extends RouteHandler {
		private BiFunction<String,WOContext,WOActionResults> _biFunction;

		public BiFunctionHandler( final BiFunction<String,WOContext,WOActionResults> biFunction ) {
			_biFunction = biFunction;
		}

		@Override
		public WOActionResults handle( String url, WOContext context ) {
			return _biFunction.apply( url, context );
		}
	}

	public static class ComponentHandler extends RouteHandler {
		private Class<? extends ERXComponent> _componentClass;

		public ComponentHandler( final Class<? extends ERXComponent> componentClass ) {
			_componentClass = componentClass;
		}

		@Override
		public WOActionResults handle( String url, WOContext context ) {
			return ERXApplication.erxApplication().pageWithName( _componentClass, context );
		}
	}

	/**
	 * Handle the given URL
	 *
	 * FIXME: We should be returning a 404 response if no handler is found for the URL.
	 */
	public WOActionResults handle( final String url, final WOContext context ) {
		logger.info( "Handling URL: {}", url );
		return handlerForURL( url ).handle( url, context );
	}

	public void map( final String pattern, final Class<? extends URLHandler> handlerClass ) {
		final URLHandlerRouteHandler routeHandler = new URLHandlerRouteHandler( handlerClass );
		_routeHandlers.put( pattern, routeHandler );
	}

	public void map( final String pattern, final BiFunction<String,WOContext,WOActionResults> biFunction ) {
		final BiFunctionHandler routeHandler = new BiFunctionHandler( biFunction );
		_routeHandlers.put( pattern, routeHandler );
	}

	public void mapComponent( final String pattern, final Class<? extends ERXComponent> componentClass ) {
		final ComponentHandler routeHandler = new ComponentHandler( componentClass );
		_routeHandlers.put( pattern, routeHandler );
	}

	private Map<String,RouteHandler> routeHandlers() {
		return _routeHandlers;
	}

	public RouteHandler handlerForURL( final String url ) {

		for( String pattern : routeHandlers().keySet() ) {
			if( url.startsWith( pattern ) ) {
				return routeHandlers().get( pattern );
			}
		}

		throw new RuntimeException( "Unhandleable URL: " + url );
	}

	public URLHandler handlerInstance( final Class<? extends URLHandler> handlerClass, final String url, final WOContext context ) {

		try {
			Constructor<? extends URLHandler> constructor = handlerClass.getConstructor( String.class, WOContext.class );
			return constructor.newInstance( url, context );
		}
		catch( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new RuntimeException( "Failed to instantiate URL handler" );
		}
	}
}