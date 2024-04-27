package is.rebbi.wo.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOSession;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

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

	public void sessionDidRestore( final NSNotification notification ) {
		ERXSession session = (ERXSession)notification.object();
		addSessionIfMissing( session );
	}

	public void sessionDidCreate( final NSNotification notification ) {
		ERXSession session = (ERXSession)notification.object();
		addSessionIfMissing( session );
	}

	public void sessionDidTimeOut( final NSNotification notification ) {
		String sessionID = (String)notification.object();

		if( sessionID != null ) {
			activeSessions().remove( sessionID );
		}
	}

	public void addSessionIfMissing( final ERXSession session ) {
		addSessionIfMissing( session, null );
	}

	public void addSessionIfMissing( final ERXSession session, WORequest request ) {
		//		Objects.requireNonNull( session );

		if( session != null ) { // FIXME: This check should not be required

			// FIXME: This whole thing feels very hacky
			if( request == null ) {
				final WOContext context = session.context();

				if( context != null ) {
					request = context.request();
				}
			}
			setSessionLastTouchedDate( session );

			final String sessionID = session.sessionID();

			if( !activeSessions().containsKey( sessionID ) ) {
				activeSessions().put( sessionID, session );
				shortenTimeoutIfRobotSessionFromBrowser( session );

				if( request != null ) {
					final String ipAddress = USHTTPUtilities.ipAddressFromRequest( request );

					if( ipAddress != null ) {
						setSessionIPAddress( session, ipAddress );
					}

					final String userAgent = USHTTPUtilities.userAgent( request );

					if( userAgent != null ) {
						setSessionUserAgent( session, userAgent );
						shortenTimeoutIfRobotSessionFromUserAgent( session, userAgent );
					}
				}
			}
		}
	}

	private void setSessionLastTouchedDate( final ERXSession session ) {
		session.objectStore().takeValueForKey( LocalDateTime.now(), "lastTouchedDate" );
	}

	private void setSessionIPAddress( final ERXSession session, final String ipAddress ) {
		session.objectStore().takeValueForKey( ipAddress, "lastIPAddress" );
	}

	private void setSessionUserAgent( final ERXSession session, final String userAgent ) {
		session.objectStore().takeValueForKey( userAgent, "lastUserAgent" );
	}

	private static final List<String> SHORTENED_USER_AGENTS = List.of(
			"applebot",
			"googleother",
			"gptbot",
			"bingbot" );

	private void shortenTimeoutIfRobotSessionFromUserAgent( final ERXSession session, final String userAgent ) {
		boolean isRobot = false;

		for( String agentString : SHORTENED_USER_AGENTS ) {
			if( userAgent.toLowerCase().contains( agentString ) ) {
				isRobot = true;
			}
		}

		if( isRobot ) {
			session.setTimeOut( 30 );
		}
	}

	private void shortenTimeoutIfRobotSessionFromBrowser( final ERXSession session ) {
		ERXBrowser browser = session.browser();

		if( browser != null ) {
			if( browser.isRobot() ) {
				session.setTimeOut( 30 );
			}
		}
	}
}