package is.rebbi.wo.urls;

import com.webobjects.appserver.WOContext;

/**
 * A URLProvider should be able to generate URLs for objects of a specific type.
 */

public abstract class URLProvider {

	public abstract String urlForObject( Object object, WOContext context );

	public abstract String urlForObject( String entityName, Object identifier, WOContext context );
}