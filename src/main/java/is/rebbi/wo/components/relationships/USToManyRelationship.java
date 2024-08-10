package is.rebbi.wo.components.relationships;

import java.util.List;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.PersistentObject;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.map.ObjRelationship;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.components.ERXComponent;
import is.rebbi.core.humanreadable.HumanReadableUtils;
import is.rebbi.wo.components.USBaseComponent;
import is.rebbi.wo.util.Inspection;

/**
 * Inspects a to-many relationship, allowing editing, addition and removal of objects.
 */

public class USToManyRelationship extends USBaseComponent {

	public PersistentObject currentObject;

	public USToManyRelationship( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	private PersistentObject object() {
		return (PersistentObject)valueForBinding( "object" );
	}

	private String key() {
		return stringValueForBinding( "key" );
	}

	private String displayKey() {
		return stringValueForBinding( "displayKey" );
	}

	private ObjRelationship relationship() {
		return object().getObjectContext().getEntityResolver().getObjEntity( object().getClass() ).getRelationship( key() );
	}

	public List<PersistentObject> destinationObjects() {
		return (List<PersistentObject>)NSKeyValueCoding.Utility.valueForKey( object(), key() );
	}

	public WOActionResults createObject() {
		DataContext dc = (DataContext)object().getObjectContext();
		String destinationEntityName = relationship().getTargetEntityName();
		Persistent newObject = dc.newObject( destinationEntityName );
		object().addToManyTarget( relationship().getName(), newObject, true );
		return Inspection.editObjectInContext( newObject, context() );
	}

	public WOActionResults removeObject() {
		object().removeToManyTarget( relationship().getName(), currentObject, true );
		return context().page();
	}

	public Object displayString() {
		Object displayString = null;

		if( displayKey() == null ) {
			displayString = HumanReadableUtils.toStringHuman( currentObject );
		}
		else {
			displayString = NSKeyValueCodingAdditions.Utility.valueForKeyPath( currentObject, displayKey() );
		}

		return displayString;
	}

	public WOActionResults selectObject() {
		USRelationshipTargetSelection nextPage = pageWithName( USRelationshipTargetSelection.class );
		nextPage.object = object();
		nextPage.key = key();
		nextPage.callingComponent = (ERXComponent)context().page();
		// nextPage.resetDG();
		return nextPage;
	}
}