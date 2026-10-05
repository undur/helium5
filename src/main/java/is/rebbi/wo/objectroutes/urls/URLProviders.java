package is.rebbi.wo.objectroutes.urls;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.PersistentObject;

/**
 * The URL of an object's page, by the provider for its class
 */

public class URLProviders {

	/**
	 * The providers, each for a class and its subclasses
	 */
	private static final List<Map.Entry<Class<?>, URLProvider<?>>> PROVIDERS = List.of(
			Map.entry( PersistentObject.class, new URLProviderDataObject() ),
			Map.entry( ObjectId.class, new URLProviderObjectId() ) );

	/**
	 * The provider for each class, found once per class: a page listing thousands of objects looks it up for each
	 */
	private static final ClassValue<URLProvider<?>> PROVIDER_FOR_CLASS = new ClassValue<>() {
		@Override
		protected URLProvider<?> computeValue( final Class<?> type ) {
			for( final Map.Entry<Class<?>, URLProvider<?>> provider : PROVIDERS ) {
				if( provider.getKey().isAssignableFrom( type ) ) {
					return provider.getValue();
				}
			}

			return null;
		}
	};

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static String urlForObject( final Object object ) {
		final URLProvider urlProvider = PROVIDER_FOR_CLASS.get( object.getClass() );

		if( urlProvider == null ) {
			throw new NullPointerException( "No URLProvider registered for objects of class: " + object.getClass() );
		}

		return urlProvider.urlForObject( object );
	}
}
