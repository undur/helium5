package is.rebbi.wo.components.admin.look.adminlte2;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXResponseRewriter;
import is.rebbi.wo.Primary;
import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.menu.USMenu;
import is.rebbi.wo.util.USSettings;

public class USAdminLTE2Look extends USViewPage {

	public USAdminLTE2Look( WOContext context ) {
		super( context );
	}

	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );
		ERXResponseRewriter.addScriptResourceInHead( r, context(), Primary.frameworkBundleName(), "bootstrap_prototype_conflict_fix.js" );
	}

	public String frameworkBundleName() {
		return Primary.frameworkBundleName();
	}

	public USMenu menu() {
		return Primary.heliumMenu();
	}

	public String siteName() {
		return USSettings.name();
	}
}