package is.rebbi.wo.util;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXSelectorUtilities;
import is.rebbi.wo.interfaces.TimeStampedCreation;
import is.rebbi.wo.interfaces.TimeStampedModification;
import is.rebbi.wo.interfaces.USUser;
import is.rebbi.wo.interfaces.UUIDStamped;
import is.rebbi.wo.interfaces.UniqueIDStamped;
import is.rebbi.wo.interfaces.UserStampedCreatedBy;
import is.rebbi.wo.interfaces.UserStampedModifiedBy;

/**
 * Automatically updates EOs with some common bits of data.
 *
 * - Objects that implement the interface UUIDStamped will be stamped with a UUID at time of insertion.
 * - Objects that implement TimeStamped will get stamped with a creationDate on insertion and a modificationDate on modification.
 * - Objects that implement UserStamped will get stamped with createdBy on insertion and modifiedBy on modification (if the EC has a user set).
 */

public class TransactionStamper {

	/**
	 * Key used to reference the current user for ECs.
	 */
	private static final String USER_USERINFO_KEY = "USER_FOR_TRANSACTION_MANAGEMENT";

	/**
	 * Keys used to disable stamping in single EditingContexts (can be useful for stuff like bulk processing)
	 */
	private static final String DISABLED_MARKER = "DISABLED" + TransactionStamper.class.getSimpleName();

	/**
	 * The transaction watcher is a singleton.
	 */
	private static TransactionStamper _instance;

	/**
	 * I'll make the instances here, thankyouverymuch.
	 */
	private TransactionStamper() {}

	/**
	 * Singleton.
	 */
	private static TransactionStamper instance() {
		if( _instance == null ) {
			_instance = new TransactionStamper();
		}

		return _instance;
	}

	/**
	 * Start watching transactions.
	 */
	public static void register() {
		linkNotificationToMethod( EOEditingContext.ObjectsChangedInEditingContextNotification, "handleInsertion" );
		linkNotificationToMethod( ERXEC.EditingContextWillSaveChangesNotification, "handleModification" );
	}

	/**
	 * Register a method to invoke when a named notification is received.
	 */
	private static void linkNotificationToMethod( String notificationName, String method ) {
		NSNotificationCenter.defaultCenter().addObserver( instance(), ERXSelectorUtilities.notificationSelector( method ), notificationName, null );
	}

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

	/**
	 * Marks the given Editing context to disable any logging.
	 */
	public static void setUserInEditingContext( USUser user, EOEditingContext ec ) {
		ec.setUserInfoForKey( user, USER_USERINFO_KEY );
	}

	/**
	 * Marks the given Editing context to disable any logging.
	 */
	public static USUser userInEditingContext( EOEditingContext ec ) {
		return (USUser)ec.userInfoForKey( USER_USERINFO_KEY );
	}

	public void handleInsertion( NSNotification notification ) {
		EOEditingContext ec = (EOEditingContext)notification.object();

		if( !isDisabledInEditingContext( ec ) ) {
			NSDictionary userInfo = notification.userInfo();
			NSArray<EOEnterpriseObject> insertedObjects = (NSArray<EOEnterpriseObject>)userInfo.objectForKey( EOObjectStore.InsertedKey );

			USUser user = userInEditingContext( ec );

			for( EOEnterpriseObject eo : insertedObjects ) {
				if( eo instanceof TimeStampedCreation ) {
					((TimeStampedCreation)eo).setCreationDate( new NSTimestamp() );
				}

				if( eo instanceof TimeStampedModification ) {
					((TimeStampedModification)eo).setModificationDate( new NSTimestamp() );
				}

				if( user != null ) {
					if( eo instanceof UserStampedCreatedBy ) {
						((UserStampedCreatedBy)eo).setCreatedBy( user );
					}

					if( eo instanceof UserStampedModifiedBy ) {
						((UserStampedModifiedBy)eo).setModifiedBy( user );
					}
				}

				if( eo instanceof UUIDStamped ) {
					if( ((UUIDStamped)eo).uuid() == null ) {
						((UUIDStamped)eo).setUuid( java.util.UUID.randomUUID().toString() );
					}
				}

				if( eo instanceof UniqueIDStamped ) {
					if( ((UniqueIDStamped)eo).uniqueID() == null ) {
						((UniqueIDStamped)eo).setUniqueID( java.util.UUID.randomUUID().toString() );
					}
				}
			}
		}
	}

	public void handleModification( NSNotification notification ) {
		EOEditingContext ec = (EOEditingContext)notification.object();

		if( !isDisabledInEditingContext( ec ) ) {
			NSArray<EOEnterpriseObject> updatedObjects = ec.updatedObjects();

			USUser user = userInEditingContext( ec );

			for( EOEnterpriseObject eo : updatedObjects ) {
				if( eo instanceof TimeStampedModification ) {
					((TimeStampedModification)eo).setModificationDate( new NSTimestamp() );
				}

				if( eo instanceof UserStampedModifiedBy ) {
					if( user != null ) {
						((UserStampedModifiedBy)eo).setModifiedBy( user );
					}
				}
			}
		}
	}
}