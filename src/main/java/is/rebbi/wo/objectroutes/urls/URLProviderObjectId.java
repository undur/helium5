package is.rebbi.wo.objectroutes.urls;

import org.apache.cayenne.ObjectId;

public class URLProviderObjectId implements URLProvider<ObjectId> {

	@Override
	public String urlForObject( ObjectId oid ) {
		return URLProviderDataObject.urlForObjectId( oid );
	}
}