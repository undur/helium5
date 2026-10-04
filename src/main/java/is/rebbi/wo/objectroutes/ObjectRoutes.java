package is.rebbi.wo.objectroutes;

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

import com.webobjects.appserver.WOActionResults;

import er.extensions.routing.Declined;
import er.extensions.routing.PlainRoute;
import er.extensions.routing.Route;
import er.extensions.routing.RouteGroup;
import er.extensions.routing.RouteInvocation;
import er.routing.conversion.Converters.Converter;

import is.rebbi.wo.objectroutes.Inspection.InspectionRoute;
import is.rebbi.wo.objectroutes.urls.URLProviderDataObject;

import jambalaya.Jambalaya;

/**
 * An object's page, at /i/{type}/{object}: the type is an inspection route's URL prefix ({@code dictionary-entry}), the
 * object its unique ID ({@code uid-…}) or its primary key ({@code id-…}). Declared by {@link HeliumPlugin} in every
 * application including helium.
 */
public class ObjectRoutes {

	/**
	 * An object's page
	 */
	public static final PlainRoute object = Route.plain();

	public static void declare( final RouteGroup routes ) {

		// A type that isn't an inspection route's declines the request (the converter is for helium's own type, so it
		// doesn't touch the application's parameters)
		routes.converters().register( InspectionRoute.class, Converter.of( InspectionRoute::forURLPrefix, InspectionRoute::urlPrefix ) );

		routes.map( "/i/{type}/{object}", object, ObjectRoutes::inspect );
	}

	/**
	 * @return The object's page, the object found in the entity its type names: one that isn't there declines
	 */
	private static WOActionResults inspect( final RouteInvocation invocation ) {
		final InspectionRoute type = invocation.parameter( "type", InspectionRoute.class );
		final String identifier = invocation.parameter( "object" );
		final PersistentObject found = objectFromIdentifierString( Jambalaya.newContext(), type.entityClass().getSimpleName(), identifier );

		if( found == null ) {
			throw new Declined( "There's no %s '%s'".formatted( type.entityClass().getSimpleName(), identifier ) );
		}

		return Inspection.inspectObjectInContext( found, invocation.context() );
	}

	static PersistentObject objectFromIdentifierString( final ObjectContext oc, final String objectEntityName, final String objectIdentifier ) {
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
}
