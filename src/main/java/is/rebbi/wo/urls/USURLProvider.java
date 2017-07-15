package is.rebbi.wo.urls;

import java.util.Map;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXApplication;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.SWSettings;
import is.rebbi.wo.util.USHTTPUtilities;

/**
 * Generates URLs.
 */

public abstract class USURLProvider {

	private static Map<Class, URLProvider> _urlProviders;

	/**
	 * @return The URL for viewing the given object.
	 */
	public static String urlForObjectInContext( Object object, WOContext context ) {
		URLProvider urlProvider = urlProviderForClass( object.getClass() );

		if( urlProvider == null ) {
			throw new NullPointerException( "No URLProvider registered for objects of class: " + object.getClass() );
		}

		return urlProvider.urlForObject( object, context );
	}

	/**
	 * @return The URL for viewing the given object.
	 */
	public static String urlForObjectInContext( String entityName, Object id, WOContext context ) {
		Class clazz = EntityViewDefinition.get( entityName ).entityClass();
		URLProvider urlProvider = urlProviderForClass( clazz );

		if( urlProvider == null ) {
			throw new NullPointerException( "No URLProvider registered for objects of class: " + clazz );
		}

		return urlProvider.urlForObject( entityName, id, context );
	}

	/**
	 * @return The URL for viewing the default list of the specified entity.
	 */
	public static String urlForListInContext( String entityName, WOContext context ) {
		String url = "/l/" + EntityViewDefinition.get( entityName ).urlPrefix();

		if( ERXApplication.erxApplication().isDevelopmentMode() ) {
			url = URLUtilities.makeURLDeveloperFriendly( url, context );
		}

		return url;
	}

	private static Map<Class, URLProvider> urlProviders() {
		if( _urlProviders == null ) {
			_urlProviders = new NSMutableDictionary<>();
		}

		return _urlProviders;
	}

	public static URLProvider urlProviderForClass( Class<?> clazz ) {

		if( DataObject.class.isAssignableFrom( clazz ) ) {
			return new URLProviderCayenne();
		}

		return urlProviders().get( clazz );
	}

	public static String absoluteURL( String url, WOContext context ) {
		String host = USHTTPUtilities.host( context.request() );

		if( host == null ) {
			host = SWSettings.defaultDomainName();
		}

		if( !SWSettings.generateFriendlyURLs( context.request() ) ) {
			url = URLUtilities.makeURLDeveloperFriendly( url, context );
		}

		return "http://" + host + url;
	}
}