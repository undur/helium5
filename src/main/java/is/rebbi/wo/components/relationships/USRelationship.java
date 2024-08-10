package is.rebbi.wo.components.relationships;

import org.apache.cayenne.PersistentObject;
import org.apache.cayenne.map.ObjRelationship;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXNonSynchronizingComponent;

public class USRelationship extends ERXNonSynchronizingComponent {

	public USRelationship( WOContext context ) {
		super( context );
	}

	public PersistentObject object() {
		return (PersistentObject)valueForBinding( "object" );
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