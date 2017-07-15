package is.rebbi.wo.urls.providers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.SWSettings;

public class URLProviderDataObject extends URLProvider {

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	public static final String PK_IDENTIFIER_PREFIX = "id-";

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	public static final String ENTITY_IDENTIFIER_PREFIX = "entity-";

	/**
	 * @return A friendly URL.
	 */
	private static String friendlyURL( String protocol, String host, String typeIdentifier, String objectIdentifier ) {
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
	 * @return The url prefix for the given object.
	 */
	private static String urlPrefix( String entityName ) {
		EntityViewDefinition<?, ?, ?> type = EntityViewDefinition.get( entityName );

		if( type != null ) {
			String urlPrefix = type.urlPrefix();

			if( urlPrefix != null ) {
				return urlPrefix;
			}
		}

		return ENTITY_IDENTIFIER_PREFIX + entityName;
	}

	@Override
	public String urlForObject( Object object, WOContext context ) {
		DataObject dataObject = (DataObject)object;

		Map<String, Object> idSnapshot = dataObject.getObjectId().getIdSnapshot();
		List<String> keys = new ArrayList<>( idSnapshot.keySet() );
		keys.sort( Comparator.naturalOrder() );

		StringBuilder b = new StringBuilder();

		int i = 0;

		for( String key : keys ) {
			if( i++ > 0 ) {
				b.append( "|" );
			}

			b.append( idSnapshot.get( key ) );
		}

		String serializedID = b.toString();
		return urlForObject( dataObject.getObjectId().getEntityName(), serializedID, context );
	}

	/**
	 * @return A URL for the given object.
	 */
	public String urlForObject( String entityName, Object serializedID, WOContext context ) {
		String typeIdentifier = urlPrefix( entityName );
		String objectIdentifier = PK_IDENTIFIER_PREFIX + serializedID;

		if( context == null ) {
			return friendlyURL( "http", SWSettings.defaultDomainName(), typeIdentifier, objectIdentifier );
		}
		else {
			return friendlyURL( null, null, typeIdentifier, objectIdentifier );
		}
	}
}