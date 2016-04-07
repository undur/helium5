package is.rebbi.wo.cayenne;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.configuration.server.ServerRuntimeBuilder;

import com.webobjects.appserver.WOSession;

import er.extensions.appserver.ERXSession;

public class USCayenne {

	private static ServerRuntime _serverRuntime;

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

	/**
	 * Key used to store object context in the session's object store.
	 */
	private static final String OC_IDENTIFIER = "defaultObjectContext";

	public static ObjectContext defaultObjectContext( WOSession session ) {
		ObjectContext oc = (ObjectContext)((ERXSession)session).objectStore().valueForKey( OC_IDENTIFIER );

		if( oc == null ) {
			oc = USCayenne.newContext();
			((ERXSession)session).objectStore().takeValueForKey( oc, OC_IDENTIFIER );
		}

		return oc;
	}
}