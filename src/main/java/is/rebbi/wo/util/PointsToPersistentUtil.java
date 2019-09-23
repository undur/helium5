package is.rebbi.wo.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;

import is.rebbi.core.search.PointsToPersistent;
import is.rebbi.core.util.ListUtilities;
import is.rebbi.wo.interfaces.HasFakeRelationship;
import jambalaya.CayenneUtils;
import jambalaya.PKSerializer;

public class PointsToPersistentUtil {

	private static final Property<String> TARGET_ENTITY_NAME = Property.create( "targetEntityName", String.class );
	private static final Property<String> TARGET_ID = Property.create( "targetID", String.class );

	/**
	 * @return The target object of the given fake relationship container object.
	 */
	public static DataObject targetObject( ObjectContext ec, PointsToPersistent object ) {

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
	public static boolean targetObjectExists( ObjectContext oc, PointsToPersistent object ) {
		ObjEntity objEntity = oc.getEntityResolver().getObjEntity( object.targetEntityName() );
		Class<? extends DataObject> entityClass = (Class<? extends DataObject>)objEntity.getJavaClass();
		Long count = CayenneUtils.count( oc, entityClass, TARGET_ID.eq( object.targetID() ) );
		return count != null && count > 0;
	}

	/**
	 * @return The target object of the given fake relationship container object.
	 */
	public static DataObject targetObject( ObjectContext ec, String entityName, String idString ) {
		return PKSerializer.eo( ec, entityName, idString );
	}

	/**
	 * @param entityClass Class of object to create.
	 * @param targetObject The object to target with the relationship.
	 * @return A new HasFakeRelationship targeting targetObject.
	 */
	public static <E extends HasFakeRelationship> E create( Class<E> entityClass, DataObject targetObject ) {
		E newObject = targetObject.getObjectContext().newObject( entityClass );
		setTargetObject( newObject, targetObject );
		return newObject;
	}

	/**
	 * @param link The link to change.
	 * @param targetObject The new object to target.
	 */
	public static <E extends HasFakeRelationship> void setTargetObject( E link, DataObject targetObject ) {
		link.setTargetEntityName( targetObject.getObjectId().getEntityName() );
		link.setTargetID( PKSerializer.serialize( targetObject.getObjectId() ) );
	}

	public static int relatedObjectCount( ObjectContext oc, Class entityClass, DataObject targetObject ) {

		List<Expression> a = new ArrayList<>();
		a.add( TARGET_ENTITY_NAME.eq( targetObject.getObjectId().getEntityName() ) );
		a.add( TARGET_ID.eq( PKSerializer.serialize( targetObject.getObjectId() ) ) );
		Expression q = ExpressionFactory.and( a );

		return (int)CayenneUtils.count( oc, entityClass, q );
	}

	public static <E extends PointsToPersistent> List<E> relatedObjects( Class<E> entityClass, DataObject targetObject, List<Ordering> orderings ) {

		List<Expression> a = new ArrayList<>();
		a.add( TARGET_ENTITY_NAME.eq( targetObject.getObjectId().getEntityName() ) );
		a.add( TARGET_ID.eq( PKSerializer.serialize( targetObject.getObjectId() ) ) );
		Expression q = ExpressionFactory.and( a );

		SelectQuery<E> query = new SelectQuery<>( entityClass );
		query.setQualifier( q );
		List<E> objects = targetObject.getObjectContext().select( query );

		if( ListUtilities.hasObjects( orderings ) ) {
			Ordering.orderList( objects, orderings );
		}

		return objects;
	}
}