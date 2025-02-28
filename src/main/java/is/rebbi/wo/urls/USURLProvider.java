package is.rebbi.wo.urls;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableDictionary;

import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.routes.RouteAction;
import is.rebbi.wo.util.USSettings;

/**
 * Clean up this whole thing. It could really use some cleanup.
 */

public abstract class USURLProvider {

	/**
	 * @return The URL for viewing the given object.
	 */
	public static String urlForObjectInContext( final Object object, final WOContext context ) {

		String url = URLProviders.urlForObject( object );

		if( !USSettings.generateFriendlyURLs() ) {
			url = urlForDevelopment( url, context );
		}

		return url;
	}

	/**
	 * @return A direct connect version of the URL.
	 */
	public static String urlForDevelopment( String url, WOContext context ) {
		final NSMutableDictionary<String, Object> params = new NSMutableDictionary<>( url, "url" );
		url = context.directActionURLForActionNamed( RouteAction.class.getSimpleName() + "/handler", params );
		url = StringUtilities.replace( url, "&", "&amp;" );
		return url;
	}
}