package is.rebbi.wo.urls.providers;

import org.apache.cayenne.ObjectId;

import com.webobjects.appserver.WOContext;

public class URLProviderObjectId extends URLProvider<ObjectId> {

	@Override
	public String urlForObject( ObjectId oid, WOContext context ) {
		return URLProviderDataObject.urlForObjectId( oid, context );
	}
}