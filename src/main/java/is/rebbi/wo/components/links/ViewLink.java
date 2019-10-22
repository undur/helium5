package is.rebbi.wo.components.links;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.wo.urls.USURLProvider;

/**
 * Link to view objects.
 */

public class ViewLink extends ERXStatelessComponent {

	public ViewLink( WOContext context ) {
		super( context );
	}

	/**
	 * Disable the link if the object is null.
	 */
	public boolean disabled() {

		if( isTemporary() ) {
			return true;
		}

		return object() == null || booleanValueForBinding( "disabled" );
	}

	private boolean isTemporary() {
		return object() instanceof DataObject && ((DataObject)object()).getObjectId().isTemporary();
	}

	/**
	 * @return The value of the "object"-binding.
	 */
	public Object object() {
		return valueForBinding( "object" );
	}

	/**
	 * @return The URL for the link.
	 */
	public String href() {

		if( isTemporary() ) {
			return null;
		}

		return USURLProvider.urlForObjectInContext( object(), context() );
	}

	/**
	 * @return The operation to link to. If no operation is specified, the default is the "view" operation.
	 */
	public String operation() {
		return stringValueForBinding( "operation" );
	}

	public boolean showPlaceholder() {
		if( disabled() && valueForBinding( "class" ) != null ) {
			return true;
		}

		return false;
	}

	public String disabledClass() {
		return valueForBinding( "class" ) + " disabled";
	}
}