package is.rebbi.wo.components;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;

public class USViewPageGeneric<E extends DataObject> extends USViewPage<E> {

	public USViewPageGeneric( WOContext context ) {
		super( context );
	}
}