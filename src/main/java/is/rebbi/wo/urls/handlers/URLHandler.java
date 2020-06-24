package is.rebbi.wo.urls.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.urls.WrappedURL;

public abstract class URLHandler {

	private WOContext _context;
	private String _url;
	private WrappedURL _path;

	public URLHandler( final String url, final WOContext context ) {
		_context = context;
		_path = WrappedURL.create( url );
		_url = url;
	}

	public String url() {
		return _url;
	}

	public WrappedURL path() {
		return _path;
	}

	public WOContext context() {
		return _context;
	}

	public static URLHandler handlerInstance( final Class<? extends URLHandler> handlerClass, final String url, final WOContext context ) {

		try {
			Constructor<? extends URLHandler> constructor = handlerClass.getConstructor( String.class, WOContext.class );
			return constructor.newInstance( url, context );
		}
		catch( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new RuntimeException( "Failed to instantiate URL handler" );
		}
	}

	public abstract WOActionResults generateResponse();
}