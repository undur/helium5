package is.rebbi.wo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.foundation.NSTimestamp;

import is.rebbi.wo.search.IndexManager;
import is.rebbi.wo.util.SessionManager;
import is.rebbi.wo.util.SoftUser;

public class Primary {

	private static final Logger logger = LoggerFactory.getLogger( Primary.class );
	private static NSTimestamp _startupTime;

	static {
		logger.info( "Initializing Helium" );
		_startupTime = new NSTimestamp();
		SoftUser.Manager.register();
		SessionManager.register();
		IndexManager.register();
	}

	public static String frameworkBundleName() {
		return "helium";
	}

	public static NSTimestamp startupTime() {
		return _startupTime;
	}
}