package is.rebbi.wo.components.admin.look.adminlte;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.Primary;
import is.rebbi.wo.components.USViewPage;

public class USAdminLTELook extends USViewPage {

	public USAdminLTELook( WOContext context ) {
		super( context );
	}

	public String frameworkBundleName() {
		return Primary.frameworkBundleName();
	}
}