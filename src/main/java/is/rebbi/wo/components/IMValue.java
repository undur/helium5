package is.rebbi.wo.components;

import java.util.List;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.components.ERXStatelessComponent;

/**
 * Display any object as a human readable string in a component.
 */

public class IMValue extends ERXStatelessComponent {

	public IMValue(WOContext context) {
		super( context );
	}

	public Object object() {
	    if( hasBinding( "object" ) ) {
	        return valueForBinding( "object" );
	    }

	    if( hasBinding( "value" ) ) {
	        return valueForBinding( "value" );
	    }
	    
	    return null;
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

	public Object valueForDisplay() {
	    if( keyPath() != null ) {
			return NSKeyValueCodingAdditions.Utility.valueForKeyPath( object(), keyPath() );
		}

		return object();
	}
}