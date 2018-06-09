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
import is.rebbi.wo.urls.USURLPath;
import is.rebbi.wo.urls.providers.URLProviderDataObject;
import is.rebbi.wo.util.InspectAction;
import is.rebbi.wo.util.Inspection;

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

		return Inspection.inspectObjectInContext( object, context() );
	}

	private String typeIdentifier() {
	    return url().split( "/" )[2];
	}

	private String objectIdentifier() {
	    return url().split( "/" )[3];
	}

    public ObjectContext oc() {
        return USCayenne.defaultObjectContext( context().session() );
    }

	/**
	 * @return The object the user wanted from the URL.
	 */
	public DataObject selectedObject() {

		String objEntityName = entityNameFromTypeIdentifier( typeIdentifier() );

		if( objectIdentifier().startsWith( URLProviderDataObject.PK_IDENTIFIER_PREFIX ) ) {
			String identifier = objectIdentifier().substring( URLProviderDataObject.PK_IDENTIFIER_PREFIX.length(), objectIdentifier().length() );
			return objectFromPKString( oc(), objEntityName, identifier );
		}
		
		if( objectIdentifier().startsWith( URLProviderDataObject.UNIQUE_ID_IDENTIFIER_PREFIX ) ) {
		    String identifier = objectIdentifier().substring( URLProviderDataObject.UNIQUE_ID_IDENTIFIER_PREFIX.length(), objectIdentifier().length() );
		    return objectFromUniqueID( oc(), objEntityName, identifier );
		}

		throw new RuntimeException( "Unsupported URL format" );
	}

	private static DataObject objectFromUniqueID( ObjectContext oc, String objEntityName, String uid ) {
		SelectQuery<?> q = new SelectQuery<>( objEntityName, ExpressionFactory.matchExp( "uniqueID", uid ) );
		return (DataObject)q.selectOne( oc );
	}

	private static DataObject objectFromPKString( ObjectContext oc, String objEntityName, String identifier ) {
		ObjEntity objEntity = oc.getEntityResolver().getObjEntity( objEntityName );
		Collection<DbAttribute> primaryKeyAttributes = objEntity.getDbEntity().getPrimaryKeys();
		String[] components = identifier.split( "\\|" );

		Map<String, Object> keyMap = new HashMap<>();

		int i = 0;

		for( DbAttribute attribute : primaryKeyAttributes ) {
			keyMap.put( attribute.getName(), components[i++] );
		}

		SelectQuery<?> q = new SelectQuery<>( objEntityName, ExpressionFactory.matchAllDbExp( keyMap, Expression.EQUAL_TO ) );
		return (DataObject)q.selectOne( oc );
	}

	private static String entityNameFromTypeIdentifier( String urlPrefix ) {
		EntityViewDefinition viewDefinition = EntityViewDefinition.definitionForURLPrefix( urlPrefix );

		if( viewDefinition == null ) {
			throw new RuntimeException( "No view definition found for URL prefix: " + urlPrefix );
		}

		return viewDefinition.name();
	}
}