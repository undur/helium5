package is.rebbi.wo.urls.providers;

import com.webobjects.appserver.WOContext;

/**
 * A URLProvider should be able to generate URLs for objects of a specific type.
 */

public abstract class URLProvider {

	public abstract String urlForObject( Object object, WOContext context );
}