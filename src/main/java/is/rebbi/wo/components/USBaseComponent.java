package is.rebbi.wo.components;

import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXWOContext;
import er.extensions.components.ERXComponent;
import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.util.USSettings;

public abstract class USBaseComponent extends ERXComponent {

	/**
	 * A unique ID suitable for use in client side operations.
	 */
	private String _uniqueID;

	public USBaseComponent( WOContext context ) {
		super( context );
	}

	/**
	 * @return value of the optional "id" binding for components that generate a uniqueID.
	 */
	private String id() {
		return stringValueForBinding( "id" );
	}

	/**
	 * @return A unique ID that can be used for creating unique IDs for elements on a page.
	 */
	public String uniqueID() {

		if( _uniqueID == null ) {
			if( StringUtilities.hasValue( id() ) ) {
				_uniqueID = id();
			}
			else {
				_uniqueID = context().elementID();
			}

			_uniqueID = ERXWOContext.safeIdentifierName( _uniqueID, "u_" );
		}

		return _uniqueID;
	}

	/**
	 * @return Name of WOComponent to wrap around content when viewing objects.
	 */
	public String lookName() {
		String lookName = context().request().stringFormValueForKey( "look" );

		if( lookName != null ) {
			return lookName;
		}

		lookName = USSettings.defaultLookName();

		if( lookName == null ) {
			lookName = USStandardLook.class.getSimpleName();
		}

		return lookName;
	}

	/**
	 * @return Name of WOComponent to wrap around content when editing objects.
	 */
	public String editLookName() {
		String lookName = context().request().stringFormValueForKey( "look" );

		if( lookName != null ) {
			return lookName;
		}

		lookName = USSettings.defaultEditLookName();

		if( lookName == null ) {
			lookName = USStandardLook.class.getSimpleName();
		}

		return lookName;
	}
}