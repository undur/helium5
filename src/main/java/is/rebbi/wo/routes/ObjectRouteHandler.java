package is.rebbi.wo.routes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.SelectQuery;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.routes.RouteTable.RouteHandler;
import is.rebbi.wo.urls.URLProviderDataObject;
import is.rebbi.wo.util.Inspection;
import is.rebbi.wo.util.Inspection.InspectionRoute;
import is.rebbi.wo.util.USHTTPUtilities;
import jambalaya.Jambalaya;

public class ObjectRouteHandler extends RouteHandler {

	@Override
	public WOActionResults handle( final WrappedURL url, final WOContext context ) {
		final Object object = selectedObject( url );

		if( object == null ) {
			return USHTTPUtilities.statusResponse( 404, "Nothing found at: " + url );
		}

		return Inspection.inspectObjectInContext( object, context );
	}

	/**
	 * @return The object the user wanted from the URL.
	 */
	private static DataObject selectedObject( final WrappedURL path ) {
		final String typeIdentifier = path.getString( 1 );
		final String objectIdentifier = path.getString( 2 );

		final String objEntityName = entityNameFromTypeIdentifier( typeIdentifier );

		if( objectIdentifier.startsWith( URLProviderDataObject.PK_IDENTIFIER_PREFIX ) ) {
			final String identifier = objectIdentifier.substring( URLProviderDataObject.PK_IDENTIFIER_PREFIX.length(), objectIdentifier.length() );
			return objectFromPKString( Jambalaya.newContext(), objEntityName, identifier );
		}

		if( objectIdentifier.startsWith( URLProviderDataObject.UNIQUE_ID_IDENTIFIER_PREFIX ) ) {
			final String identifier = objectIdentifier.substring( URLProviderDataObject.UNIQUE_ID_IDENTIFIER_PREFIX.length(), objectIdentifier.length() );
			return objectFromUniqueID( Jambalaya.newContext(), objEntityName, identifier );
		}

		throw new RuntimeException( "Unsupported URL format" );
	}

	private static DataObject objectFromUniqueID( final ObjectContext oc, final String objEntityName, final String uniqueID ) {
		final SelectQuery<?> q = new SelectQuery<>( objEntityName, ExpressionFactory.matchExp( "uniqueID", uniqueID ) );
		return (DataObject)q.selectOne( oc );
	}

	private static DataObject objectFromPKString( final ObjectContext oc, final String objEntityName, final String identifier ) {
		final ObjEntity objEntity = oc.getEntityResolver().getObjEntity( objEntityName );
		final Collection<DbAttribute> primaryKeyAttributes = objEntity.getDbEntity().getPrimaryKeys();
		final String[] components = identifier.split( "\\|" );

		final Map<String, Object> keyMap = new HashMap<>();

		int i = 0;

		for( final DbAttribute attribute : primaryKeyAttributes ) {
			keyMap.put( attribute.getName(), components[i++] );
		}

		final SelectQuery<?> q = new SelectQuery<>( objEntityName, ExpressionFactory.matchAllDbExp( keyMap, Expression.EQUAL_TO ) );
		return (DataObject)q.selectOne( oc );
	}

	/**
	 * FIXME: Evaluate this method
	 */
	private static String entityNameFromTypeIdentifier( final String typeIdentifier ) {
		InspectionRoute inspectionRoute = InspectionRoute.forURLPrefix( typeIdentifier );

		if( inspectionRoute == null ) {
			throw new RuntimeException( "No view definition found for URL prefix: " + typeIdentifier );
		}

		return inspectionRoute.entityClass().getName();
	}
}