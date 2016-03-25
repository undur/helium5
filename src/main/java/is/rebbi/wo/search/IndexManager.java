package is.rebbi.wo.search;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

import er.extensions.eof.ERXEC;
import is.rebbi.core.search.Indexable;

public class IndexManager {

	/**
	 * The transaction watcher is a singleton - this is the instance.
	 */
	private static IndexManager _instance;

	/**
	 * Key set in EC userinfo indicating that this manager should be disabled in them.
	 */
	private static final String DISABLED_MARKER = "DISABLED" + IndexManager.class.getSimpleName();

	/**
	 * Marks the given Editing context to disable any logging.
	 */
	public static void disableInEditingContext( EOEditingContext ec ) {
		ec.setUserInfoForKey( true, DISABLED_MARKER );
	}

	/**
	 * Marks the given Editing context to disable any logging.
	 */
	private static boolean isDisabledInEditingContext( EOEditingContext ec ) {
		return ec.userInfoForKey( DISABLED_MARKER ) != null;
	}

	private IndexManager() {};

	/**
	 * Start watching transactions
	 */
	public static void register() {
		NSSelector<IndexManager> beforeSaveSelector = new NSSelector<>( "beforeSaveChangesInEditingContext", new Class[] { NSNotification.class } );
		NSNotificationCenter.defaultCenter().addObserver( instance(), beforeSaveSelector, ERXEC.EditingContextWillSaveChangesNotification, null );
	}

	/**
	 * Creates our default transaction manager.
	 */
	public static IndexManager instance() {
		if( _instance == null ) {
			_instance = new IndexManager();
		}

		return _instance;
	}

	/**
	 * Invoked each time changes are saved in the application.
	 */
	public void beforeSaveChangesInEditingContext( NSNotification notification ) {
		EOEditingContext ec = (EOEditingContext)notification.object();

		if( !isDisabledInEditingContext( ec ) ) {
			if( ec.parentObjectStore() instanceof EOObjectStoreCoordinator ) {
				for( EOEnterpriseObject eo : ec.insertedObjects() ) {
					if( eo instanceof Indexable ) {
						Indexable indexable = (Indexable)eo;
						Indexer.updateRecord( indexable.indexRecord() );
					}
				}

				for( EOEnterpriseObject eo : ec.updatedObjects() ) {
					if( eo instanceof Indexable ) {
						Indexable indexable = (Indexable)eo;
						Indexer.updateRecord( indexable.indexRecord() );
					}
				}

				for( EOEnterpriseObject eo : ec.deletedObjects() ) {
					if( eo instanceof Indexable ) {
						Indexable indexable = (Indexable)eo;
						Indexer.deleteRecord( indexable.indexRecord() );
					}
				}
			}
		}
	}
}