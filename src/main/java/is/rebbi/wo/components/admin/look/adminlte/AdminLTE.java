package is.rebbi.wo.components.admin.look.adminlte;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;

public class AdminLTE extends ERXDirectAction {

	public AdminLTE( WORequest r ) {
		super( r );
	}

	@Override
	public WOActionResults defaultAction() {
		return pageWithName( USAdminLTELook.class );
	}
}