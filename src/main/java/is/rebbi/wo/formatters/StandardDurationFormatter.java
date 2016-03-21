package is.rebbi.wo.formatters;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.util.USUtilities;

/**
 * Formats seconds and outputs a string formatted for days, hours, minutes and seconds.
 */

public class StandardDurationFormatter extends Format {

	private boolean _hideHoursIfZero;
	private boolean _hideSeconds;

	public StandardDurationFormatter() {
		this( false );
	}

	public StandardDurationFormatter( boolean hideHoursIfZero ) {
		_hideHoursIfZero = hideHoursIfZero;
	}

	public void setHideSeconds( boolean hideSeconds ) {
		_hideSeconds = hideSeconds;
	}

	@Override
	public StringBuffer format( Object obj, StringBuffer toAppendTo, FieldPosition pos ) {

		Integer seconds = USUtilities.integerFromObject( obj );

		StringBuilder b = new StringBuilder();

		if( seconds != null ) {
			int hours = seconds / 3600;
			seconds = seconds - hours * 3600;

			int minutes = seconds / 60;
			seconds = seconds - minutes * 60;

			boolean showHours = (hours > 0) || (hours == 0 && !_hideHoursIfZero);

			if( showHours ) {
				b.append( StringUtilities.padLeft( String.valueOf( hours ), "0", 2 ) );
				b.append( ":" );
			}

			b.append( StringUtilities.padLeft( String.valueOf( minutes ), "0", 2 ) );

			if( !_hideSeconds ) {
				b.append( ":" );
				b.append( StringUtilities.padLeft( String.valueOf( seconds ), "0", 2 ) );
			}

			toAppendTo.append( b.toString().trim() );
		}

		return toAppendTo;
	}

	@Override
	public Object parseObject( String source, ParsePosition status ) {

		source = source.substring( status.getIndex() );
		status.setIndex( source.length() );

		Long totalSeconds = null;

		if( StringUtilities.hasValue( source ) ) {

			totalSeconds = 0l;

			NSMutableArray<String> parts = NSArray.componentsSeparatedByString( source, ":" ).mutableClone();

			if( parts.count() > 0 ) {
				totalSeconds += Integer.parseInt( parts.lastObject() );
				parts.removeLastObject();
			}

			if( parts.count() > 0 ) {
				totalSeconds += Integer.parseInt( parts.lastObject() ) * 60;
				parts.removeLastObject();
			}

			if( parts.count() > 0 ) {
				totalSeconds += Integer.parseInt( parts.lastObject() ) * 3600;
			}
		}

		return totalSeconds;
	}
}