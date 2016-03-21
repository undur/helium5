package is.rebbi.wo.components.plists;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Editing of property lists.
 */

public class USPlistEditorDictionary extends USPlistEditorAbstract {

	/**
	 * The name of a new key to add
	 */
	public String newKey;

	/**
	 * The key currently being iterated over
	 */
	public String currentKey;

	public USPlistEditorDictionary( WOContext context ) {
		super( context );
	}

	@Override
	public NSMutableDictionary object() {
		return (NSMutableDictionary)super.object();
	}

	public NSArray allKeys() {
		if( object() != null ) {
			return object().allKeys();
		}

		return NSArray.emptyArray();
	}

	public Object currentValue() {
		return object().valueForKey( currentKey );
	}

	public void setCurrentValue( Object newValue ) {
		if( newValue != null ) {
			object().takeValueForKey( newValue, currentKey );
		}
		else {
			object().takeValueForKey( "", currentKey );
		}
	}

	public WOActionResults add() {
		if( newKey != null ) {

			if( selectedObjectType.equals( TYPE_STRING ) ) {
				object().takeValueForKey( "", newKey );
			}
			else if( selectedObjectType.equals( TYPE_DICTIONARY ) ) {
				object().takeValueForKey( new NSMutableDictionary<>(), newKey );
			}
			else if( selectedObjectType.equals( TYPE_ARRAY ) ) {
				object().takeValueForKey( new NSMutableArray<>(), newKey );
			}

			newKey = null;
		}

		return null;
	}

	public WOActionResults remove() {
		object().removeObjectForKey( currentKey );
		return null;
	}
}