package is.rebbi.wo.urls.providers;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;

import is.rebbi.wo.definitions.EntityViewDefinition;
import jambalaya.PKSerializer;
import jambalaya.interfaces.UniqueIDStamped;

public class URLProviderDataObject implements URLProvider<DataObject> {

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	public static final String PK_IDENTIFIER_PREFIX = "id-";

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	public static final String UNIQUE_ID_IDENTIFIER_PREFIX = "uid-";

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	private static final String ENTITY_IDENTIFIER_PREFIX = "entity-";

	/**
	 * @return The url prefix for the given object.
	 */
	private static String typeIdentifierForEntityName( final String entityName ) {
		final EntityViewDefinition<?, ?, ?> type = EntityViewDefinition.get( entityName );

		if( type != null ) {
			final String typeIdentifier = type.urlPrefix();

			if( typeIdentifier != null ) {
				return typeIdentifier;
			}
		}

		return ENTITY_IDENTIFIER_PREFIX + entityName;
	}

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
		String typeIdentifierString = typeIdentifierForEntityName( objectId.getEntityName() );
		String objectIdentifierString = PK_IDENTIFIER_PREFIX + PKSerializer.serialize( objectId );
		return fullURL( typeIdentifierString, objectIdentifierString );
	}

	public static String urlForUniqueID( final String entityName, final String uniqueID ) {
		final String typeIdentifierString = typeIdentifierForEntityName( entityName );
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