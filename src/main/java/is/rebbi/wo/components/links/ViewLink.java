package is.rebbi.wo.components.links;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.Persistent;

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

		if( object() == null ) {
			return true;
		}

		// We can't generate URLs for objects that haven't been committed to the DB, so we disable the link
		if( isNewPersistentObject() ) {
			return true;
		}

		return booleanValueForBinding( "disabled" );
	}

	private boolean isNewPersistentObject() {
		return object() instanceof Persistent && ((Persistent)object()).getObjectId().isTemporary();
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

		if( isNewPersistentObject() ) {
			return null;
		}

		return USURLProvider.urlForObjectInContext( object(), context() );
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