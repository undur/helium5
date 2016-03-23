package is.rebbi.wo.util;

import java.util.GregorianCalendar;

import com.webobjects.foundation.NSTimestamp;

/**
 * Timestamp related utility classes.
 */

public class USTimestampUtilities {

	/**
	 * All methods are static.
	 */
	private USTimestampUtilities() {}

	/**
	 * Normalizes a timestamp to midnight on the given day.
	 */
	public static NSTimestamp normalizeTimestampToMidnight( NSTimestamp oldTimestamp ) {
		GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
		calendar.setTime( oldTimestamp );

		int hourOfDay = calendar.get( GregorianCalendar.HOUR_OF_DAY );
		int minuteOfHour = calendar.get( GregorianCalendar.MINUTE );
		int secondOfMinute = calendar.get( GregorianCalendar.SECOND );

		return oldTimestamp.timestampByAddingGregorianUnits( 0, 0, 0, -hourOfDay, -minuteOfHour, -secondOfMinute );
	}

	/**
	 * This method will normalize an NSTimestamp to the beginning of the month,
	 * i.e., creates a timestamp set to midnight on the first day of the month.
	 *
	 * @param timestamp The timestamp to normalize
	 */
	public static NSTimestamp normalizeTimestampForMonth( NSTimestamp timestamp ) {
		GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
		calendar.setTime( timestamp );

		int dayOfMonth = calendar.get( GregorianCalendar.DAY_OF_MONTH );
		int hourOfDay = calendar.get( GregorianCalendar.HOUR_OF_DAY );
		int minuteOfHour = calendar.get( GregorianCalendar.MINUTE );
		int secondOfMinute = calendar.get( GregorianCalendar.SECOND );

		return timestamp.timestampByAddingGregorianUnits( 0, 0, -dayOfMonth + 1, -hourOfDay, -minuteOfHour, -secondOfMinute );
	}

	/**
	 * Normalizes a timestamp to the year.
	 */
	public static NSTimestamp normalizeTimestampForYear( NSTimestamp oldTimestamp ) {
		GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
		calendar.setTime( oldTimestamp );

		int month = calendar.get( GregorianCalendar.MONTH );
		int dayOfMonth = calendar.get( GregorianCalendar.DAY_OF_MONTH );
		int hourOfDay = calendar.get( GregorianCalendar.HOUR_OF_DAY );
		int minuteOfHour = calendar.get( GregorianCalendar.MINUTE );
		int secondOfMinute = calendar.get( GregorianCalendar.SECOND );

		return oldTimestamp.timestampByAddingGregorianUnits( 0, -month, -dayOfMonth + 1, -hourOfDay, -minuteOfHour, -secondOfMinute );
	}

	public static NSTimestamp beginningOfCurrentMonth() {
		return normalizeTimestampForMonth( new NSTimestamp() );
	}

	public static NSTimestamp beginningOfLastMonth() {
		return normalizeTimestampForMonth( new NSTimestamp() ).timestampByAddingGregorianUnits( 0, -1, 0, 0, 0, 0 );
	}
}