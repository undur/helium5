package is.rebbi.wo.urls;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXApplication;
import is.rebbi.core.util.StringUtilities;
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

	private static Map<Class, URLProvider> urlProviders() {
		if( _urlProviders == null ) {
			_urlProviders = new HashMap<>();
			_urlProviders.put( DataObject.class, new URLProviderCayenne() );
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

	/**
	 * @return The URL for viewing the default list of the specified entity.
	 */
	public static String urlForListInContext( String entityName, WOContext context ) {
		String url = "/l/" + EntityViewDefinition.get( entityName ).urlPrefix();

		if( ERXApplication.erxApplication().isDevelopmentMode() ) {
			url = USURLProvider.makeURLDeveloperFriendly( url, context );
		}

		return url;
	}

	public static String absoluteURL( String url, WOContext context ) {
		String host = USHTTPUtilities.host( context.request() );

		if( host == null ) {
			host = SWSettings.defaultDomainName();
		}

		if( !SWSettings.generateFriendlyURLs( context.request() ) ) {
			url = USURLProvider.makeURLDeveloperFriendly( url, context );
		}

		return "http://" + host + url;
	}

	/**
	 * @return A direct connect version of the URL.
	 */
	public static String makeURLDeveloperFriendly( String url, WOContext context ) {
		NSMutableDictionary<String, Object> d = new NSMutableDictionary<>();
		d.setObjectForKey( url, "url" );
		// FIXME: url = context.directActionURLForActionNamed( InspectAction.class.getSimpleName() + "/handler", d );
		url = context.directActionURLForActionNamed( "InspectAction/handler", d );
		url = StringUtilities.replace( url, "&", "&amp;" );
		return url;
	}
}