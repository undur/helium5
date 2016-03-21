package is.rebbi.wo.components.plists;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * For editing an array of objects.
 */

public class USPlistEditorArray extends USPlistEditorAbstract {

	public int index;

	public USPlistEditorArray( WOContext context ) {
		super( context );
	}

	@Override
	public NSMutableArray object() {
		return (NSMutableArray)super.object();
	}

	public Object currentValue() {
		return object().objectAtIndex( index );
	}

	public void setCurrentValue( Object newObject ) {
		if( newObject != null ) {
			object().replaceObjectAtIndex( newObject, index );
		}
	}

	public WOActionResults add() {

		if( selectedObjectType.equals( TYPE_STRING ) ) {
			object().addObject( "" );
		}
		else if( selectedObjectType.equals( TYPE_DICTIONARY ) ) {
			object().addObject( new NSMutableDictionary<>() );
		}
		else if( selectedObjectType.equals( TYPE_ARRAY ) ) {
			object().addObject( new NSMutableArray<>() );
		}

		return null;
	}

	public WOActionResults remove() {
		object().removeObjectAtIndex( index );
		return null;
	}
}