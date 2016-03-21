package is.rebbi.wo.components.plists;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

public class USPlistEditor extends USPlistEditorAbstract {

	public USPlistEditor( WOContext context ) {
		super( context );
	}

	public boolean isDictionary() {
		return object() instanceof NSDictionary;
	}

	public boolean isArray() {
		return object() instanceof NSArray;
	}

	public boolean isValue() {
		return !isDictionary() && !isArray();
	}
}