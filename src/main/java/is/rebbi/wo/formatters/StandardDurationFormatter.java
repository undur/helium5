package is.rebbi.wo.formatters;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import is.rebbi.core.util.ListUtilities;
import is.rebbi.core.util.StringUtilities;

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

		StringBuilder b = new StringBuilder();

		if( obj != null ) {
			Integer seconds = ((Number)obj).intValue();
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

			String[] split = source.split( Pattern.quote( ":" ) );
			List<String> parts = new ArrayList<>( Arrays.asList( split ) );

			if( parts.size() > 0 ) {
				totalSeconds += Integer.parseInt( ListUtilities.lastObject( parts ) );
				parts.remove( parts.size() - 1 );
			}

			if( parts.size() > 0 ) {
				totalSeconds += Integer.parseInt( ListUtilities.lastObject( parts ) ) * 60;
				parts.remove( parts.size() - 1 );
			}

			if( parts.size() > 0 ) {
				totalSeconds += Integer.parseInt( ListUtilities.lastObject( parts ) ) * 3600;
			}
		}

		return totalSeconds;
	}
}