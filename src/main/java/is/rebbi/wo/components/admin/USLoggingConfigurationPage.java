package is.rebbi.wo.components.admin;

//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import org.apache.log4j.Appender;
//import org.apache.log4j.Level;
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//
//import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
//
import er.extensions.components.ERXComponent;

public class USLoggingConfigurationPage extends ERXComponent {

//	public Logger currentLogger;
//	public Level currentLevel;

	public USLoggingConfigurationPage( WOContext context ) {
		super( context );
	}

//	public List<Logger> loggers() {
//		final ArrayList list = Collections.list( LogManager.getCurrentLoggers() );
//		Collections.sort( list, Comparator.comparing( Logger::getName ) );
//		return list;
//	}
//
//	public List<Appender> currentAppenders() {
//		return Collections.list( currentLogger.getAllAppenders() );
//	}
//
//	public List<Level> logLevels() {
//		return Arrays.asList( Level.OFF, Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE, Level.ALL );
//	}
//
//	public WOActionResults defaultSettings() {
//		LogManager.resetConfiguration();
//		return null;
//	}
//
//	public WOActionResults selectLevel() {
//
//		if( currentLevel.equals( currentLogger.getLevel() ) ) {
//			currentLogger.setLevel( null );
//		}
//		else {
//			currentLogger.setLevel( currentLevel );
//		}
//
//		return null;
//	}
//
//	public String currentClass() {
//		String s = "btn btn-xs";
//
//		if( currentLevel.equals( currentLogger.getLevel() ) ) {
//			s = s + " btn-success";
//		}
//
//		if( currentLogger.getLevel() == null ) {
//			if( currentLevel.equals( currentLogger.getEffectiveLevel() ) ) {
//				s = s + " btn-default";
//			}
//		}
//
//		return s;
//	}
}