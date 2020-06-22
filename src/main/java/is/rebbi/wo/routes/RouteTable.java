package is.rebbi.wo.routes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.urls.handlers.URLHandler;

public class RouteTable {

	private static final Logger logger = LoggerFactory.getLogger( RouteTable.class );

	private Map<String,RouteHandler> _routes = new HashMap<>();

	private Map<String, Class<? extends URLHandler>> _urlHandlers = new HashMap<>();

	public void map( final String routeString, final RouteHandler routeHandler ) {
		_routes.put( routeString, routeHandler );
	}

	public void addURLHandler( final String urlPrefix, final Class<? extends URLHandler> handlerClass ) {
		_urlHandlers.put( urlPrefix, handlerClass );
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