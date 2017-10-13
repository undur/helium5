package is.rebbi.wo.urls.providers;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;

import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.PKSerializer;
import jambalaya.Jambalaya;
import jambalaya.interfaces.UniqueIDStamped;

public class URLProviderDataObject extends URLProvider<DataObject> {

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
	public static final String ENTITY_IDENTIFIER_PREFIX = "entity-";

	/**
	 * @return The url prefix for the given object.
	 */
	private static String typeIdentifierForEntityName( String entityName ) {
		EntityViewDefinition<?, ?, ?> type = EntityViewDefinition.get( entityName );

		if( type != null ) {
			String typeIdentifier = type.urlPrefix();

			if( typeIdentifier != null ) {
				return typeIdentifier;
			}
		}

		return ENTITY_IDENTIFIER_PREFIX + entityName;
	}

	@Override
	public String urlForObject( DataObject dataObject ) {
		return urlForObjectId( dataObject.getObjectId() );
	}

	/**
	 * @return A URL for the given object.
	 */
	public static String urlForObjectId( ObjectId oid ) {
		String typeIdentifier = typeIdentifierForEntityName( oid.getEntityName() );
		String objectIdentifier;

		boolean isUniqueIDStamped = UniqueIDStamped.class.isAssignableFrom( Jambalaya.serverRuntime().getDataDomain().getEntityResolver().getObjEntity( oid.getEntityName() ).getJavaClass() );

		if( isUniqueIDStamped ) {
			objectIdentifier = UNIQUE_ID_IDENTIFIER_PREFIX + PKSerializer.serialize( oid );
		}
		else {
			objectIdentifier = PK_IDENTIFIER_PREFIX + PKSerializer.serialize( oid );
		}

		StringBuilder b = new StringBuilder();
		b.append( "/i/" );
		b.append( typeIdentifier );
		b.append( "/" );
		b.append( objectIdentifier );
		return b.toString();
	}
}