package is.rebbi.wo.urls;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.routes.RouteTable;
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
			url = RouteTable.urlForDevelopment( url, context );
		}

		return url;
	}
}