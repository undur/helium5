package is.rebbi.wo.components.admin;

import org.apache.cayenne.PersistentObject;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXNonSynchronizingComponent;
import jambalaya.definitions.AttributeDefinition;
import jambalaya.definitions.EntityDefinition;

/**
 * Shows a label accompanying fields of information.
 */

public class USLabel extends ERXNonSynchronizingComponent {

	private AttributeDefinition _viewDefinition;

	public USLabel( WOContext context ) {
		super( context );
	}

	private PersistentObject object() {
		return (PersistentObject)valueForBinding( "object" );
	}

	private String key() {
		return stringValueForBinding( "key" );
	}

	private EntityDefinition viewDefinition() {
		return EntityDefinition.get( object().getClass() );
	}

	private AttributeDefinition meta() {
		if( _viewDefinition == null ) {
			_viewDefinition = viewDefinition().attributeNamed( key() );
		}

		return _viewDefinition;
	}

	public String displayName() {
		if( meta() != null ) {
			final String icelandicName = meta().icelandicName();

			if( icelandicName != null ) {
				return icelandicName;
			}
		}

		return key();
	}

	public String text() {
		if( meta() != null ) {
			return meta().text();
		}

		return null;
	}
}