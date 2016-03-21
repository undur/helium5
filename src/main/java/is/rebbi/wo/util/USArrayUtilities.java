package is.rebbi.wo.util;

import java.text.Collator;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSComparator.ComparisonException;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSRange;

import er.extensions.foundation.ERXArrayUtilities;

/**
 * Various utility methods applying to NSArrays.
 */

public class USArrayUtilities {

	private static final Logger logger = LoggerFactory.getLogger( USArrayUtilities.class );

	private static final Random RANDOM = new Random();

	/**
	 * No instances created, ever.
	 */
	private USArrayUtilities() {}

	/**
	 * @return True if the given Collection is not null and contains anything.
	 */
	public static boolean hasObjects( Collection<?> list ) {
		return list != null && !list.isEmpty();
	}

	/**
	 * Returns an array of the first letters of the keyPath of anArray
	 * Note: Strings are trimmed, meaning whitespace before strings is ignored.
	 */
	public static NSArray<String> firstLettersForKeyPathInArray( String keyPath, NSArray<?> array ) {

		if( !hasObjects( array ) ) {
			return NSArray.emptyArray();
		}

		NSMutableSet<String> set = new NSMutableSet<>();

		for( Object object : array ) {
			Object value = NSKeyValueCodingAdditions.Utility.valueForKeyPath( object, keyPath );

			if( value != null ) {

				String stringValue = value.toString();

				if( stringValue != null && stringValue.length() > 0 ) {
					set.addObject( stringValue.substring( 0, 1 ).toUpperCase() );
				}
			}
		}

		return sortedArrayUsingIcelandicComparator( set.allObjects() );
	}

	/**
	 * Returns a new array sorted according to the Icelandic alphabet.
	 *
	 * @param array the array to sort
	 * @param keyPath the keypath to sort by
	 */
	public static NSArray<String> sortedArrayUsingIcelandicComparator( NSArray<String> array ) {
		return sortedArrayUsingIcelandicComparator( array, true );
	}

	/**
	 * Returns a new array sorted according to the Icelandic alphabet.
	 *
	 * @param array the array to sort
	 * @param keyPath the keypath to sort by
	 */
	public static NSArray<String> sortedArrayUsingIcelandicComparator( NSArray<String> array, boolean ascending ) {

		if( !hasObjects( array ) ) {
			return NSArray.emptyArray();
		}

		NSComparator comparator = null;

		if( ascending ) {
			comparator = USGenericNSComparator.IcelandicAscendingComparator;
		}
		else {
			comparator = USGenericNSComparator.IcelandicDescendingComparator;
		}

		try {
			return array.sortedArrayUsingComparator( comparator );
		}
		catch( ComparisonException e ) {
			logger.error( "Unable to sort array, returning original array", e );
			return array;
		}
	}

	/**
	 * Returns a new array sorted according to the icelandic alphabet on the given keypath of the array objects
	 *
	 * @param array the array to sort
	 * @param keyPath the keyPath to sort by
	 */
	public static <E> NSArray<E> sortedArrayUsingIcelandicComparator( NSArray<E> array, String keyPath ) {

		if( !hasObjects( array ) ) {
			return NSArray.emptyArray();
		}

		Collator collator = Collator.getInstance( new java.util.Locale( "is", "IS" ) );
		USGenericNSComparator icelandicComparator = new USGenericNSComparator( collator, keyPath, true, true );

		try {
			return array.sortedArrayUsingComparator( icelandicComparator );
		}
		catch( Exception e ) {
			logger.error( "Unable to sort array, returning original array", e );
			return array;
		}
	}

	/**
	 * Returns a random object from the specified array. If the array contains no objects or is null, null is returned.
	 */
	public static <E> E randomObject( List<E> list ) {

		if( !hasObjects( list ) ) {
			return null;
		}

		int count = list.size();
		int number = RANDOM.nextInt( count );
		E object = list.get( number );
		return object;
	}

	/**
	 * Constructs a new array containing the specified number of random objects form the source array.
	 * If the array is null, an empty array is returned.
	 * If the source array is shorter or equal in length to the count, the original array is returned randomized.
	 *
	 * @param array The array to read from.
	 * @param count The max size of the return array.
	 */
	public static <E> NSArray<E> randomObjects( NSArray<E> array, int count ) {

		if( !hasObjects( array ) ) {
			return NSArray.emptyArray();
		}

		if( count >= array.count() ) {
			return randomized( array );
		}

		NSMutableArray<E> originalArrayMutable = array.mutableClone();
		NSMutableArray<E> result = new NSMutableArray<>();

		for( int i = count; i > 0; i-- ) {
			E randomObject = randomObject( originalArrayMutable );
			result.addObject( randomObject );
			originalArrayMutable.removeObject( randomObject );
		}

		return result.immutableClone();
	}

	/**
	 * Randomizes the objects in an array
	 */
	public static <E> NSArray<E> randomized( NSArray<E> array ) {

		if( !hasObjects( array ) ) {
			return NSArray.emptyArray();
		}

		NSMutableArray<E> originalArray = array.mutableClone();
		NSMutableArray<E> resultArray = new NSMutableArray<>();

		while( originalArray.count() > 0 ) {
			E o = randomObject( originalArray );
			resultArray.addObject( o );
			originalArray.removeObject( o );
		}

		return resultArray.immutableClone();
	}

	/**
	 * Reverses the order of the objects in the given array.
	 */
	public static <E> NSArray<E> reversed( NSArray<E> array ) {

		if( !hasObjects( array ) ) {
			return NSArray.emptyArray();
		}

		Enumeration<E> e = array.reverseObjectEnumerator();
		NSMutableArray<E> b = new NSMutableArray<>();

		while( e.hasMoreElements() ) {
			b.addObject( e.nextElement() );
		}

		return b.immutableClone();
	}

	/**
	 * This method will check an NSArray for duplicate objects.
	 * If it finds any duplicates, it will return an NSArray containing one instance of each duplicate object.
	 */
	public static <E> NSArray<E> arrayWithoutDuplicates( NSArray<E> a ) {
		return ERXArrayUtilities.arrayWithoutDuplicates( a );
	}

	/**
	 * Returns the array unmodified if it contains [maxCount] or fewer items, otherwise
	 * it returns the first [maxCount] items.
	 *
	 * @param array The array to work with
	 * @param maxCount The maximum number of items to return
	 */
	public static <E> NSArray<E> maxObjectsFromArray( NSArray<E> array, Integer maxCount ) {

		if( !hasObjects( array ) ) {
			return NSArray.emptyArray();
		}

		if( maxCount == null ) {
			return array;
		}

		if( maxCount < 0 ) {
			throw new IllegalArgumentException( "The parameter 'maxCount' must be a positive integer" );
		}

		if( array.count() <= maxCount ) {
			return array;
		}

		return array.subarrayWithRange( new NSRange( 0, maxCount ) );
	}

	/**
	 * Groups an array by multiple keypaths
	 *
	 * @param objects An array of objects to group.
	 * @param keyPaths The keypaths to group the objects by.
	 * @return A dictionary with the "distinct" values as a key, and the objects conforming to that distinct value set as object.
	 */
	public static <E extends NSKeyValueCodingAdditions> NSDictionary<NSDictionary<String, Object>, NSArray<E>> arrayGroupedByKeyPaths( NSArray<E> objects, NSArray<String> keyPaths ) {

		if( objects == null ) {
			return NSDictionary.emptyDictionary();
		}

		if( keyPaths == null ) {
			throw new IllegalArgumentException( "The 'keyPaths' parameter must not be null" );
		}

		NSArray<NSDictionary<String, Object>> distinctObjects = distinctCombinationsWithKeyPaths( objects, keyPaths );

		NSMutableDictionary<NSDictionary<String, Object>, NSArray<E>> result = new NSMutableDictionary<>();

		for( NSDictionary<String, Object> distinctObject : distinctObjects ) {
			EOQualifier distinctQualifier = EOQualifier.qualifierToMatchAllValues( distinctObject );
			NSArray<E> filteredArray = EOQualifier.filteredArrayWithQualifier( objects, distinctQualifier );
			result.setObjectForKey( filteredArray, distinctObject );
		}

		return result;
	}

	/**
	 * Construct an array of dictionaries with all value combinations in the given keyPaths.
	 *
	 * @param objects The object array to go through
	 * @param keyPaths The keyPaths to look at
	 * @return An array of distinct objects
	 */
	public static <E extends NSKeyValueCodingAdditions> NSArray<NSDictionary<String, Object>> distinctCombinationsWithKeyPaths( NSArray<E> objects, NSArray<String> keyPaths ) {

		if( objects == null ) {
			return NSArray.emptyArray();
		}

		if( keyPaths == null ) {
			throw new IllegalArgumentException( "The 'keyPaths' parameter must not be null" );
		}

		NSMutableSet<NSDictionary<String, Object>> a = new NSMutableSet<>();

		for( E object : objects ) {
			NSMutableDictionary<String, Object> d = new NSMutableDictionary<>();
			for( String keyPath : keyPaths ) {
				Object value = object.valueForKeyPath( keyPath );

				if( value != null ) {
					d.setObjectForKey( value, keyPath );
				}
				else {
					d.setObjectForKey( NSKeyValueCoding.NullValue, keyPath );
				}
			}
			a.addObject( d );
		}

		return a.allObjects();
	}
}