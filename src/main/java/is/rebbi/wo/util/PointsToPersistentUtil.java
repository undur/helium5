package is.rebbi.wo.util;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXEnterpriseObject;
import er.extensions.eof.ERXGenericRecord;
import er.extensions.eof.ERXKey;
import is.rebbi.core.search.PointsToPersistent;
import is.rebbi.wo.interfaces.HasFakeRelationship;

public class PointsToPersistentUtil {

	private static final ERXKey<String> TARGET_ENTITY_NAME = new ERXKey<>( "targetEntityName" );
	private static final ERXKey<String> TARGET_ID = new ERXKey<>( "targetID" );

	/**
	 * @return The target object of the given fake relationship container object.
	 */
	public static ERXGenericRecord targetObject( EOEditingContext ec, PointsToPersistent object ) {

		if( object == null ) {
			return null;
		}

		String entityName = object.targetEntityName();
		String id = object.targetID();
		return targetObject( ec, entityName, id );
	}

	/**
	 * @return true if the target object exists.
	 */
	public static boolean targetObjectExists( EOEditingContext ec, PointsToPersistent object ) {
		Integer count = ERXEOControlUtilities.objectCountWithQualifier( ec, object.targetEntityName(), TARGET_ID.eq( object.targetID() ) );
		return count != null && count > 0;
	}

	/**
	 * @return The target object of the given fake relationship container object.
	 */
	public static ERXGenericRecord targetObject( EOEditingContext ec, String entityName, String idString ) {
		return PKSerializerEOF.eo( ec, entityName, idString );
	}

	/**
	 * @param entityClass Class of object to create.
	 * @param targetObject The object to target with the relationship.
	 * @return A new HasFakeRelationship targeting targetObject.
	 */
	public static <E extends HasFakeRelationship> E create( Class<E> entityClass, ERXEnterpriseObject targetObject ) {
		E object = ERXEOControlUtilities.createAndInsertObject( targetObject.editingContext(), entityClass );
		setTargetObject( object, targetObject );
		return object;
	}

	/**
	 * @param link The link to change.
	 * @param targetObject The new object to target.
	 */
	public static <E extends HasFakeRelationship> void setTargetObject( E link, ERXEnterpriseObject targetObject ) {
		link.setTargetEntityName( targetObject.entityName() );
		link.setTargetID( PKSerializerEOF.serialize( targetObject ) );
	}

	public static int relatedObjectCount( EOEditingContext ec, Class entityClass, ERXEnterpriseObject targetObject ) {

		String entityName = entityClass.getSimpleName();

		NSMutableArray<EOQualifier> a = new NSMutableArray<>();
		a.addObject( TARGET_ENTITY_NAME.eq( targetObject.entityName() ) );
		a.addObject( TARGET_ID.eq( PKSerializerEOF.serialize( targetObject ) ) );
		EOQualifier q = new EOAndQualifier( a );

		return ERXEOControlUtilities.objectCountWithQualifier( ec, entityName, q );
	}

	public static <E extends PointsToPersistent> NSArray<E> relatedObjects( Class<E> entityClass, ERXEnterpriseObject targetObject, NSArray<EOSortOrdering> sortOrderings ) {

		String entityName = entityClass.getSimpleName();

		if( targetObject == null || targetObject.primaryKey() == null ) {
			return NSArray.emptyArray();
		}

		NSMutableArray<EOQualifier> a = new NSMutableArray<>();
		a.addObject( TARGET_ENTITY_NAME.eq( targetObject.entityName() ) );
		a.addObject( TARGET_ID.eq( PKSerializerEOF.serialize( targetObject ) ) );
		EOQualifier q = new EOAndQualifier( a );

		NSArray<E> objects = ERXEOControlUtilities.objectsWithQualifier( targetObject.editingContext(), entityName, q, NSArray.emptyArray(), true );

		if( USArrayUtilities.hasObjects( sortOrderings ) ) {
			objects = EOSortOrdering.sortedArrayUsingKeyOrderArray( objects, sortOrderings );
		}

		return objects;
	}
}