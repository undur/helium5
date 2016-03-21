package is.rebbi.wo.formatters;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import is.rebbi.core.util.DateUtilities;

public class RelativeDateFormatter extends Format {

	private boolean _showTime;

	@Override
	public StringBuffer format( Object obj, StringBuffer toAppendTo, FieldPosition pos ) {

		if( obj != null ) {
			toAppendTo.append( dateToRelativeString( (Date)obj, DateUtilities.beginningOfDay( new Date() ), _showTime ) );
		}

		return toAppendTo;
	}

	public void setShowTime( boolean showTime ) {
		_showTime = showTime;
	}

	@Override
	public Object parseObject( String source, ParsePosition pos ) {
		throw new RuntimeException( "Parsing is not implemented" );
	}

	private static String dateToRelativeString( Date dateToFormat, Date relativeToDate, boolean showTime ) {

		if( DateUtilities.isToday( dateToFormat ) ) {
			StringBuilder b = new StringBuilder();
			b.append( "Í dag" );

			if( showTime ) {
				b.append( " kl. " );
				b.append( timeFormat().format( dateToFormat ) );
			}

			return b.toString();
		}

		relativeToDate = DateUtilities.dateByAddingGregorianUnits( relativeToDate, 0, 0, -1, 0, 0, 0 );

		if( dateToFormat.compareTo( relativeToDate ) >= 0 ) {
			StringBuilder b = new StringBuilder();
			b.append( "Í gær" );

			if( showTime ) {
				b.append( " kl. " );
				b.append( timeFormat().format( dateToFormat ) );
			}

			return b.toString();
		}

		relativeToDate = DateUtilities.dateByAddingGregorianUnits( relativeToDate, 0, 0, -1, 0, 0, 0 );

		if( dateToFormat.compareTo( relativeToDate ) >= 0 ) {
			StringBuilder b = new StringBuilder();
			b.append( "Í fyrradag" );

			if( showTime ) {
				b.append( " kl. " );
				b.append( timeFormat().format( dateToFormat ) );
			}

			return b.toString();
		}

		return dateFormat().format( dateToFormat );
	}

	private static SimpleDateFormat dateFormat() {
		return new SimpleDateFormat( "d.MM.yyyy" );
	}

	private static SimpleDateFormat timeFormat() {
		return new SimpleDateFormat( "HH:MM" );
	}
}