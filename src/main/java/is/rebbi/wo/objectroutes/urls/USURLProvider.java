package is.rebbi.wo.objectroutes.urls;

import com.webobjects.appserver.WOContext;

/**
 * Clean up this whole thing. It could really use some cleanup.
 */

public abstract class USURLProvider {

	/**
	 * @return The URL for viewing the given object.
	 */
	@Deprecated
	public static String urlForObjectInContext( final Object object, final WOContext context ) {
		return URLProviders.urlForObject( object );
	}
}