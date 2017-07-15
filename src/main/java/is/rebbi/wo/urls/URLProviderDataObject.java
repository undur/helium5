package is.rebbi.wo.urls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.SelectQuery;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.SWSettings;

public class URLProviderDataObject extends URLProvider {

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	private static final String PK_IDENTIFIER_PREFIX = "id-";

	/**
	 * If an object does not implement UrlFriendlyNaming, the URL will contain this prefix and the Object's id.
	 */
	private static final String ENTITY_IDENTIFIER_PREFIX = "entity-";

	/**
	 * @return A friendly URL without the domain.
	 */
	private static String relativeURL( String typeIdentifier, String objectIdentifier ) {
		return friendlyURL( null, null, typeIdentifier, objectIdentifier );
	}

	/**
	 * @return A friendly URL including the domain.
	 */
	private static String absoluteURL( String typeIdentifier, String objectIdentifier ) {
		return friendlyURL( "http", SWSettings.defaultDomainName(), typeIdentifier, objectIdentifier );
	}

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
	 * @return true if the given identifier is based on an object's primary key, rather than system generated.
	 */
	private static boolean objectIdentiferIsGeneric( String objectIdentifier ) {
		return objectIdentifier.startsWith( PK_IDENTIFIER_PREFIX );
	}

	private static String entityNameFromTypeIdentifier( String urlPrefix ) {
		return EntityViewDefinition.definitionForURLPrefix( urlPrefix ).name();
	}

	/**
	 * @return URL identifier for objects based on primary key.
	 */
	private static String objectIdentifier( Object primaryKey ) {
		return PK_IDENTIFIER_PREFIX + primaryKey;
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

	public static EntityViewDefinition<?, ?, ?> viewDefinitionFromURL( String url ) {
		String[] smu = url.split( "/" );
		String typeIdentifier = smu[2];
		String entityName = entityNameFromTypeIdentifier( typeIdentifier );
		return EntityViewDefinition.get( entityName );
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
		String objectIdentifier = objectIdentifier( serializedID );

		System.out.println( "typeIdentifier: " + typeIdentifier );
		System.out.println( "objectIdentifier: " + objectIdentifier );
		if( context == null ) {
			return absoluteURL( typeIdentifier, objectIdentifier );
		}
		else {
			return relativeURL( typeIdentifier, objectIdentifier );
		}
	}

	/**
	 * @return The object the user wanted from the URL.
	 */
	public static DataObject objectFromURL( ObjectContext oc, String url ) {
		String[] smu = url.split( "/" );
		String typeIdentifier = smu[2];
		String objectIdentifier = smu[3];
		return objectFromIdentifiers( oc, typeIdentifier, objectIdentifier );
	}

	/**
	 * @return The object specified by the parameters.
	 */
	private static DataObject objectFromIdentifiers( ObjectContext oc, String typeIdentifier, String objectIdentifier ) {

		if( objectIdentiferIsGeneric( objectIdentifier ) ) {
			String objEntityName = entityNameFromTypeIdentifier( typeIdentifier );
			String identifier = objectIdentifier.substring( PK_IDENTIFIER_PREFIX.length(), objectIdentifier.length() );
			DataObject object = objectFromIdentifier( oc, objEntityName, identifier );
			return object;
		}
		else {
			throw new RuntimeException( "Unsupported URL format" );
		}
	}

	private static DataObject objectFromIdentifier( ObjectContext oc, String objEntityName, String identifier ) {
		ObjEntity objEntity = oc.getEntityResolver().getObjEntity( objEntityName );
		Collection<DbAttribute> primaryKeyAttributes = objEntity.getDbEntity().getPrimaryKeys();
		String[] components = identifier.split( "\\|" );

		Map<String, Object> keyMap = new HashMap<>();

		int i = 0;

		for( DbAttribute attribute : primaryKeyAttributes ) {
			keyMap.put( attribute.getName(), components[i++] );
		}

		SelectQuery<?> q = new SelectQuery<>( objEntityName );
		Expression e = ExpressionFactory.matchAllDbExp( keyMap, Expression.EQUAL_TO );
		q.setQualifier( e );
		DataObject object = (DataObject)q.selectOne( oc );
		return object;
	}
}