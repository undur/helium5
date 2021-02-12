package is.rebbi.wo.components.admin;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXNonSynchronizingComponent;
import is.rebbi.wo.definitions.AttributeDefinition;
import is.rebbi.wo.definitions.EntityViewDefinition;

/**
 * Shows a label accompanying fields of information.
 */

public class USLabel extends ERXNonSynchronizingComponent {

	private AttributeDefinition _viewDefinition;

	public USLabel( WOContext context ) {
		super( context );
	}

	private DataObject object() {
		return (DataObject)valueForBinding( "object" );
	}

	private String key() {
		return stringValueForBinding( "key" );
	}

	private EntityViewDefinition viewDefinition() {
		return EntityViewDefinition.get( object().getClass() );
	}

	private AttributeDefinition meta() {
		if( _viewDefinition == null ) {
			_viewDefinition = viewDefinition().attributeNamed( key() );
		}

		return _viewDefinition;
	}

	public String displayName() {
		String result = null;

		if( meta() != null ) {
			return meta().icelandicName();
		}

		if( result == null ) {
			result = key();
		}

		return result;
	}

	public String text() {
		if( meta() != null ) {
			return meta().text();
		}

		return null;
	}
}