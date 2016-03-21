package is.rebbi.wo.components;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXNonSynchronizingComponent;
import is.rebbi.wo.definitions.AttributeViewDefinition;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.USCRUDUtilities;

/**
 * Shows a label accompanying fields of information.
 */

public class USLabel extends ERXNonSynchronizingComponent {

	private AttributeViewDefinition _viewDefinition;

	public USLabel( WOContext context ) {
		super( context );
	}

	private Object object() {
		return valueForBinding( "object" );
	}

	public String entityName() {
		String entityName = (String)valueForBinding( "entityName" );

		if( entityName == null && object() != null ) {
			entityName = USCRUDUtilities.entityNameFromObject( object() );
		}

		return entityName;
	}

	public String key() {
		return stringValueForBinding( "key" );
	}

	public EntityViewDefinition viewDefinition() {
		return EntityViewDefinition.get( entityName() );
	}

	private AttributeViewDefinition meta() {
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