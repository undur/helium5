package is.rebbi.wo.formatters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class FileSizeFormatter extends Format {

	private static final BigDecimal THOUSAND = new BigDecimal( 1000 );

	@Override
	public StringBuffer format( Object obj, StringBuffer toAppendTo, FieldPosition pos ) {

		if( obj != null ) {
			BigDecimal bd = (BigDecimal)obj;
			toAppendTo.append( format( bd ) );
		}

		return toAppendTo;
	}

	private String format( BigDecimal l ) {
		if( l.doubleValue() < 1000 ) {
			return l + " B";
		}

		l = l.divide( THOUSAND, RoundingMode.HALF_UP );

		if( l.doubleValue() < 1000 ) {
			return l + " KB";
		}

		l = l.divide( THOUSAND, RoundingMode.HALF_UP );

		if( l.doubleValue() < 1000 ) {
			return l + " MB";
		}

		l = l.divide( THOUSAND, RoundingMode.HALF_UP );

		return l + " GB";
	}

	@Override
	public Object parseObject( String source, ParsePosition pos ) {
		throw new RuntimeException( "Parsing is not supported" );
	}
}