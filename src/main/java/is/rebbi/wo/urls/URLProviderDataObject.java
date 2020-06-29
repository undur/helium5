package is.rebbi.wo.urls;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;

import is.rebbi.wo.definitions.EntityViewDefinition;
import jambalaya.ObjectIdSerializer;
import jambalaya.interfaces.UniqueIDStamped;

public class URLProviderDataObject implements URLProvider<DataObject> {

	public static final String PK_IDENTIFIER_PREFIX = "id-";

	public static final String UNIQUE_ID_IDENTIFIER_PREFIX = "uid-";

	@Override
	public String urlForObject( final DataObject dataObject ) {
		if( dataObject instanceof UniqueIDStamped ) {
			final String uniqueID = ((UniqueIDStamped)dataObject).uniqueID();

			if( uniqueID != null ) {
				return urlForUniqueID( dataObject.getObjectId().getEntityName(), uniqueID );
			}
		}

		return urlForObjectId( dataObject.getObjectId() );
	}

	public static String urlForObjectId( final ObjectId objectId ) {
		final String typeIdentifierString = EntityViewDefinition.get( objectId.getEntityName() ).urlPrefix();
		final String objectIdentifierString = PK_IDENTIFIER_PREFIX + ObjectIdSerializer.serialize( objectId );
		return fullURL( typeIdentifierString, objectIdentifierString );
	}

	public static String urlForUniqueID( final String entityName, final String uniqueID ) {
		final String typeIdentifierString = EntityViewDefinition.get( entityName ).urlPrefix();
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