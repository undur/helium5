package is.rebbi.wo.formatters;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.util.USUtilities;

/**
 * Formats seconds and outputs a string formatted for days, hours, minutes and seconds.
 */

public class DurationFormatterDecimal extends Format {

	@Override
	public StringBuffer format( Object obj, StringBuffer toAppendTo, FieldPosition pos ) {

		Double seconds = USUtilities.doubleFromObject( obj );

		if( seconds != null && seconds != 0 ) {
			double hours = seconds / 60d / 60d;
			toAppendTo.append( StringUtilities.formatDouble( hours, 0, 2, false ) );
		}

		return toAppendTo;
	}

	@Override
	public Object parseObject( String source, ParsePosition pos ) {
		throw new RuntimeException( "Parsing is not implemented" );
	}
}