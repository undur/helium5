package is.rebbi.wo.components.admin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator.ComparisonException;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXSession;
import er.extensions.components.ERXComponent;
import er.extensions.foundation.ERXProperties;
import is.rebbi.core.formatters.DurationFormatter;
import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.util.SessionManager;

public class USSystemInfo extends ERXComponent {

	private static final String PATH_SEPARATOR = ERXProperties.stringForKey( "path.separator" );
	private static final int psLength = PATH_SEPARATOR.length();

	public String currentPropertyKey;
	private final NSDictionary _properties = new NSDictionary( java.lang.System.getProperties() );

	public USSystemInfo( WOContext context ) {
		super( context );
	}

	public NSTimestamp now() {
		return new NSTimestamp();
	}

	/**
	 * @return All property keys in the application.
	 */
	public NSArray<String> propertyKeys() throws ComparisonException {
		return _properties.allKeys(); // FIXME: Sorting
	}

	/**
	 * @return Value of the property currently being iterated over.
	 */
	public Object currentPropertyValue() {
		return _properties.objectForKey( currentPropertyKey );
	}

	public String reportPath() {
		StringBuilder b = new StringBuilder();
		b.append( "\n" );
		b.append( reportPath( "java.home" ) );
		b.append( "\n" );
		b.append( "\n" );
		b.append( reportPath( "sun.boot.class.path" ) );
		b.append( "\n" );
		b.append( reportPath( "java.ext.dirs" ) );
		b.append( "\n" );
		b.append( reportPath( "sun.boot.library.path" ) );
		b.append( "\n" );
		b.append( reportPath( "user.dir" ) );
		b.append( "\n" );
		b.append( reportPath( "java.library.path" ) );
		b.append( "\n" );
		b.append( reportPath( "java.class.path" ) );
		b.append( "\n" );

		return StringUtilities.convertBreakString( b.toString() );
	}

	private static NSArray<URL> urlClassPath() {
		NSMutableArray<URL> result = new NSMutableArray<>();
		URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();

		for( URL url : classLoader.getURLs() ) {
			result.addObject( url );
		}

		return result.immutableClone();
	}

	private static String reportPath( NSArray<String> arr, String name ) {
		StringBuilder b = new StringBuilder();
		b.append( "<strong>" + name + "</strong>" );
		b.append( "\n" );
		Enumeration<String> en = arr.objectEnumerator();

		while( en.hasMoreElements() ) {
			b.append( en.nextElement() );
			b.append( "\n" );
		}

		return b.toString();
	}

	private static String reportPath( String systemProperty ) {
		NSArray<String> arr = ppp( systemProperty );
		return reportPath( arr, systemProperty );
	}

	private static NSArray<String> pp( String path ) {

		if( path == null ) {
			return NSArray.emptyArray();
		}

		NSMutableArray<String> result = new NSMutableArray<>();
		int oldloc = 0;
		int loc = 0;
		String found = null;
		int len = path.length();

		while( oldloc < len ) {
			loc = path.indexOf( PATH_SEPARATOR, oldloc );
			if( -1 == loc ) {
				loc = len;
			}
			found = path.substring( oldloc, loc );
			result.addObject( found );
			oldloc = loc + psLength;
		}

		return result.immutableClone();
	}

	private static NSArray<String> ppp( String systemProperty ) {
		return pp( ERXProperties.stringForKey( systemProperty ) );
	}

	public DurationFormatter durationFormatter() {
		return new DurationFormatter();
	}

	public NSArray<ERXSession> activeUserSessions() {
		return SessionManager.singleton().activeSessions();
	}
}