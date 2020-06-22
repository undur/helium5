package is.rebbi.wo.urls.handlers;

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

import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.urls.providers.URLProviderDataObject;
import is.rebbi.wo.util.InspectAction;
import is.rebbi.wo.util.Inspection;
import jambalaya.Jambalaya;

public class URLHandlerDataObject extends URLHandler {

	public URLHandlerDataObject( String url, WOContext context ) {
		super( url, context );
	}

	@Override
	public WOActionResults generateResponse() {
		Object object = selectedObject();

		if( object == null ) {
			return InspectAction.response404( url() );
		}

		if( operationIdentifier() == null || operationIdentifier().equals( "view" ) ) {
			return Inspection.inspectObjectInContext( object, context() );
		}

		// if( operationIdentifier() == null || operationIdentifier().equals( "edit" ) ) {
		// return Inspection.editObjectInContext( object, context() );
		// }

		return InspectAction.response404( url() );
	}

	/**
	 * Identifies the type of the requested object.
	 */
	private String typeIdentifier() {
		return path().getString( 1 );
	}

	private String objectIdentifier() {
		return path().getString( 2 );
	}

	// FIXME: Implement // Hugi 2018-06-09
	private String operationIdentifier() {
		return path().getString( 3 );
	}

	/**
	 * @return The object the user wanted from the URL.
	 */
	public DataObject selectedObject() {

		final String objEntityName = entityNameFromTypeIdentifier( typeIdentifier() );

		if( objectIdentifier().startsWith( URLProviderDataObject.PK_IDENTIFIER_PREFIX ) ) {
			final String identifier = objectIdentifier().substring( URLProviderDataObject.PK_IDENTIFIER_PREFIX.length(), objectIdentifier().length() );
			return objectFromPKString( Jambalaya.newContext(), objEntityName, identifier );
		}

		if( objectIdentifier().startsWith( URLProviderDataObject.UNIQUE_ID_IDENTIFIER_PREFIX ) ) {
			final String identifier = objectIdentifier().substring( URLProviderDataObject.UNIQUE_ID_IDENTIFIER_PREFIX.length(), objectIdentifier().length() );
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

	private static String entityNameFromTypeIdentifier( final String typeIdentifier ) {
		EntityViewDefinition viewDefinition = EntityViewDefinition.definitionForURLPrefix( typeIdentifier );

		if( viewDefinition == null ) {
			throw new RuntimeException( "No view definition found for URL prefix: " + typeIdentifier );
		}

		return viewDefinition.name();
	}
}