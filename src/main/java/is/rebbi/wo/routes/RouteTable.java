package is.rebbi.wo.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.util.USHTTPUtilities;

/**
 * Contains a list of handlers for URLs
 *
 * Routing allows a couple of types of wildcards
 *
 * - Named parameters are prefixed with a colon
 * - Positional parameters are a star
 */

public class RouteTable {

	private static final NotFoundRouteHandler NOT_FOUND_ROUTE_HANDLER = new NotFoundRouteHandler();

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

	private RouteHandler handlerForURL( final String url ) {

		for( final Route route : routes() ) {
			if( matches( route.pattern, url ) ) {
				return route.routeHandler;
			}
		}

		return null;
	}

	/**
	 * Check if the given handler matches the given URL
	 */
	private static boolean matches( final String pattern, final String url ) {
		return url.startsWith( pattern );
	}

	public WOActionResults handle( final WORequest request ) {
		final String routeURL = routeURLFromRequestParameters( request );
		return handle( routeURL, request.context() );
	}

	/**
	 * Handle the given URL
	 */
	@Deprecated
	private WOActionResults handle( final String url, final WOContext context ) {
		final WORequest request = context.request();
		logger.info( "Handling URL: {};{};{}", url, USHTTPUtilities.ipAddressFromRequest( request ), USHTTPUtilities.userAgent( request ) );
		RouteHandler routeHandler = handlerForURL( url );

		if( routeHandler == null ) {
			logger.warn( "No RouteHandler found for URL: {}", url.toString() );
			routeHandler = NOT_FOUND_ROUTE_HANDLER;
		}

		return routeHandler.handle( WrappedURL.create( url ), context );
	}

	/**
	 * @return The requested URL
	 *
	 * Either
	 *  - from the "URL"query parameter (usually used for development)
	 *  - or from the redirect_url header provided by Apache's 404 handler
	 */
	private static String routeURLFromRequestParameters( final WORequest request ) {
		String url = request.stringFormValueForKey( "url" );

		if( url == null ) {
			url = USHTTPUtilities.redirectURL( request );
		}

		return url;
	}

	public void map( final String pattern, final RouteHandler routeHandler ) {
		Route r = new Route();
		r.pattern = pattern;
		r.routeHandler = routeHandler;
		_routes.add( r );
	}

	public void map( final String pattern, final BiFunction<WrappedURL, WOContext, WOActionResults> biFunction ) {
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

	/**
	 * For returning 404
	 */
	public static class NotFoundRouteHandler extends RouteHandler {
		@Override
		public WOActionResults handle( final WrappedURL url, WOContext context ) {
			final WOResponse response = new WOResponse();
			response.setStatus( 404 );
			response.setContent( "No route found for URL: " + url );
			return response;
		}
	}

	public static class BiFunctionRouteHandler extends RouteHandler {
		private BiFunction<WrappedURL, WOContext, WOActionResults> _biFunction;

		public BiFunctionRouteHandler( final BiFunction<WrappedURL, WOContext, WOActionResults> biFunction ) {
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