package x.deprecated;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.components.admin.USLoginPage;

/**
 * Main entry point into the system.
 *
 * FIXME: WE should really delete this and change it to a route
 */

@Deprecated
public class LoginAction extends ERXDirectAction {

	public LoginAction( WORequest r ) {
		super( r );
	}

	@Deprecated
	public WOActionResults loginAction() {
		return pageWithName( USLoginPage.class );
	}
}