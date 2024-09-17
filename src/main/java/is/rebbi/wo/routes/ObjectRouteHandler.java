package is.rebbi.wo.routes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistentObject;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.routes.RouteTable.RouteHandler;
import is.rebbi.wo.urls.URLProviderDataObject;
import is.rebbi.wo.util.Inspection;
import is.rebbi.wo.util.Inspection.InspectionRoute;
import is.rebbi.wo.util.USHTTPUtilities;
import jambalaya.Jambalaya;

public class ObjectRouteHandler extends RouteHandler {

	private static final Logger logger = LoggerFactory.getLogger( ObjectRouteHandler.class );

	@Override
	public WOActionResults handle( final WrappedURL url, final WOContext context ) {
		final Object object = selectedObject( url );

		// FIXME: 404 handling could really use some improvement here.
		if( object == null ) {
			logger.warn( "Nothing found at {}", url );
			return USHTTPUtilities.statusResponse( 404, "Nothing found at: " + url );
		}

		return Inspection.inspectObjectInContext( object, context );
	}

	/**
	 * @return The object the user wanted from the URL.
	 */
	private static PersistentObject selectedObject( final WrappedURL path ) {
		final String objectTypeIdentifier = path.getString( 1 );
		final String objectIdentifier = path.getString( 2 );

		final String objectEntityName = entityNameFromTypeIdentifier( objectTypeIdentifier );

		if( objectEntityName == null ) {
			return null;
		}

		return objectFromIdentifierString( Jambalaya.newContext(), objectEntityName, objectIdentifier );
	}

	private static PersistentObject objectFromIdentifierString( final ObjectContext oc, final String objectEntityName, final String objectIdentifier ) {
		if( objectIdentifier.startsWith( URLProviderDataObject.PK_IDENTIFIER_PREFIX ) ) {
			final String identifier = objectIdentifier.substring( URLProviderDataObject.PK_IDENTIFIER_PREFIX.length(), objectIdentifier.length() );
			return objectFromPKString( oc, objectEntityName, identifier );
		}

		if( objectIdentifier.startsWith( URLProviderDataObject.UNIQUE_ID_IDENTIFIER_PREFIX ) ) {
			final String identifier = objectIdentifier.substring( URLProviderDataObject.UNIQUE_ID_IDENTIFIER_PREFIX.length(), objectIdentifier.length() );
			return objectFromUniqueID( oc, objectEntityName, identifier );
		}

		// FIXME: Just returning null feels wrong, we should be throwing an exception here (probably resulting in a 404 in the front end) // Hugi 2024-09-17
		return null;
	}

	private static PersistentObject objectFromUniqueID( final ObjectContext oc, final String objEntityName, final String uniqueID ) {
		return ObjectSelect
				.query( PersistentObject.class, objEntityName )
				.where( ExpressionFactory.matchExp( "uniqueID", uniqueID ) )
				.selectOne( oc );
	}

	private static PersistentObject objectFromPKString( final ObjectContext oc, final String objEntityName, final String identifier ) {
		final ObjEntity objEntity = oc.getEntityResolver().getObjEntity( objEntityName );
		final Collection<DbAttribute> primaryKeyAttributes = objEntity.getDbEntity().getPrimaryKeys();
		final String[] components = identifier.split( "\\|" );

		final Map<String, Object> keyMap = new HashMap<>();

		int i = 0;

		for( final DbAttribute attribute : primaryKeyAttributes ) {
			keyMap.put( attribute.getName(), components[i++] );
		}

		final Expression exp = ExpressionFactory.matchAllDbExp( keyMap, Expression.EQUAL_TO );

		return ObjectSelect
				.query( PersistentObject.class, objEntityName )
				.where( exp )
				.selectOne( oc );
	}

	/**
	 * @return The name of the identifier identified by the type identifier
	 */
	private static String entityNameFromTypeIdentifier( final String typeIdentifier ) {
		InspectionRoute inspectionRoute = InspectionRoute.forURLPrefix( typeIdentifier );

		if( inspectionRoute == null ) {
			// FIXME: If no identifier is found we should be throwing an exception here (probably resulting in a 404 in the front end) // Hugi 2024-09-17
			return null;
		}

		return inspectionRoute.entityClass().getSimpleName();
	}
}