package is.rebbi.wo.search;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.lifecycle.changemap.ChangeMap;
import org.apache.cayenne.lifecycle.changemap.ObjectChange;
import org.apache.cayenne.lifecycle.changemap.ObjectChangeType;
import org.apache.cayenne.lifecycle.postcommit.PostCommitListener;

import is.rebbi.core.search.Indexable;
import is.rebbi.wo.util.PKSerializer;

public class IndexListener implements PostCommitListener {

	/**
	 * Key set in EC userinfo indicating that this manager should be disabled in them.
	 */
	private static final String DISABLED_MARKER = "DISABLED" + IndexListener.class.getSimpleName();

	/**
	 * Marks the given Editing context to disable any logging.
	 */
	public static void disableInObjectContext( ObjectContext ec ) {
		ec.setUserProperty( DISABLED_MARKER, true );
	}

	/**
	 * Marks the given Editing context to disable any logging.
	 */
	private static boolean isDisabledInObjectContext( ObjectContext ec ) {
		return ec.getUserProperty( DISABLED_MARKER ) != null;
	}

	/**
	 * @return A unique ID for the record in the index.
	 *
	 * TODO: This probably belongs in Indexable or IndexRecord.
	 */
	private static String uniqueIDFromObjectId( ObjectId objectId ) {
		return objectId.getEntityName() + PKSerializer.serialize( objectId );
	}

	@Override
	public void onPostCommit( ObjectContext originatingContext, ChangeMap changeMap ) {

		if( !isDisabledInObjectContext( originatingContext ) ) {
			for( java.util.Map.Entry<ObjectId, ? extends ObjectChange> changes : changeMap.getChanges().entrySet() ) {
				ObjectId objectId = changes.getKey();

				Class<?> entityClass = originatingContext.getEntityResolver().getObjEntity( objectId.getEntityName() ).getJavaClass();
				boolean isIndexable = Indexable.class.isAssignableFrom( entityClass );

				if( isIndexable ) {
					ObjectChangeType changeType = changes.getValue().getType();

					if( changeType.equals( ObjectChangeType.UPDATE ) || changes.getValue().getType().equals( ObjectChangeType.INSERT ) ) {
						Indexable indexable = (Indexable)Cayenne.objectForPK( originatingContext, objectId );
						Indexer.updateRecord( indexable.indexRecord() );
					}
					else if( changeType.equals( ObjectChangeType.DELETE ) ) {
						Indexer.deleteRecord( uniqueIDFromObjectId( objectId ) );
					}
				}
			}
		}
	}
}