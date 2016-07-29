package is.rebbi.wo.components.relationships;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjRelationship;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCoding;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.components.USListPageEdit;
import is.rebbi.wo.definitions.EntityViewDefinition;

public class USRelationshipTargetSelection extends USListPageEdit {

	public ERXComponent callingComponent;
	public DataObject object;
	public String key;
	public boolean saveOnSelect;

	public USRelationshipTargetSelection(WOContext context) {
		super( context );
	}

	public String sourceEntityName() {
		return EntityViewDefinition.icelandicName( object.getObjectId().getEntityName() );
	}

	public String destinationEntityName() {
		return EntityViewDefinition.icelandicName( relationship().getTargetEntityName() );
	}

	public String destinationEntityNamePlural() {
		return EntityViewDefinition.icelandicNamePlural( relationship().getTargetEntityName() );
	}

	public ObjRelationship relationship() {
		return oc().getEntityResolver().getObjEntity( object.getObjectId().getEntityName() ).getRelationship( key );
	}

	@Override
	public EntityViewDefinition selectedViewDefinition() {
		return EntityViewDefinition.get( relationship().getTargetEntityName() );
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

		if( saveOnSelect ) {
			oc().commitChanges();
		}

		return callingComponent;
	}

	@Override
	protected ObjectContext oc() {
		return object.getObjectContext();
	}
}