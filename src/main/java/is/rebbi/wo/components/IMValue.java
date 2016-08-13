package is.rebbi.wo.components;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.core.humanreadable.HumanReadableUtils;

/**
 * Display any object as a human readable string in a component.
 */

public class IMValue extends ERXStatelessComponent {

	public IMValue(WOContext context) {
		super( context );
	}

	public Object object() {
		return valueForBinding( "object" );
	}

	public String keyPath() {
		return (String)valueForBinding( "keyPath" );
	}

	public boolean isArray() {
		return object() instanceof List;
	}

	public boolean disabled() {
		if( booleanValueForBinding( "disabled" ) ) {
			return true;
		}

		if( !(object() instanceof DataObject) ) {
			return true;
		}

		return false;
	}

	private Object valueUnformatted() {

		if( keyPath() != null ) {
			return NSKeyValueCodingAdditions.Utility.valueForKeyPath( object(), keyPath() );
		}

		return object();
	}

	public Object value() {

		Object valueUnformatted = valueUnformatted();

		if( valueUnformatted == null ) {
			return "";
		}

		if( valueUnformatted instanceof BigDecimal ) {
			return NumberFormat.getInstance( new Locale( "is" ) ).format( valueUnformatted );
		}

		return HumanReadableUtils.toStringHuman( valueUnformatted );
	}
}