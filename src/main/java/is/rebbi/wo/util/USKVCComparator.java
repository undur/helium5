package is.rebbi.wo.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.webobjects.foundation.NSKeyValueCodingAdditions;

/**
 * Comparator for comparing/sorting objects based on a keypath.
 */

public class USKVCComparator<T> implements Comparator<T> {

	private final Comparator _wrappedComparator;
	private final String _keyPath;

	/**
	 * Creates a case insensitive comparator
	 */
	public static USKVCComparator of( final String keyPath ) {

		// CHECKME: We shouldn't really be defaulting to Icelandic. Collation isn't really the responsibility of this class
		// CHECKME: Are collations thread safe? IF so, we wouldn't need to construct an instance each time here
		final Collator defaultCollator = Collator.getInstance( Locale.of( "is", "IS" ) );
		defaultCollator.setStrength( Collator.SECONDARY );

		return new USKVCComparator( keyPath, defaultCollator );
	}

	private USKVCComparator( final String keyPath, final Comparator comparator ) {
		_wrappedComparator = comparator;
		_keyPath = keyPath;
	}

	@Override
	public int compare( final T object1, final T object2 ) {

		final Object value1 = object1 == null ? null : NSKeyValueCodingAdditions.Utility.valueForKeyPath( object1, _keyPath );
		final Object value2 = object2 == null ? null : NSKeyValueCodingAdditions.Utility.valueForKeyPath( object2, _keyPath );

		if( value1 == value2 ) {
			return 0;
		}

		// Start by handling null values
		if( value1 == null && value2 != null ) {
			return -1;
		}

		if( value1 != null && value2 == null ) {
			return 1;
		}

		if( value1 instanceof Comparable comparableValue1 && !(value1 instanceof String) ) {
			return comparableValue1.compareTo( value2 );
		}

		final String string1 = value1.toString();
		final String string2 = value2.toString();
		return _wrappedComparator.compare( string1, string2 );
	}
}