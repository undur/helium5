package is.rebbi.wo.components.error;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;

public class USSessionTimeoutPage extends ERXComponent {

	public USSessionTimeoutPage( WOContext context ) {
		super( context );
	}

	public static WOResponse handleSessionRestorationErrorInContext( WOContext context ) {
		return ERXApplication.erxApplication().pageWithName( USSessionTimeoutPage.class, context ).generateResponse();
	}
}