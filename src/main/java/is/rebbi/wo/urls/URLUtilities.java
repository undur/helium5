package is.rebbi.wo.urls;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableDictionary;

import is.rebbi.core.util.StringUtilities;

public class URLUtilities {

	/**
	 * @return A direct connect version of the URL.
	 */
	public static String makeURLDeveloperFriendly( String url, WOContext context ) {
		NSMutableDictionary<String, Object> d = new NSMutableDictionary<>();
		d.setObjectForKey( url, "url" );
		// FIXME: url = context.directActionURLForActionNamed( InspectAction.class.getSimpleName() + "/handler", d );
		url = context.directActionURLForActionNamed( "InspectAction/handler", d );
		url = StringUtilities.replace( url, "&", "&amp;" );
		return url;
	}
}