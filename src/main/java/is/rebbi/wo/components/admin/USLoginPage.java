package is.rebbi.wo.components.admin;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.Primary;
import is.rebbi.wo.util.USSettings;

public class USLoginPage extends ERXComponent {

	public String username;
	public String password;

	public USLoginPage( WOContext context ) {
		super( context );
	}

	public String frameworkBundleName() {
		return Primary.frameworkBundleName();
	}

	public WOActionResults login() {

		if( USSettings.adminUsername().equals( username ) && USSettings.adminPassword().equals( password ) ) {
			return pageWithName( USStartPage.class );
		}

		return null;
	}
}