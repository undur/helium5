package is.rebbi.wo.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.util.Inspection;

public class USEditWrapper extends USViewWrapper {

	public USEditWrapper( WOContext context ) {
		super( context );
	}

	public WOActionResults editGeneric() {
		return Inspection.editObjectInContextUsingGenericComponent( selectedObject(), context() );
	}

	public WOActionResults viewGeneric() {
		return Inspection.inspectObjectInContextUsingGenericComponent( selectedObject(), context() );
	}
}