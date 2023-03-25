package is.rebbi.wo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.rebbi.wo.util.SessionManager;
import is.rebbi.wo.util.SoftUser;

public class Primary {

	private static final Logger logger = LoggerFactory.getLogger( Primary.class );

	static {
		System.out.println( "==== Initializing Helium ====" );
		logger.info( "Initializing Helium" );
		SoftUser.Manager.register();
		SessionManager.register();
	}

	public static String frameworkBundleName() {
		return "helium";
	}
}