package is.rebbi.wo.components.fields;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.util.USUtilities;

/**
 * For entering an email address in a form
 */

public class USEmailAddressField extends USField {

	public USEmailAddressField( WOContext context ) {
		super( context );
	}

	@Override
	public String cleanupValue( String s ) {
		if( s != null ) {
			s = s.trim();
		}

		return s;
	}

	@Override
	public String errorString() {

		if( invalidValue() ) {
			return "ógilt netfang";
		}

		return null;
	}

	@Override
	public boolean isValid() {
		return USUtilities.validateEmailAddress( value() );
	}
}