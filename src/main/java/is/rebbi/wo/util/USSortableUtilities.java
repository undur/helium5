package is.rebbi.wo.util;

import java.util.Collection;
import java.util.Enumeration;

import com.webobjects.foundation.NSMutableArray;

/**
 * Utility methods for working with sortables.
 */

public class USSortableUtilities {

	private USSortableUtilities() {}

	/**
	 * Indicates that this element is the first in it's list.
	 */
	public static boolean isFirst( USSortable object ) {
		return object.sortNumber().intValue() == 0;
	}

	/**
	 * Indicates that this element is the last in it's list.
	 */
	public static boolean isLast( USSortable object, Collection<? extends USSortable> siblings ) {
		return object.sortNumber().intValue() == (siblings.size() - 1);
	}

	/**
	 * Indicates that this element is the last in it's list.
	 */
	public static boolean moveUp( USSortable object, Collection<? extends USSortable> siblings ) {
		return object.sortNumber().intValue() == (siblings.size() - 1);
	}

	public static <E extends USSortable> void changeSortOrder( E object, NSMutableArray<E> siblings, int offset ) {
		int index = siblings.indexOfObject( object );

		if( index == -1 ) {
			throw new IllegalArgumentException( "The object provided is not contained in the [siblings] array" );
		}

		siblings.removeObjectAtIndex( index );
		siblings.insertObjectAtIndex( object, index + offset );

		Enumeration<E> e = siblings.objectEnumerator();

		while( e.hasMoreElements() ) {
			E sibling = e.nextElement();
			sibling.setSortNumber( siblings.indexOf( sibling ) );
		}
	}
}