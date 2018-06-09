package is.rebbi.wo.urls.handlers;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import is.rebbi.wo.components.admin.USLoginPage;

public class URLHandlerLogin extends URLHandler {

	public URLHandlerLogin( String url, WOContext context ) {
		super( url, context );
	}

	@Override
	public WOActionResults generateResponse() {
		return ERXApplication.erxApplication().pageWithName( USLoginPage.class, context() );
	}
}