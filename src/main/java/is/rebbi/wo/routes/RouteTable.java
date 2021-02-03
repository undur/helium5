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

		for( final Route route : routes() ) {
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

		// FIXME: Introduced this just to find a bug in NBServer
		// Feb 03 10:11:45 NBServer[2002] (RouteTable.java:68) INFO  is.rebbi.wo.routes.RouteTable  (77c8aa24-df60-49f3-8828-900f81e605e6) - Handling URL: cgi-bin/WebObjects/NBServer.woa/wr
		if( url != null && url.sourceURL().contains( "cgi-bin" ) ) {

			try {
				throw new RuntimeException( "Just wanted a stack trace, sorry about that." );
			}
			catch( Exception exceptionForStackTrace ) {
				System.out.println( "======== START DEBUG ========" );
				exceptionForStackTrace.printStackTrace();
				System.out.println( "======== END DEBUG ========" );
			}
		}

		return handlerForURL( url ).handle( url, context );
	}

	public void map( final String pattern, final RouteHandler routeHandler ) {
		Route r = new Route();
		r.pattern = pattern;
		r.routeHandler = routeHandler;
		_routes.add( r );
	}

	public void map( final String pattern, final BiFunction<WrappedURL,WOContext,WOActionResults> biFunction ) {
		final BiFunctionRouteHandler routeHandler = new BiFunctionRouteHandler( biFunction );
		map( pattern, routeHandler );
	}

	public void mapComponent( final String pattern, final Class<? extends ERXComponent> componentClass ) {
		final ComponentRouteHandler routeHandler = new ComponentRouteHandler( componentClass );
		map( pattern, routeHandler );
	}

	/**
	 * Maps a URL pattern to a given RouteHandler
	 */
	public static class Route {

		/**
		 * The pattern this route uses
		 */
		public String pattern;

		/**
		 * The routeHandler that will handle requests passed to this route
		 */
		public RouteHandler routeHandler;
	}

	public static abstract class RouteHandler {
		public abstract WOActionResults handle( WrappedURL url, WOContext context );
	}

	public static class BiFunctionRouteHandler extends RouteHandler {
		private BiFunction<WrappedURL,WOContext,WOActionResults> _biFunction;

		public BiFunctionRouteHandler( final BiFunction<WrappedURL,WOContext,WOActionResults> biFunction ) {
			_biFunction = biFunction;
		}

		@Override
		public WOActionResults handle( WrappedURL url, WOContext context ) {
			return _biFunction.apply( url, context );
		}
	}

	public static class ComponentRouteHandler extends RouteHandler {
		private Class<? extends ERXComponent> _componentClass;

		public ComponentRouteHandler( final Class<? extends ERXComponent> componentClass ) {
			_componentClass = componentClass;
		}

		@Override
		public WOActionResults handle( WrappedURL url, WOContext context ) {
			return ERXApplication.erxApplication().pageWithName( _componentClass, context );
		}
	}
}