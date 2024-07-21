package is.rebbi.wo.components.fields;

import java.time.LocalTime;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.components.USBaseComponent;

/**
 * A field for entering time
 */

public class USLocalTimeField extends USBaseComponent {

	public USLocalTimeField( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	public String stringValue() {

		if( hasBinding( "stringValue" ) ) {
			return stringValueForBinding( "stringValue" );
		}

		final LocalTime value = (LocalTime)valueForBinding( "value" );

		if( value == null ) {
			return null;
		}

		return value.toString();
	}

	public void setStringValue( String stringValue ) {

		if( hasBinding( "stringValue" ) ) {
			setValueForBinding( stringValue, "stringValue" );
		}
		else {
			LocalTime localTime;

			if( stringValue != null ) {
				localTime = LocalTime.parse( stringValue );
			}
			else {
				localTime = null;
			}

			setValueForBinding( localTime, "value" );
		}
	}
}