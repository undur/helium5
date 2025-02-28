package is.rebbi.wo.routes;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableDictionary;

import is.rebbi.core.util.StringUtilities;

public class Routes {

	/**
	 * @return The route usable for development (i.e. invoking the direct action directly)
	 */
	public static String urlForDevelopment( String url, WOContext context ) {
		final NSMutableDictionary<String, Object> params = new NSMutableDictionary<>( url, "url" );
		url = context.directActionURLForActionNamed( RouteAction.class.getSimpleName() + "/handler", params );
		url = StringUtilities.replace( url, "&", "&amp;" );
		return url;
	}
}