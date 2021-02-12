package is.rebbi.wo.components.relationships;

import java.util.List;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjRelationship;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCoding;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.components.admin.USListPageEdit;
import jambalaya.definitions.EntityDefinition;

public class USRelationshipTargetSelection extends USListPageEdit {

	public ERXComponent callingComponent;
	public DataObject object;
	public String key;

	public USRelationshipTargetSelection( WOContext context ) {
		super( context );
	}

	public String sourceEntityName() {
		return EntityDefinition.icelandicName( object.getObjectId().getEntityName() );
	}

	public String destinationEntityName() {
		return EntityDefinition.icelandicName( relationship().getTargetEntityName() );
	}

	public String destinationEntityNamePlural() {
		return EntityDefinition.icelandicNamePlural( relationship().getTargetEntityName() );
	}

	public ObjRelationship relationship() {
		return oc().getEntityResolver().getObjEntity( object.getObjectId().getEntityName() ).getRelationship( key );
	}

	/**
	 * FIXME: Should this definitely be overridden?
	 */
	@Override
	public EntityDefinition entityDefinition() {
		return EntityDefinition.get( relationship().getTargetEntityName() );
	}

	public WOActionResults cancel() {
		callingComponent.ensureAwakeInContext( context() );
		return callingComponent;
	}

	public WOActionResults selectObject() {

		if( relationship().isToMany() ) {
			object.addToManyTarget( key, currentObject, true );
		}
		else {
			NSKeyValueCoding.Utility.takeValueForKey( object, currentObject, key );
		}

		return callingComponent;
	}

	@Override
	protected ObjectContext oc() {
		return object.getObjectContext();
	}

	private Object currentRelationshipValue() {
		return NSKeyValueCoding.Utility.valueForKey( object, key );
	}

	public boolean currentChecked() {
		if( relationship().isToMany() ) {
			return ((List)currentRelationshipValue()).contains( currentObject );
		}

		return currentRelationshipValue().equals( currentObject );
	}

	public void setCurrentChecked( boolean isChecked ) {
		if( relationship().isToMany() ) {
			if( isChecked ) {
				if( !currentChecked() ) {
					object.addToManyTarget( key, currentObject, true );
				}
			}
			else {
				if( currentChecked() ) {
					object.removeToManyTarget( key, currentObject, true );
				}
			}
		}
	}

	public WOActionResults linkSelected() {
		return callingComponent;
	}
}