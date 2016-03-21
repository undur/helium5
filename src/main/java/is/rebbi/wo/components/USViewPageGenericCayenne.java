package is.rebbi.wo.components;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;

public class USViewPageGenericCayenne<E extends DataObject> extends USViewPage<E> {

	public USViewPageGenericCayenne( WOContext context ) {
		super( context );
	}
}