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
import is.rebbi.wo.util.InspectAction;

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

		String url = urlProvider.urlForObject( object, context );

		if( ERXApplication.erxApplication().isDevelopmentMode() ) {
			url = USURLProvider.makeURLDeveloperFriendly( url, context );
		}

		return url;
	}

	private static Map<Class, URLProvider> urlProviders() {
		if( _urlProviders == null ) {
			_urlProviders = new HashMap<>();
			// _urlProviders.put( UniqueIDStamped.class, new URLProviderUniqueIDStamped() );
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

	/**
	 * @return A direct connect version of the URL.
	 */
	public static String makeURLDeveloperFriendly( String url, WOContext context ) {
		NSMutableDictionary<String, Object> d = new NSMutableDictionary<>();
		d.setObjectForKey( url, "url" );
		url = context.directActionURLForActionNamed( InspectAction.class.getSimpleName() + "/handler", d );
		url = StringUtilities.replace( url, "&", "&amp;" );
		return url;
	}
}