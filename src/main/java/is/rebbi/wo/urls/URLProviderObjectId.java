package is.rebbi.wo.urls;

import org.apache.cayenne.ObjectId;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.util.PKSerializer;

public class URLProviderObjectId extends URLProvider {

	@Override
	public String urlForObject( Object object, WOContext context ) {
		ObjectId oid = (ObjectId)object;
		return new URLProviderCayenne().urlForObject( oid.getEntityName(), PKSerializer.serialize( oid ), context );
	}
}