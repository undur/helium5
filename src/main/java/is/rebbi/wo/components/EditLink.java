package is.rebbi.wo.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.wo.util.Inspection;

/**
 * Link to edit objects.
 */

public class EditLink extends ERXStatelessComponent {

	public EditLink( WOContext context ) {
		super( context );
	}

	/**
	 * Disable the link if [object] is null.
	 */
	public boolean disabled() {
		return object() == null || booleanValueForBinding( "disabled" );
	}

	/**
	 * @return The value of the "object"-binding.
	 */
	public Object object() {
		return valueForBinding( "object" );
	}

	/**
	 * @return An edit page for [object]
	 */
	public WOActionResults selectObject() {
		return Inspection.editObjectInContext( object(), context() );
	}
}