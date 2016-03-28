package is.rebbi.wo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.rebbi.wo.search.IndexManager;
import is.rebbi.wo.util.SessionManager;
import is.rebbi.wo.util.SoftUser;
import is.rebbi.wo.util.StatsManager;

public class Primary {

	private static final Logger logger = LoggerFactory.getLogger( Primary.class );

	static {
		logger.info( "Initializing Helium" );
		SoftUser.Manager.register();
		SessionManager.register();
		StatsManager.register();
		IndexManager.register();
	}

	public static String frameworkBundleName() {
		return "helium";
	}
}