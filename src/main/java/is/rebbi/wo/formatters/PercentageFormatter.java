package is.rebbi.wo.formatters;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

public class PercentageFormatter extends Format {

	private static final Locale LOCALE = new Locale( "is" );
	private boolean addPercentage = false;
	private boolean renderZeroAsEmpty = false;
	private int _numberOfDecimals = 2;

	private static void replaceString( StringBuffer sb, String toReplace, String replacement ) {
		int index = -1;
		while( (index = sb.lastIndexOf( toReplace )) != -1 ) {
			sb.replace( index, index + toReplace.length(), replacement );
		}
	}

	@Override
	public StringBuffer format( Object obj, StringBuffer toAppendTo, FieldPosition pos ) {
		if( obj != null ) {
			Double number = ((Number)obj).doubleValue();

			if( !(renderZeroAsEmpty && number == 0) ) {
				NumberFormat format = NumberFormat.getPercentInstance( LOCALE );
				format.setMaximumFractionDigits( _numberOfDecimals );
				format.format( obj, toAppendTo, pos );

				if( !addPercentage ) {
					replaceString( toAppendTo, "%", "" );
				}
			}
		}

		return toAppendTo;
	}

	public void setAddPercentage( boolean value ) {
		addPercentage = value;
	}

	public void setRenderZeroAsEmpty( boolean value ) {
		renderZeroAsEmpty = value;
	}

	public void setNumberOfDecimals( int value ) {
		_numberOfDecimals = value;
	}

	@Override
	public Object parseObject( String source, ParsePosition status ) {
		try {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols( LOCALE );
			DecimalFormat f = new DecimalFormat( "##.###", symbols );
			source = source.substring( status.getIndex() );
			status.setIndex( source.length() );
			Number number = (Number)f.parseObject( source );

			if( !(number instanceof BigDecimal) ) {
				number = new BigDecimal( number.toString() );
			}

			return ((BigDecimal)number).divide( new BigDecimal( 100 ) );
		}
		catch( ParseException e ) {
			throw new RuntimeException( e );
		}
	}
}