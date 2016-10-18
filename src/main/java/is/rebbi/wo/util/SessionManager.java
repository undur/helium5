package is.rebbi.wo.util;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOSession;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXBrowser;
import er.extensions.appserver.ERXSession;

/**
 * Mark last activity of the session.
 */

public class SessionManager {

	private static SessionManager _singleton;

	/**
	 * It's a singleton.
	 */
	private SessionManager() {}

	/**
	 * List of all currently active sessions.
	 */
	private NSMutableArray<ERXSession> _activeSessions = new NSMutableArray<>();

	/**
	 * @return The session manager singleton.
	 */
	public static SessionManager singleton() {
		if( _singleton == null ) {
			_singleton = new SessionManager();
		}

		return _singleton;
	}

	/**
	 * Registers the transaction manager so it starts listening and watching transactions.
	 */
	public static void register() {
		NSSelector<SessionManager> sessionDidRestore = new NSSelector<>( "sessionDidRestore", new Class[] { NSNotification.class } );
		NSNotificationCenter.defaultCenter().addObserver( singleton(), sessionDidRestore, WOSession.SessionDidRestoreNotification, null );

		NSSelector<SessionManager> sessionDidCreate = new NSSelector<>( "sessionDidCreate", new Class[] { NSNotification.class } );
		NSNotificationCenter.defaultCenter().addObserver( singleton(), sessionDidCreate, WOSession.SessionDidCreateNotification, null );

		NSSelector<SessionManager> sessionDidTimeOut = new NSSelector<>( "sessionDidTimeOut", new Class[] { NSNotification.class } );
		NSNotificationCenter.defaultCenter().addObserver( singleton(), sessionDidTimeOut, WOSession.SessionDidTimeOutNotification, null );
	}

	/**
	 * @return A list of all active sessions.
	 */
	public NSMutableArray<ERXSession> activeSessions() {
		return _activeSessions;
	}

	public void sessionDidRestore( NSNotification notification ) {
		ERXSession session = (ERXSession)notification.object();
		addSessionIfMissing( session );
	}

	public void sessionDidCreate( NSNotification notification ) {
		ERXSession session = (ERXSession)notification.object();
		addSessionIfMissing( session );
	}

	public void sessionDidTimeOut( NSNotification notification ) {
		String sessionID = (String)notification.object();

		if( sessionID != null ) {
			for( int i = activeSessions().count(); i > 0; i-- ) {
				ERXSession session = activeSessions().objectAtIndex( i - 1 );

				if( session.sessionID().equals( sessionID ) ) {
					activeSessions().removeObject( session );
				}
			}
		}
	}

	public void addSessionIfMissing( ERXSession session ) {
		if( session != null ) {
			touchSession( session );

			if( !activeSessions().contains( session ) ) {
				activeSessions().addObject( session );

				WOContext context = session.context();

				if( context != null ) {
					String ipAddress = USHTTPUtilities.ipAddressFromRequest( context.request() );

					if( ipAddress != null ) {
						session.objectStore().takeValueForKey( ipAddress, "remoteHostAddress" );
					}
				}

				ERXBrowser browser = session.browser();

				if( browser != null ) {
					if( browser.isRobot() ) {
						session.setTimeOut( 300 );
					}
				}
			}
		}
	}

	private void touchSession( ERXSession session ) {
		session.objectStore().takeValueForKey( new NSTimestamp(), "lastTouchedDate" );
	}
}