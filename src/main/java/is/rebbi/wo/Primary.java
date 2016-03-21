package is.rebbi.wo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.rebbi.wo.search.IndexManagerEOF;
import is.rebbi.wo.util.RequestManager;
import is.rebbi.wo.util.SessionManager;
import is.rebbi.wo.util.SoftUser;
import is.rebbi.wo.util.StatsManager;
import is.rebbi.wo.util.TransactionStamper;

//Smu
public class Primary {

	private static final Logger logger = LoggerFactory.getLogger( Primary.class );

	static {
		logger.info( "Initializing Helium" );
		TransactionStamper.register();
		SoftUser.Manager.register();
		SessionManager.register();
		StatsManager.register();
		RequestManager.register();
		IndexManagerEOF.register();
	}

	public static String frameworkBundleName() {
		return "helium";
	}
}