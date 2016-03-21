package is.rebbi.wo.components.fields;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.ajax.AjaxUtils;
import er.extensions.components.ERXComponent;
import er.extensions.foundation.ERXStringUtilities;
import is.rebbi.core.util.StringUtilities;

/**
 * A common superclass for form fields.
 */

public abstract class USField extends ERXComponent {

	private String _fieldID;
	private String _updateContainerID;

	/**
	 * Indicates that this field has gone through the validation stage at least once.
	 */
	public boolean hasValidated;

	public USField(WOContext context) {
		super( context );
	}

	public abstract String errorString();

	public abstract String cleanupValue( String o );

	public abstract boolean isValid();

	public boolean hasValidated() {
		boolean result = hasValidated || hasValue();
		return result;
	}

	/**
	 * @return true if the field has a value
	 */
	public boolean hasValue() {
		return StringUtilities.hasValue( value() );
	}

	@Override
	public void appendToResponse( WOResponse response, WOContext context ) {
		super.appendToResponse( response, context );
		AjaxUtils.addStylesheetResourceInHead( context, response, "helium", "USField.css" );
	}

	/**
	 * @return True if a search was performed and a corresponding person was found.
	 */
	public boolean invalidValue() {
		return !isValid();
	}

	/**
	 * The field's value.
	 */
	public String value() {
		return cleanupValue( stringValueForBinding( "value" ) );
	}

	/**
	 * Sets the field's value.
	 */
	public void setValue( String newValue ) {
		setValueForBinding( cleanupValue( newValue ), "value" );
	}

	/**
	 * The field's id binding.
	 */
	private String id() {
		return (String)valueForBinding( "id" );
	}

	/**
	 * @return A uniqueID for this field.
	 */
	public String fieldID() {

		if( !ERXStringUtilities.stringIsNullOrEmpty( id() ) ) {
			_fieldID = id();
		}

		if( _fieldID == null ) {
			String safeElementID = StringUtilities.replace( context().elementID(), ".", "_" );
			_fieldID = "field_" + safeElementID;
		}

		return _fieldID;
	}

	/**
	 * @return a unique id for the update container.
	 */
	public String updateContainerID() {

		if( !ERXStringUtilities.stringIsNullOrEmpty( id() ) ) {
			_updateContainerID = "up_container_" + id();
		}

		if( _updateContainerID == null ) {
			String safeElementID = StringUtilities.replace( context().elementID(), ".", "_" );
			_updateContainerID = "up_" + safeElementID;
		}

		return _updateContainerID;
	}

	public WOActionResults validateField() {
		hasValidated = true;
		return null;
	}

	public String fieldClass() {
		return (String)valueForBinding( "class" );
	}
}