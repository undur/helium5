package is.rebbi.wo.components.admin;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.components.USViewWrapper;
import is.rebbi.wo.util.Inspection;

public class USEditWrapper extends USViewWrapper {

	public USEditWrapper( WOContext context ) {
		super( context );
	}
	
	public WOActionResults openListPage() {
	    return Inspection.openListPage( viewDefinition().entityClass() );
	}
}