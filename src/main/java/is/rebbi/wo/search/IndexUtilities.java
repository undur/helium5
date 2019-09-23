package is.rebbi.wo.search;

import org.apache.cayenne.DataObject;

import is.rebbi.core.search.IndexRecord;
import jambalaya.PKSerializer;

public class IndexUtilities {

	public static IndexRecord create( DataObject obj ) {
		String entityName = obj.getObjectId().getEntityName();
		String targetID = PKSerializer.serialize( obj.getObjectId() );
		return IndexRecord.create( entityName, targetID );
	}
}