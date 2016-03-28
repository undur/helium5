package is.rebbi.wo.search;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DataObject;

import is.rebbi.core.search.IndexRecord;

public class IndexUtilities {

	public static IndexRecord create( DataObject obj ) {
		String entityName = obj.getObjectId().getEntityName();
		String targetID = String.valueOf( Cayenne.longPKForObject( obj ) );
		return IndexRecord.create( entityName, targetID );
	}
}