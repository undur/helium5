package is.rebbi.wo.components.links;

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
		return object() == null || booleanValueForBinding( "disabled" );
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
		return USURLProvider.urlForObjectInContext( object(), context() );
	}

	/**
	 * @return The operation to link to. If no operation is specified, the default is the "view" operation.
	 */
	public String operation() {
		return stringValueForBinding( "operation" );
	}
}