package is.rebbi.wo.util;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DataObject;

import er.extensions.eof.ERXGenericRecord;
import is.rebbi.core.search.IndexRecord;

public class IndexingEOUtilities {

	public static IndexRecord create( ERXGenericRecord obj ) {
		String entityName = obj.entityName();
		String targetID = PKSerializerEOF.serialize( obj );
		return IndexRecord.create( entityName, targetID );
	}

	public static IndexRecord create( DataObject obj ) {
		String entityName = obj.getObjectId().getEntityName();
		String targetID = String.valueOf( Cayenne.longPKForObject( obj ) );
		return IndexRecord.create( entityName, targetID );
	}
}