package is.rebbi.wo.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.urls.WrappedURL;
import is.rebbi.wo.urls.handlers.URLHandler;

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

	/**
	 * A list of all routes mapped by this table
	 */
	private List<Route> _routes = new ArrayList<>();

	/**
	 * The default global route table used by RouteAction to access actions
	 */
	private static RouteTable _defaultRouteTable = new RouteTable();

	public static RouteTable defaultRouteTable() {
		return _defaultRouteTable;
	}

	private List<Route> routes() {
		return _routes;
	}

	private RouteHandler handlerForURL( final WrappedURL url ) {

		for( Route route : routes() ) {
			if( matches( route.pattern, url.sourceURL() ) ) {
				return route.routeHandler;
			}
		}

		throw new RuntimeException( "No handler found for URL: " + url );
	}

	private static boolean matches( final String pattern, final String url ) {
		return url.startsWith( pattern );
	}

	/**
	 * Handle the given URL
	 *
	 * FIXME: We should be returning a 404 response if no handler is found for the URL.
	 */
	public WOActionResults handle( final WrappedURL url, final WOContext context ) {
		logger.info( "Handling URL: {}", url );
		return handlerForURL( url ).handle( url, context );
	}

	public void map( final String pattern, final RouteHandler routeHandler ) {
		Route r = new Route();
		r.pattern = pattern;
		r.routeHandler = routeHandler;
		_routes.add( r );
	}

	public void map( final String pattern, final Class<? extends URLHandler> handlerClass ) {
		final URLHandlerRouteHandler routeHandler = new URLHandlerRouteHandler( handlerClass );
		map( pattern, routeHandler );
	}

	public void map( final String pattern, final BiFunction<WrappedURL,WOContext,WOActionResults> biFunction ) {
		final BiFunctionHandler routeHandler = new BiFunctionHandler( biFunction );
		map( pattern, routeHandler );
	}

	public void mapComponent( final String pattern, final Class<? extends ERXComponent> componentClass ) {
		final ComponentHandler routeHandler = new ComponentHandler( componentClass );
		map( pattern, routeHandler );
	}

	public static class Route {
		public String pattern;
		public RouteHandler routeHandler;
	}

	public static abstract class RouteHandler {
		public abstract WOActionResults handle( WrappedURL url, WOContext context );
	}

	public static class URLHandlerRouteHandler extends RouteHandler {

		public Class<? extends URLHandler> _urlHandlerClass;

		public URLHandlerRouteHandler( Class<? extends URLHandler> urlHandlerClass ) {
			_urlHandlerClass = urlHandlerClass;
		}

		@Override
		public WOActionResults handle( final WrappedURL url, final WOContext context ) {
			return URLHandler.handlerInstance( _urlHandlerClass, url.sourceURL(), context ).generateResponse();
		}
	}

	public static class BiFunctionHandler extends RouteHandler {
		private BiFunction<WrappedURL,WOContext,WOActionResults> _biFunction;

		public BiFunctionHandler( final BiFunction<WrappedURL,WOContext,WOActionResults> biFunction ) {
			_biFunction = biFunction;
		}

		@Override
		public WOActionResults handle( WrappedURL url, WOContext context ) {
			return _biFunction.apply( url, context );
		}
	}

	public static class ComponentHandler extends RouteHandler {
		private Class<? extends ERXComponent> _componentClass;

		public ComponentHandler( final Class<? extends ERXComponent> componentClass ) {
			_componentClass = componentClass;
		}

		@Override
		public WOActionResults handle( WrappedURL url, WOContext context ) {
			return ERXApplication.erxApplication().pageWithName( _componentClass, context );
		}
	}

	// FIXME: Remove
	private static ERXApplication app() {
		return ERXApplication.erxApplication();
	}
}