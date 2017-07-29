package is.rebbi.wo.urls.providers;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.PKSerializer;

public class URLProviderDataObject extends URLProvider<DataObject> {

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	public static final String PK_IDENTIFIER_PREFIX = "id-";

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
	public String urlForObject( DataObject dataObject, WOContext context ) {
		ObjectId oid = dataObject.getObjectId();
		String idString = PKSerializer.serialize( oid );
		return urlForObject( oid.getEntityName(), idString, context );
	}

	/**
	 * @return A URL for the given object.
	 */
	public String urlForObject( String entityName, Object serializedID, WOContext context ) {
		String typeIdentifier = typeIdentifierForEntityName( entityName );
		String objectIdentifier = PK_IDENTIFIER_PREFIX + serializedID;

		StringBuilder b = new StringBuilder();
		b.append( "/i/" );
		b.append( typeIdentifier );
		b.append( "/" );
		b.append( objectIdentifier );
		return b.toString();
	}
}