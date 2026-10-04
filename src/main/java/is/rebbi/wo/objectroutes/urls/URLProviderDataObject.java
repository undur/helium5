package is.rebbi.wo.objectroutes.urls;

import java.util.Map;

import java.util.UUID;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.PersistentObject;

import is.rebbi.wo.objectroutes.Inspection.InspectionRoute;
import is.rebbi.wo.objectroutes.ObjectRoutes;
import jambalaya.ObjectIdSerializer;
import jambalaya.interfaces.UUIDStamped;
import jambalaya.interfaces.UniqueIDStamped;

public class URLProviderDataObject implements URLProvider<PersistentObject> {

	public static final String PK_IDENTIFIER_PREFIX = "id-";

	public static final String UNIQUE_ID_IDENTIFIER_PREFIX = "uid-";

	@Override
	public String urlForObject( final PersistentObject object ) {

		if( object instanceof UniqueIDStamped obj ) {
			final String uniqueID = obj.uniqueID();

			if( uniqueID != null ) {
				return urlForUniqueID( object.getObjectId().getEntityName(), uniqueID );
			}
		}

		if( object instanceof UUIDStamped obj ) {
			final UUID uniqueID = obj.uniqueID();

			if( uniqueID != null ) {
				return urlForUniqueID( object.getObjectId().getEntityName(), uniqueID.toString() );
			}
		}

		return urlForObjectId( object.getObjectId() );
	}

	public static String urlForObjectId( final ObjectId objectId ) {
		final InspectionRoute inspectionRoute = InspectionRoute.forEntityName( objectId.getEntityName() );

		if( inspectionRoute == null ) {
			return "no-route";
		}

		final String typeIdentifierString = inspectionRoute.urlPrefix();
		final String objectIdentifierString = PK_IDENTIFIER_PREFIX + ObjectIdSerializer.serialize( objectId );
		return fullURL( typeIdentifierString, objectIdentifierString );
	}

	public static String urlForUniqueID( final String entityName, final String uniqueID ) {
		final InspectionRoute inspectionRoute = InspectionRoute.forEntityName( entityName );

		if( inspectionRoute == null ) {
			return "no-route";
		}

		final String typeIdentifierString = inspectionRoute.urlPrefix();
		final String objectIdentifierString = UNIQUE_ID_IDENTIFIER_PREFIX + uniqueID;
		return fullURL( typeIdentifierString, objectIdentifierString );
	}

	private static String fullURL( final String typeIdentifier, final String objectIdentifier ) {
		return ObjectRoutes.object.url( Map.of( "type", typeIdentifier, "object", objectIdentifier ) );
	}
}