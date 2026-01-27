package is.rebbi.wo.objectroutes.urls;

import com.webobjects.appserver.WOContext;

/**
 * Clean up this whole thing. It could really use some cleanup.
 */

public abstract class USURLProvider {

	/**
	 * @return The URL for viewing the given object.
	 */
	public static String urlForObjectInContext( final Object object, final WOContext context ) {

		String url = URLProviders.urlForObject( object );

		//		FIXME: Experimental. Hopefully we won't need this again, since our apps should now handle routed URLs directly // Hugi 2026-01-27
		//		if( !USSettings.generateFriendlyURLs() ) {
		//			url = RouteTable.urlForDevelopment( url, context );
		//		}

		return url;
	}
}