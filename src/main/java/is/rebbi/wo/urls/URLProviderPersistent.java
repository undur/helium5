package is.rebbi.wo.urls;

import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.SWSettings;

public abstract class URLProviderPersistent extends URLProvider {

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	protected static final String PK_IDENTIFIER_PREFIX = "id-";

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	protected static final String ENTITY_IDENTIFIER_PREFIX = "entity-";

	/**
	 * @return A friendly URL without the domain.
	 */
	protected static String urlWithoutDomain( String typeIdentifier, String objectIdentifier ) {
		return friendlyURL( null, null, typeIdentifier, objectIdentifier );
	}

	/**
	 * @return A friendly URL including the domain.
	 */
	protected static String urlWithDomain( String typeIdentifier, String objectIdentifier ) {
		return friendlyURL( "http", SWSettings.defaultDomainName(), typeIdentifier, objectIdentifier );
	}

	/**
	 * @return A friendly URL.
	 */
	protected static String friendlyURL( String protocol, String host, String typeIdentifier, String objectIdentifier ) {
		StringBuilder b = new StringBuilder();

		if( protocol != null ) {
			b.append( protocol );
			b.append( "://" );
		}

		if( host != null ) {
			b.append( host );
		}

		b.append( "/i/" );
		b.append( typeIdentifier );
		b.append( "/" );
		b.append( objectIdentifier );

		return b.toString();
	}

	/**
	 * @return true if the given identifier is based on an object's primary key, rather than system generated.
	 */
	protected static boolean objectIdentiferIsGeneric( String objectIdentifier ) {
		return objectIdentifier.startsWith( PK_IDENTIFIER_PREFIX );
	}

	/**
	 * @return true if the given identifier is based on an object's primary key, rather than system generated.
	 */
	protected static boolean typeIdentifierIsGeneric( String typeIdentifier ) {
		return typeIdentifier.startsWith( ENTITY_IDENTIFIER_PREFIX );
	}

	protected static String entityNameFromTypeIdentifier( String urlPrefix ) {
		return EntityViewDefinition.definitionForURLPrefix( urlPrefix ).name();
	}

	/**
	 * @return URL identifier for objects based on  primary key.
	 */
	static String objectIdentifier( Object primaryKey ) {
		return PK_IDENTIFIER_PREFIX + primaryKey;
	}

	/**
	 * @return The url prefix for the given object.
	 */
	protected static String urlPrefix( String entityName ) {
		EntityViewDefinition type = EntityViewDefinition.get( entityName );

		if( type != null ) {
			String urlPrefix = type.urlPrefix();

			if( urlPrefix != null ) {
				return urlPrefix;
			}
		}

		return ENTITY_IDENTIFIER_PREFIX + entityName;
	}

	public static EntityViewDefinition viewDefinitionFromURL( String url ) {
		String[] smu = url.split( "/" );
		String typeIdentifier = smu[2];
		String entityName = entityNameFromTypeIdentifier( typeIdentifier );
		return EntityViewDefinition.get( entityName );
	}
}