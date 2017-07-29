package is.rebbi.wo.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;

public class PKSerializer {

	private static final String PK_ELEMENT_SEPARATOR = "|";

	public static String serialize( DataObject dataObject ) {
		return serialize( dataObject.getObjectId() );
	}

	public static String serialize( ObjectId oid ) {
		List<String> keys = new ArrayList<>( oid.getIdSnapshot().keySet() );
		keys.sort( Comparator.naturalOrder() );
		return String.join( PK_ELEMENT_SEPARATOR, keys );
	}

	public static DataObject eo( ObjectContext ec, String entityName, String string ) {
		return (DataObject)Cayenne.objectForPK( ec, entityName, string );
	}
}