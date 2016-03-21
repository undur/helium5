package is.rebbi.wo.components.relationships.eof;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EODatabaseDataSource;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.foundation.NSArray;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.batching.ERXBatchingDisplayGroup;
import er.extensions.components.ERXComponent;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXGenericRecord;
import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.definitions.AttributeViewDefinition;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.USEOUtilities;

public class USRelationshipTargetSelectionEOF extends USViewPage {

	public ERXDisplayGroup dg;
	public String searchString;

	public ERXComponent callingComponent;
	public ERXGenericRecord currentObject;
	public ERXGenericRecord object;
	public String key;
	public AttributeViewDefinition currentAttribute;
	public boolean saveOnSelect;

	public USRelationshipTargetSelectionEOF( WOContext context ) {
		super( context );
	}

	public String sourceEntityName() {
		return EntityViewDefinition.icelandicName( object.entityName() );
	}

	public String destinationEntityName() {
		return EntityViewDefinition.icelandicName( relationship().destinationEntity().name() );
	}

	public String destinationEntityNamePlural() {
		return EntityViewDefinition.icelandicNamePlural( relationship().destinationEntity().name() );
	}

	public EORelationship relationship() {
		return object.entity().relationshipNamed( key );
	}

	public NSArray<ERXGenericRecord> objects() {
		return ERXEOControlUtilities.objectsWithQualifier( object.editingContext(), relationship().destinationEntity().name(), null, null, false );
	}

	public WOActionResults cancel() {
		callingComponent.ensureAwakeInContext( context() );
		return callingComponent;
	}

	public WOActionResults selectObject() {
		Object previousObject = object.valueForKey( key );

		if( !relationship().isToMany() && previousObject != null ) {
			object.removeObjectFromBothSidesOfRelationshipWithKey( (ERXGenericRecord)previousObject, key );
		}

		object.addObjectToBothSidesOfRelationshipWithKey( currentObject, key );

		if( saveOnSelect ) {
			ec().saveChanges();
		}

		return callingComponent;
	}

	public void resetDG() {
		dg = new ERXBatchingDisplayGroup();
		dg.setDataSource( new EODatabaseDataSource( object.editingContext(), viewDefinition().entity().name() ) );
		dg.setNumberOfObjectsPerBatch( 100 );
		dg.setSortOrderings( viewDefinition().defaultSortOrderings() );

		if( searchString != null ) {
			dg.setQualifier( USEOUtilities.allQualifier( searchString, viewDefinition().entity() ) );
		}

		dg.qualifyDataSource();
	}

	public Object currentValue() {
		return currentObject.valueForKeyPath( currentAttribute.name() );
	}

	@Override
	public EntityViewDefinition viewDefinition() {
		return EntityViewDefinition.get( relationship().destinationEntity().name() );
	}
}