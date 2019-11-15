package is.rebbi.wo.components.admin;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.util.SWSettings;

public class USLoginPage extends ERXComponent {

	public String username;
	public String password;

	public USLoginPage( WOContext context ) {
		super( context );
	}

	public WOActionResults login() {

		if( SWSettings.adminUsername().equals( username ) && SWSettings.adminPassword().equals( password ) ) {
			return pageWithName( USStartPage.class );
		}

		return null;
	}
}