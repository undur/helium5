package is.rebbi.wo.components.links;

import org.apache.cayenne.ObjectId;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.core.search.PointsToPersistent;
import is.rebbi.wo.urls.USURLProvider;

public class PointsToPersistentLink extends ERXStatelessComponent {

	public PointsToPersistentLink( WOContext context ) {
		super( context );
	}

	public PointsToPersistent object() {
		return (PointsToPersistent)valueForBinding( "object" );
	}

	public String href() {
		ObjectId oid = new ObjectId( object().targetEntityName(), "id", object().targetID() );
		return USURLProvider.urlForObjectInContext( oid, context() );
	}
}