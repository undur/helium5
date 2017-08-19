package is.rebbi.wo.urls.providers;

import org.apache.cayenne.ObjectId;

public class URLProviderObjectId extends URLProvider<ObjectId> {

	@Override
	public String urlForObject( ObjectId oid ) {
		return URLProviderDataObject.urlForObjectId( oid );
	}
}