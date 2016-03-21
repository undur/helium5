package is.rebbi.wo.util;

import org.apache.cayenne.DataObject;

import er.extensions.eof.ERXGenericRecord;

public class USCRUDUtilities {

	public static String entityNameFromObject( Object object ) {

		if( object instanceof DataObject ) {
			return ((DataObject)object).getObjectId().getEntityName();
		}

		if( object instanceof ERXGenericRecord ) {
			return ((ERXGenericRecord)object).entity().name();
		}

		throw new IllegalArgumentException( "Unsupported object class: " + object.getClass() );
	}
}