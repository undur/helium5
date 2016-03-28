package is.rebbi.wo.components.error;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.ajax.AjaxUtils;
import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.Primary;

public class USSessionTimeoutPage extends ERXComponent {

	public USSessionTimeoutPage( WOContext context ) {
		super( context );
	}

	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );
		AjaxUtils.addStylesheetResourceInHead( c, r, Primary.frameworkBundleName(), "helium/css/bootstrap.min.css" );
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/vendor/bootstrap.min.js" );
	}

	public static WOResponse handleSessionRestorationErrorInContext( WOContext context ) {
		return ERXApplication.erxApplication().pageWithName( USSessionTimeoutPage.class, context ).generateResponse();
	}
}