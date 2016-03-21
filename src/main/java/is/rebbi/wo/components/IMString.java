package is.rebbi.wo.components;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.util.USUtilities;

/**
 * String component with some added sugar.
 */

public class IMString extends ERXStatelessComponent {

	public IMString( WOContext context ) {
		super( context );
	}

	@Override
	protected boolean useDefaultComponentCSS() {
		return true;
	}

	private Integer maxLength() {
		return USUtilities.integerFromObject( valueForBinding( "maxLength" ) );
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
			return NumberFormat.getInstance( new Locale( "is" ) ).format( value );
		}

		if( !(value instanceof String) ) {
			value = value.toString();
		}

		if( maxLength() != null ) {
			value = StringUtilities.abbreviate( value.toString(), maxLength() );
		}

		return (String)value;
	}

	public boolean hasValue() {

		if( value() == null ) {
			return false;
		}

		if( value() instanceof String ) {
			return !value().isEmpty();
		}

		return false;
	}
}