package is.rebbi.wo.util;

import org.apache.cayenne.DataObject;

public class USCRUDUtilities {

	public static String entityNameFromObject( Object object ) {

		if( object instanceof DataObject ) {
			return ((DataObject)object).getObjectId().getEntityName();
		}

		throw new IllegalArgumentException( "Unsupported object class: " + object.getClass() );
	}
}