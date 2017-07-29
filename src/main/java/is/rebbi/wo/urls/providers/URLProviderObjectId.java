package is.rebbi.wo.urls.providers;

import org.apache.cayenne.ObjectId;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.util.PKSerializer;

public class URLProviderObjectId extends URLProvider<ObjectId> {

	@Override
	public String urlForObject( ObjectId oid, WOContext context ) {
		return new URLProviderDataObject().urlForObject( oid.getEntityName(), PKSerializer.serialize( oid ), context );
	}
}