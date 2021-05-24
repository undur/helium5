package is.rebbi.wo.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOSession;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXSession;

/**
 * Mark last activity of the session.
 */

public class SessionManager {

	private static SessionManager _singleton;

	/**
	 * It's a singleton.
	 */
	private SessionManager() {
	}

	/**
	 * List of all currently active sessions.
	 */
	private Map<String, ERXSession> _activeSessions = new ConcurrentHashMap<>();

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
	public Map<String, ERXSession> activeSessions() {
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
			activeSessions().remove( sessionID );
		}
	}

	public void addSessionIfMissing( ERXSession session ) {
		if( session != null ) {
			touchSession( session );
			final String sessionID = session.sessionID();

			if( !activeSessions().containsKey( sessionID ) ) {
				activeSessions().put( sessionID, session );

				final WOContext context = session.context();

				if( context != null ) {
					final String ipAddress = USHTTPUtilities.ipAddressFromRequest( context.request() );

					if( ipAddress != null ) {
						session.objectStore().takeValueForKey( ipAddress, "remoteHostAddress" );
					}
				}
				//				FIXME: This is temporarily disabled due to package name discrepancies between Wonder and Slim
				//				ERXBrowser browser = session.browser();
				//
				//				if( browser != null ) {
				//					if( browser.isRobot() ) {
				//						session.setTimeOut( 300 );
				//					}
				//				}
			}
		}
	}

	private void touchSession( ERXSession session ) {
		session.objectStore().takeValueForKey( new NSTimestamp(), "lastTouchedDate" );
	}
}