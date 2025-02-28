package is.rebbi.wo.routes;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableDictionary;

public class Routes {

	/**
	 * @return The route usable for development (i.e. invoking the direct action directly)
	 */
	public static String urlForDevelopment( String url, WOContext context ) {
		final NSMutableDictionary<String, Object> params = new NSMutableDictionary<>( url, "url" );
		url = context.directActionURLForActionNamed( RouteAction.class.getSimpleName() + "/handler", params );
		url = url.replace( "&", "&amp;" );
		return url;
	}
}