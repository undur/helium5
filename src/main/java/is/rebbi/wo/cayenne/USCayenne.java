package is.rebbi.wo.cayenne;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.configuration.server.ServerRuntimeBuilder;

import com.webobjects.appserver.WOSession;

import er.extensions.appserver.ERXSession;

public class USCayenne {

	/**
	 * The processes global ServerRuntime
	 */
	private static ServerRuntime _serverRuntime;

	/**
	 * Key used to store object context in the session's object store.
	 */
	private static final String OC_IDENTIFIER = "defaultObjectContext";

	/**
	 * ObjectContexts store the currently logged in user as a property using this key.
	 */
	private static final String OC_USER_KEY = "heliumUser";

	public static void setServerRuntime( ServerRuntime runtime ) {
		_serverRuntime = runtime;
	}

	public static ServerRuntime serverRuntime() {
		if( _serverRuntime == null ) {
			setServerRuntime( new ServerRuntimeBuilder().build() );
		}

		return _serverRuntime;
	}

	public static ObjectContext newContext() {
		return serverRuntime().newContext();
	}

	public static boolean isActive() {
		return serverRuntime() != null;
	}

	public static ObjectContext defaultObjectContext( WOSession session ) {
		ObjectContext oc = (ObjectContext)((ERXSession)session).objectStore().valueForKey( OC_IDENTIFIER );

		if( oc == null ) {
			oc = USCayenne.newContext();
			((ERXSession)session).objectStore().takeValueForKey( oc, OC_IDENTIFIER );
		}

		return oc;
	}

	/**
	 * Set the user that owns the given object context. Actions performed within this context will be o
	 */
	public static void setUserInContext( DataObject user, ObjectContext oc ) {
		oc.setUserProperty( OC_USER_KEY, user );
	}

	/**
	 * @return The owning user of the given ObjectContext.
	 */
	public static DataObject userInContext( ObjectContext oc ) {
		return (DataObject)oc.getUserProperty( OC_USER_KEY );
	}
}