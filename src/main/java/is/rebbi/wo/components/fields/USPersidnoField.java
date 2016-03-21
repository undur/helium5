package is.rebbi.wo.components.fields;

import com.webobjects.appserver.WOContext;

import is.rebbi.core.util.PersidnoUtilities;

/**
 * For entering a "persidno" in a form
 */

public class USPersidnoField extends USField {

	public USPersidnoField( WOContext context ) {
		super( context );
	}

	@Override
	public String cleanupValue( String s ) {
		return PersidnoUtilities.cleanupPersidno( s );
	}

	@Override
	public String errorString() {

		if( invalidValue() ) {
			return "ógild kennitala";
		}

		return null;
	}

	@Override
	public boolean isValid() {
		return PersidnoUtilities.validatePersidno( value() );
	}
}