package is.rebbi.wo.urls.handlers;

import java.util.function.BiFunction;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSDictionary;

import is.rebbi.wo.util.USHTTPUtilities;

public class URLHandlerPasswordReset implements URLHandler {

	public static final String PASSWORD_RESET_REQUEST_PREFIX = "/passwordResetRequest/";

	@Override
	public String pattern() {
		return PASSWORD_RESET_REQUEST_PREFIX;
	}

	@Override
	public BiFunction<String, WOContext, WOActionResults> execute() {
		return ( url, context ) -> {
			String afterPrefix = url.substring( PASSWORD_RESET_REQUEST_PREFIX.length() );
			NSDictionary<String, Object> params = new NSDictionary<>( afterPrefix, "key" );
			String searchURL = context.directActionURLForActionNamed( "SWPasswordResetAction" /* FIXME: SWPasswordResetAction.class.getSimpleName() */, params );
			return USHTTPUtilities.redirectTemporary( searchURL );
		};
	}
}