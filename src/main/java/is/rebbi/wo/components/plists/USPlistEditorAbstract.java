package is.rebbi.wo.components.plists;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import is.rebbi.wo.components.USBaseComponent;

/**
 * Array and dictionary editors inherit from this guy.
 */

public abstract class USPlistEditorAbstract extends USBaseComponent {

	public static final String TYPE_ARRAY = "Array";
	public static final String TYPE_DICTIONARY = "Dictionary";
	public static final String TYPE_STRING = "String";

	public String currentObjectType;
	public String selectedObjectType;
	private Object _object;

	public USPlistEditorAbstract( WOContext c ) {
		super( c );
	}

	public Object object() {
		return _object;
	}

	public void setObject( Object value ) {
		_object = value;
	}

	/**
	 * Types of objects that can be inserted into the dictionary.
	 */
	public NSArray<String> objectTypes() {
		return new NSArray<>( new String[] { TYPE_STRING, TYPE_DICTIONARY, TYPE_ARRAY } );
	}
}