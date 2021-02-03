package is.rebbi.wo.components.value;

import java.math.BigDecimal;
import java.text.NumberFormat;
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

    private DateTimeFormatter dateTimeFormatter() {
        return (DateTimeFormatter)valueForBinding( "dateTimeFormatter" );
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
            value = NumberFormat.getInstance( new Locale( "is" ) ).format( value );
        }

        if( value instanceof TemporalAccessor ) {
            if( dateTimeFormatter() != null ) {
                value = dateTimeFormatter().format( (TemporalAccessor)value );
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