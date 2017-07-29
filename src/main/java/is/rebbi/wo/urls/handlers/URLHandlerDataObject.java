package is.rebbi.wo.urls.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.SelectQuery;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.cayenne.USCayenne;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.urls.providers.URLProviderDataObject;
import is.rebbi.wo.util.InspectAction;
import is.rebbi.wo.util.Inspection;

public class URLHandlerDataObject implements URLHandler {

	@Override
	public String prefix() {
		return "/i/";
	}

	@Override
	public BiFunction<String, WOContext, WOActionResults> execute() {
		return ( url, context ) -> {
			EntityViewDefinition def = viewDefinitionFromURL( url );

			Object object = objectFromURL( USCayenne.defaultObjectContext( context.session() ), url );

			if( object == null ) {
				return InspectAction.response404( url );
			}

			return Inspection.inspectObjectInContext( object, context );
		};
	}

	private static EntityViewDefinition<?, ?, ?> viewDefinitionFromURL( String url ) {
		String[] smu = url.split( "/" );
		String typeIdentifier = smu[2];
		String entityName = entityNameFromTypeIdentifier( typeIdentifier );
		return EntityViewDefinition.get( entityName );
	}

	/**
	 * @return The object the user wanted from the URL.
	 */
	private static DataObject objectFromURL( ObjectContext oc, String url ) {
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
			String identifier = objectIdentifier.substring( URLProviderDataObject.PK_IDENTIFIER_PREFIX.length(), objectIdentifier.length() );
			return objectFromIdentifier( oc, objEntityName, identifier );
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
		return (DataObject)q.selectOne( oc );
	}

	/**
	 * @return true if the given identifier is based on an object's primary key, rather than system generated.
	 */
	private static boolean objectIdentiferIsGeneric( String objectIdentifier ) {
		return objectIdentifier.startsWith( URLProviderDataObject.PK_IDENTIFIER_PREFIX );
	}

	private static String entityNameFromTypeIdentifier( String urlPrefix ) {
		return EntityViewDefinition.definitionForURLPrefix( urlPrefix ).name();
	}
}