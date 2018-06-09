package is.rebbi.wo.urls.handlers;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSDictionary;

import is.rebbi.wo.util.USHTTPUtilities;

public class URLHandlerPasswordReset extends URLHandler {

	public URLHandlerPasswordReset( String url, WOContext context ) {
		super( url, context );
	}

	@Override
	public WOActionResults generateResponse() {
		NSDictionary<String, Object> params = new NSDictionary<>( path().getString( 2 ), "key" );
		String searchURL = context().directActionURLForActionNamed( "SWPasswordResetAction" /* FIXME: SWPasswordResetAction.class.getSimpleName() */, params );
		return USHTTPUtilities.redirectTemporary( searchURL );
	}
}