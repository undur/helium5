package is.rebbi.wo.formatters;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import is.rebbi.core.util.StringUtilities;

/**
 * Formats seconds and outputs a string formatted for days, hours, minutes and seconds.
 */

public class DurationFormatter extends Format {

	@Override
	public StringBuffer format( Object obj, StringBuffer toAppendTo, FieldPosition pos ) {

		StringBuilder b = new StringBuilder();

		if( obj != null ) {
			Integer seconds = ((Number)obj).intValue();
			int hours = seconds / 3600;
			seconds = seconds - hours * 3600;

			int minutes = seconds / 60;
			seconds = seconds - minutes * 60;

			if( hours > 0 ) {
				b.append( hours );
				b.append( "h" );
			}

			if( minutes > 0 ) {
				b.append( " " );
				b.append( minutes );
				b.append( "m" );
			}

			if( seconds > 0 ) {
				b.append( " " );
				b.append( seconds );
				b.append( "s" );
			}

			toAppendTo.append( b.toString().trim() );
		}

		return toAppendTo;
	}

	@Override
	public Object parseObject( String source, ParsePosition status ) {

		source = source.substring( status.getIndex() );
		status.setIndex( source.length() );

		Long totalSeconds = 0l;

		Long hours = number( "([0-9]*)h", source );
		Long minutes = number( "([0-9]*)m", source );
		Long seconds = number( "([0-9]*)s", source );

		if( hours != null ) {
			totalSeconds += hours * 3600;
		}

		if( minutes != null ) {
			totalSeconds += minutes * 60;
		}

		if( seconds != null ) {
			totalSeconds += seconds;
		}

		return totalSeconds;
	}

	private static Long number( String regex, String source ) {
		String hourRegex = regex;
		Pattern hourPattern = Pattern.compile( hourRegex );
		Matcher hourMatcher = hourPattern.matcher( source );

		String result = null;

		while( hourMatcher.find() ) {
			result = hourMatcher.group( 1 );
		}

		if( StringUtilities.hasValue( result ) ) {
			return Long.parseLong( result );
		}

		return null;
	}
}