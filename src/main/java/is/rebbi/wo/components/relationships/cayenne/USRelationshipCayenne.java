package is.rebbi.wo.components.relationships.cayenne;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.map.ObjRelationship;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXNonSynchronizingComponent;

public class USRelationshipCayenne extends ERXNonSynchronizingComponent {

	public USRelationshipCayenne( WOContext context ) {
		super( context );
	}

	public DataObject object() {
		return (DataObject)valueForBinding( "object" );
	}

	public String key() {
		return stringValueForBinding( "key" );
	}

	public Boolean disableObjectSelection() {
		return (Boolean)valueForBinding( "disableObjectSelection" );
	}

	public ObjRelationship relationship() {
		return object().getObjectContext().getEntityResolver().getObjEntity( object().getClass() ).getRelationship( key() );
	}
}