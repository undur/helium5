package is.rebbi.wo.components.relationships.cayenne;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.components.USBaseComponent;
import is.rebbi.wo.util.HumanReadableUtils;
import is.rebbi.wo.util.Inspection;

/**
 * Inspects a to-one relationship, allowing editing, addition and removal of objects.
 */

public class USToOneRelationshipCayenne extends USBaseComponent {

	public USToOneRelationshipCayenne( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	private DataObject object() {
		return (DataObject)valueForBinding( "object" );
	}

	private String key() {
		return stringValueForBinding( "key" );
	}

	private String displayKey() {
		return stringValueForBinding( "displayKey" );
	}

	private ObjRelationship relationship() {
		ObjEntity objEntity = object().getObjectContext().getEntityResolver().getObjEntity( object().getClass() );
		return objEntity.getRelationship( key() );
	}

	public DataObject destinationObject() {
		return (DataObject)NSKeyValueCoding.Utility.valueForKey( object(), key() );
	}

	public WOActionResults createObject() {
		DataContext dc = (DataContext)object().getObjectContext();
		String destinationEntityName = relationship().getTargetEntityName();
		Persistent newObject = dc.newObject( destinationEntityName );
		NSKeyValueCoding.Utility.takeValueForKey( object(), newObject, relationship().getName() );
		dc.commitChanges();
		return Inspection.editObjectInContext( newObject, context() );
	}

	public WOActionResults removeObject() {
		NSKeyValueCoding.Utility.takeValueForKey( object(), null, relationship().getName() );
		//		object().removeObjectFromBothSidesOfRelationshipWithKey( destinationObject(), key() ); // Make sure this works.
		return null;
	}

	public Object displayString() {
		Object displayString = null;

		if( displayKey() == null ) {
			displayString = HumanReadableUtils.toStringHuman( destinationObject() );
		}
		else {
			displayString = NSKeyValueCodingAdditions.Utility.valueForKeyPath( destinationObject(), displayKey() );
		}

		return displayString;
	}

	public WOActionResults selectObject() {
		USRelationshipTargetSelectionCayenne nextPage = pageWithName( USRelationshipTargetSelectionCayenne.class );
		nextPage.object = object();
		nextPage.key = key();
		nextPage.callingComponent = (ERXComponent)context().page();
		//		nextPage.resetDG();
		return nextPage;
	}
}