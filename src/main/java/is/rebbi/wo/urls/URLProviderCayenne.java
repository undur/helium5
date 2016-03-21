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

import is.rebbi.wo.util.SWSettings;

public class URLProviderCayenne extends URLProviderPersistent {

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
	@Override
	public String urlForObject( String entityName, Object serializedID, WOContext context ) {
		String typeIdentifier = urlPrefix( entityName );
		String objectIdentifier = objectIdentifier( serializedID );

		if( context == null ) {
			return urlWithDomain( typeIdentifier, objectIdentifier );
		}
		else {
			String url = urlWithoutDomain( typeIdentifier, objectIdentifier );

			if( !SWSettings.generateFriendlyURLs( context.request() ) ) {
				url = URLUtilities.makeURLDeveloperFriendly( url, context );
			}

			return url;
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
			ObjEntity objEntity = oc.getEntityResolver().getObjEntity( objEntityName );
			Collection<DbAttribute> primaryKeyAttributes = objEntity.getDbEntity().getPrimaryKeys();

			String identifier = objectIdentifier.substring( PK_IDENTIFIER_PREFIX.length(), objectIdentifier.length() );
			String[] components = identifier.split( "\\|" );

			Map<String, Object> keyMap = new HashMap<>();

			int i = 0;

			for( DbAttribute attribute : primaryKeyAttributes ) {
				keyMap.put( attribute.getName(), components[i++] );
			}

			SelectQuery q = new SelectQuery( objEntityName );
			Expression e = ExpressionFactory.matchAllDbExp( keyMap, Expression.EQUAL_TO );
			q.setQualifier( e );
			return (DataObject)q.selectOne( oc );
		}
		else {
			throw new RuntimeException( "Unsupported URL format" );
		}
	}
}