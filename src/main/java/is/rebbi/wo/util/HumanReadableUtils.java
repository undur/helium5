package is.rebbi.wo.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.DataObject;

import is.rebbi.core.formatters.FormatterWrapperNullSafe;
import is.rebbi.core.search.IndexRecord;
import is.rebbi.core.search.Indexable;
import is.rebbi.wo.definitions.EntityViewDefinition;

public class HumanReadableUtils {

	private static final Format dateFormatWithTime() {
		return new FormatterWrapperNullSafe( new SimpleDateFormat( "yyyy-MM-dd HH:mm" ) );
	}

	public static String toStringHuman( Object object ) {

		if( object == null ) {
			return null;
		}

		if( object instanceof List ) {
			List<String> result = new ArrayList<>();

			for( Object each : ((List)object) ) {
				result.add( toStringHuman( each ) );
			}

			return String.join( ", ", result );
		}

		if( object instanceof HumanReadable ) {
			return ((HumanReadable)object).toStringHuman();
		}

		if( object instanceof Indexable ) {
			IndexRecord indexRecord = ((Indexable)object).indexRecord();

			if( indexRecord != null ) {
				return indexRecord.name();
			}
		}

		if( object instanceof Date ) {
			return dateFormatWithTime().format( object );
		}

		if( object instanceof DataObject ) {
			StringBuilder b = new StringBuilder();
			b.append( EntityViewDefinition.get( object.getClass() ).icelandicName() );
			b.append( "#" );
			b.append( ((DataObject)object).getObjectId() );
			return b.toString();
		}

		return object.toString();
	}
}