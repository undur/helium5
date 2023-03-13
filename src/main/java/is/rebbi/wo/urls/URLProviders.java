package is.rebbi.wo.urls;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;

public class URLProviders {

	private static Map<Class, URLProvider> _urlProviders;

	public static String urlForObject( final Object object ) {
		final URLProvider urlProvider = urlProviderForClass( object.getClass() );

		if( urlProvider == null ) {
			throw new NullPointerException( "No URLProvider registered for objects of class: " + object.getClass() );
		}

		return urlProvider.urlForObject( object );
	}

	private static Map<Class, URLProvider> urlProviders() {
		if( _urlProviders == null ) {
			_urlProviders = new HashMap<>();
			_urlProviders.put( DataObject.class, new URLProviderDataObject() );
			_urlProviders.put( ObjectId.class, new URLProviderObjectId() );
		}

		return _urlProviders;
	}

	private static URLProvider urlProviderForClass( Class<?> clazz ) {

		for( Entry<Class, URLProvider> provider : urlProviders().entrySet() ) {
			if( provider.getKey().isAssignableFrom( clazz ) ) {
				return provider.getValue();
			}
		}

		throw new NullPointerException( "No URLProvider registered for objects of class: " + clazz );
	}
}