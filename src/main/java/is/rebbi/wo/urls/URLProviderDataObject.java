package is.rebbi.wo.urls;

import java.util.UUID;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.PersistentObject;

import is.rebbi.wo.util.Inspection.InspectionRoute;
import jambalaya.ObjectIdSerializer;
import jambalaya.interfaces.UUIDStamped;
import jambalaya.interfaces.UniqueIDStamped;

public class URLProviderDataObject implements URLProvider<PersistentObject> {

	public static final String PK_IDENTIFIER_PREFIX = "id-";

	public static final String UNIQUE_ID_IDENTIFIER_PREFIX = "uid-";

	@Override
	public String urlForObject( final PersistentObject object ) {

		if( object instanceof UniqueIDStamped ) {
			final String uniqueID = ((UniqueIDStamped)object).uniqueID();

			if( uniqueID != null ) {
				return urlForUniqueID( object.getObjectId().getEntityName(), uniqueID );
			}
		}

		if( object instanceof UUIDStamped ) {
			final UUID uniqueID = ((UUIDStamped)object).uniqueID();

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

	private static String fullURL( final String typeIdentifierString, final String objectIdentifierString ) {
		final StringBuilder b = new StringBuilder();
		b.append( "/i/" );
		b.append( typeIdentifierString );
		b.append( "/" );
		b.append( objectIdentifierString );
		return b.toString();
	}
}