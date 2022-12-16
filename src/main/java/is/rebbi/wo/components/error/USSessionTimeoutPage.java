package is.rebbi.wo.components.error;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXResponseRewriter;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.Primary;

public class USSessionTimeoutPage extends ERXComponent {

	public USSessionTimeoutPage( WOContext context ) {
		super( context );
	}

	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );
		ERXResponseRewriter.addStylesheetResourceInHead( r, c, Primary.frameworkBundleName(), "bootstrap-3.4.1/css/bootstrap.min.css" );
		ERXResponseRewriter.addScriptResourceInHead( r, c, Primary.frameworkBundleName(), "bootstrap-3.4.1/bootstrap.min.js" );
	}

	public static WOResponse handleSessionRestorationErrorInContext( WOContext context ) {
		return ERXApplication.erxApplication().pageWithName( USSessionTimeoutPage.class, context ).generateResponse();
	}
}