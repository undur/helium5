package is.rebbi.wo.components.value;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.core.humanreadable.HumanReadableUtils;
import is.rebbi.core.util.StringUtilities;

/**
 * String component with some added sugar.
 */

public class IMString extends ERXStatelessComponent {

	public IMString( WOContext context ) {
		super( context );
	}

	private int maxLength() {
		return intValueForBinding( "maxLength", -1 ); // FIXME: I kind of don't like this minus one value
	}

	/**
	 * Default formats for temporal values when no dateTimeFormatter binding is given. Readable ISO-style, no "T" and no fractional seconds.
	 */
	private static final DateTimeFormatter DEFAULT_LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );
	private static final DateTimeFormatter DEFAULT_LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );
	private static final DateTimeFormatter DEFAULT_LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern( "HH:mm:ss" );

	private DateTimeFormatter dateTimeFormatter() {
		return (DateTimeFormatter)valueForBinding( "dateTimeFormatter" );
	}

	private static DateTimeFormatter defaultFormatterFor( final Object value ) {
		if( value instanceof LocalDateTime ) {
			return DEFAULT_LOCAL_DATE_TIME_FORMATTER;
		}

		if( value instanceof LocalDate ) {
			return DEFAULT_LOCAL_DATE_FORMATTER;
		}

		if( value instanceof LocalTime ) {
			return DEFAULT_LOCAL_TIME_FORMATTER;
		}

		return null;
	}

	public String valueWhenEmpty() {
		return stringValueForBinding( "valueWhenEmpty" );
	}

	public String value() {
		Object value = valueForBinding( "value" );

		if( value == null ) {
			return "";
		}

		if( value instanceof BigDecimal ) {
			value = NumberFormat.getInstance( Locale.of( "is" ) ).format( value );
		}

		if( value instanceof TemporalAccessor ) {
			DateTimeFormatter formatter = dateTimeFormatter();

			if( formatter == null ) {
				formatter = defaultFormatterFor( value );
			}

			if( formatter != null ) {
				value = formatter.format( (TemporalAccessor)value );
			}
		}

		if( Boolean.class.isAssignableFrom( value.getClass() ) ) {
			if( (boolean)value == true ) {
				value = "Já";
			}
			else {
				value = "";
			}
		}

		if( !(value instanceof String) ) {
			value = HumanReadableUtils.toStringHuman( value );
		}

		if( maxLength() != -1 ) {
			final String abbreviationPostfix = stringValueForBinding( "abbreviationPostfix" );
			value = StringUtilities.abbreviate( value.toString(), maxLength(), abbreviationPostfix );
		}

		return (String)value;
	}

	public boolean hasValue() {

		if( value() != null ) {
			if( value() instanceof String ) {
				return !value().isEmpty();
			}

			return true;
		}

		return false;
	}
}