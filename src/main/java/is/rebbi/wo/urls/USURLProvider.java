package is.rebbi.wo.urls;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXWOContext;
import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.routes.RouteAction;
import is.rebbi.wo.util.SWSettings;

/**
 * Clean up this whole thing. It could really use some cleanup.
 */

public abstract class USURLProvider {

	private static Map<Class, URLProvider> _urlProviders;

	public static String urlForObject( final Object object ) {
		final URLProvider urlProvider = urlProviderForClass( object.getClass() );

		if( urlProvider == null ) {
			throw new NullPointerException( "No URLProvider registered for objects of class: " + object.getClass() );
		}

		return urlProvider.urlForObject( object );
	}

	/**
	 * @return The URL for viewing the given object.
	 */
	public static String urlForObjectInContext( final Object object, final WOContext context ) {

		String url = urlForObject( object );

		if( !SWSettings.generateFriendlyURLs() ) {
			url = urlForDevelopment( url, context );
		}

		if( context == null ) {
			url = addProtocolAndHost( url );
		}

		return url;
	}

	@Deprecated
	private static String addProtocolAndHost( String url ) {
		StringBuilder b = new StringBuilder();
		b.append( "http" );
		b.append( "://" );
		b.append( SWSettings.defaultDomainName() );
		b.append( url );
		return b.toString();
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

	/**
	 * @return A direct connect version of the URL.
	 */
	private static String urlForDevelopment( String url, WOContext context ) {

		if( context == null ) {
			context = ERXWOContext.currentContext();
		}

		NSMutableDictionary<String, Object> d = new NSMutableDictionary<>();
		d.setObjectForKey( url, "url" );
		url = context.directActionURLForActionNamed( RouteAction.class.getSimpleName() + "/handler", d );
		url = StringUtilities.replace( url, "&", "&amp;" );
		return url;
	}
}