package is.rebbi.wo.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;

import is.rebbi.core.search.PointsToPersistent;
import is.rebbi.core.util.ListUtilities;
import is.rebbi.wo.util.PKSerializer;
import is.rebbi.wo.util.PointsToPersistentUtil;

/**
 * Implements a fake relationship to a table, based on an object ID and an entity name.
 */

public interface HasFakeRelationship extends DataObject, PointsToPersistent {

	public static class Util {

		private static final Property<String> TARGET_ENTITY_NAME = new Property<>( "targetEntityName" );
		private static final Property<String> TARGET_ID = new Property<>( "targetID" );

		/**
		 * @return The target object of the given fake relationship container object.
		 */
		public static DataObject targetObject( HasFakeRelationship object ) {
			return PointsToPersistentUtil.targetObject( object.getObjectContext(), object );
		}

		/**
		 * @return true if the target object exists.
		 */
		public static boolean targetObjectExists( HasFakeRelationship object ) {
			return PointsToPersistentUtil.targetObjectExists( object.getObjectContext(), object );
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
			E object = targetObject.getObjectContext().newObject( entityClass );
			setTargetObject( object, targetObject );
			return object;
		}

		/**
		 * @param link The link to change.
		 * @param targetObject The new object to target.
		 */
		public static <E extends HasFakeRelationship> void setTargetObject( E link, DataObject targetObject ) {
			link.setTargetEntityName( targetObject.getObjectId().getEntityName() );
			link.setTargetID( PKSerializer.serialize( targetObject.getObjectId() ) );
		}

		public static int relatedObjectCount( Class entityClass, DataObject targetObject ) {
			return PointsToPersistentUtil.relatedObjectCount( targetObject.getObjectContext(), entityClass, targetObject );
		}

		public static <E extends HasFakeRelationship> List<E> relatedObjects( Class<E> entityClass, DataObject targetObject, List<Ordering> sortOrderings ) {

			if( targetObject == null ) {
				return new ArrayList<>();
			}

			List<Expression> a = new ArrayList<>();
			a.add( TARGET_ENTITY_NAME.eq( targetObject.getObjectId().getEntityName() ) );
			a.add( TARGET_ID.eq( PKSerializer.serialize( targetObject.getObjectId() ) ) );
			Expression q = ExpressionFactory.and( a );

			SelectQuery<E> query = new SelectQuery<>( entityClass );
			query.setQualifier( q );
			List<E> objects = targetObject.getObjectContext().select( query );

			if( ListUtilities.hasObjects( sortOrderings ) ) {
				Ordering.orderList( objects, sortOrderings );
			}

			return objects;
		}
	}
}