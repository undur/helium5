package is.rebbi.wo.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;

public class PKSerializer {

	private static final String PK_ELEMENT_SEPARATOR = "|";

	public static String serialize( DataObject eo ) {
		return serialize( eo.getObjectId() );
	}

	public static String serialize( ObjectId gid ) {

		Map<String, Object> idSnapshot = gid.getIdSnapshot();
		List<String> keys = new ArrayList<>( idSnapshot.keySet() );
		keys.sort( Comparator.naturalOrder() );

		StringBuilder b = new StringBuilder();

		int i = 0;

		for( String key : keys ) {
			if( i++ > 0 ) {
				b.append( PK_ELEMENT_SEPARATOR );
			}

			b.append( idSnapshot.get( key ) );
		}

		return b.toString();

	}

	public static DataObject eo( ObjectContext ec, String entityName, String string ) {
		return (DataObject)Cayenne.objectForPK( ec, entityName, string );
	}
}