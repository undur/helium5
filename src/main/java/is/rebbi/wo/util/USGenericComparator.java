package is.rebbi.wo.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.webobjects.foundation.NSKeyValueCodingAdditions;

/**
 * Comparator for comparing/sorting objects based on a keypath
 */

public class USGenericComparator<T> implements Comparator<T> {

	private final Collator _collator;
	private final String _keyPath;
	private final boolean _ascending;
	private final boolean _caseInsensitive;

	private static final int OrderedAscending = -1;
	private static final int OrderedSame = 0;
	private static final int OrderedDescending = 1;

	/**
	 * A Comparator for sorting Icelandic text alphabetically in a descending order.
	 */
	public static final Comparator IcelandicDescendingComparator = new USGenericComparator._IcelandicComparator( false, true );

	/**
	 * A Comparator for sorting Icelandic text alphabetically in an ascending order.
	 */
	public static final Comparator IcelandicAscendingComparator = new USGenericComparator._IcelandicComparator( true, true );

	public USGenericComparator( final String keyPath ) {
		this( keyPath, true, false );
	}

	public USGenericComparator( final String keyPath, final boolean ascending, final boolean caseInsensitive ) {
		this( Collator.getInstance( new Locale( "is", "IS" ) ), keyPath, ascending, caseInsensitive );
	}

	public USGenericComparator( final Collator collator, final String keyPath, final boolean ascending, final boolean caseInsensitive ) {
		_collator = collator;
		_keyPath = keyPath;
		_ascending = ascending;
		_caseInsensitive = caseInsensitive;
	}

	public void sort( List<T> list ) {
		Collections.sort( list, this );
	}

	public List<T> sorted( List<T> list ) {
		List<T> listCopy = new ArrayList<>( list );
		Collections.sort( listCopy, this );
		return listCopy;
	}

	@Override
	public int compare( T kvc1, T kvc2 ) {

		Object value1 = NSKeyValueCodingAdditions.Utility.valueForKeyPath( kvc1, _keyPath );
		Object value2 = NSKeyValueCodingAdditions.Utility.valueForKeyPath( kvc2, _keyPath );

		if( value1 == value2 ) {
			return OrderedSame;
		}

		if( (value1 != null && value1 instanceof Comparable) && !(value1 instanceof String) ) {
			int result = ((Comparable)value1).compareTo( value2 );
			return _ascending ? result : result * -1;
		}

		String string1;
		String string2;

		if( value1 != null ) {
			string1 = value1.toString();
		}
		else {
			string1 = "";
		}

		if( value2 != null ) {
			string2 = value2.toString();
		}
		else {
			string2 = "";
		}

		if( string1 == string2 ) {
			return OrderedSame;
		}

		int i = _collator.compare( string1, string2 );

		if( _caseInsensitive ) {
			i = _collator.compare( string1.toUpperCase(), string2.toUpperCase() );
		}
		else {
			i = _collator.compare( string1, string2 );
		}

		if( i < 0 ) {
			return _ascending ? OrderedAscending : OrderedDescending;
		}

		if( i > 0 ) {
			return _ascending ? OrderedDescending : OrderedAscending;
		}
		else {
			return OrderedSame;
		}
	}

	/**
	 * A class for sorting string objects in correct Icelandic order.
	 */
	private static class _IcelandicComparator implements Comparator {

		private final boolean _ascending;
		private final boolean _caseInsensitive;
		private static Collator collator = Collator.getInstance( new Locale( "is", "IS" ) );

		@Override
		public int compare( Object obj, Object obj1 ) {

			if( obj == null || obj1 == null || !(obj instanceof String) || !(obj1 instanceof String) ) {
				throw new IllegalArgumentException( "Unable to compare objects. Objects should be instance of class String. Comparison was made with " + obj + " and " + obj1 + "." );
			}

			if( obj == obj1 ) {
				return 0;
			}

			int i = collator.compare( (String)obj, (String)obj1 );

			if( _caseInsensitive ) {
				i = collator.compare( ((String)obj).toUpperCase(), ((String)obj1).toUpperCase() );
			}
			else {
				i = collator.compare( (String)obj, (String)obj1 );
			}

			if( i < 0 ) {
				return _ascending ? -1 : 1;
			}

			if( i > 0 ) {
				return _ascending ? 1 : -1;
			}
			else {
				return 0;
			}
		}

		public _IcelandicComparator() {
			this( true, true );
		}

		public _IcelandicComparator( boolean flag, boolean flag1 ) {
			_ascending = flag;
			_caseInsensitive = flag1;
		}
	}
}