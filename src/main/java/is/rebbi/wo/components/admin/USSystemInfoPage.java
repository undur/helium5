package is.rebbi.wo.components.admin;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.components.ERXComponent;
import er.extensions.foundation.ERXProperties;
import is.rebbi.core.util.StringUtilities;

public class USSystemInfoPage extends ERXComponent {

	private static final String PATH_SEPARATOR = ERXProperties.stringForKey( "path.separator" );
	private static final int psLength = PATH_SEPARATOR.length();

	public String currentPropertyKey;
	private final Properties _properties = java.lang.System.getProperties();

	public USSystemInfoPage( WOContext context ) {
		super( context );
	}

	public NSTimestamp now() {
		return new NSTimestamp();
	}

	/**
	 * @return All property keys in the application.
	 */
	public List<String> propertyKeys() {
		final ArrayList<String> keys = new ArrayList<>();

		for( final Object key : _properties.keySet() ) {
			keys.add( (String)key );
		}

		Collections.sort( keys );

		return keys;
	}

	/**
	 * @return Value of the property currently being iterated over.
	 */
	public Object currentPropertyValue() {
		return _properties.get( currentPropertyKey );
	}

	public String reportPath() {
		StringBuilder b = new StringBuilder();
		b.append( "\n" );
		b.append( reportPath( "java.home" ) );
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

		return StringUtilities.replace( b.toString(), "\n", "<br />\n" );
	}

	private static String reportPath( List<String> arr, String name ) {
		StringBuilder b = new StringBuilder();
		b.append( "<strong>" + name + "</strong>" );
		b.append( "\n" );

		for( String str : arr ) {
			b.append( str );
			b.append( "\n" );
		}

		return b.toString();
	}

	private static String reportPath( String systemProperty ) {
		List<String> arr = ppp( systemProperty );
		return reportPath( arr, systemProperty );
	}

	private static List<String> pp( String path ) {

		if( path == null ) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>();
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
			result.add( found );
			oldloc = loc + psLength;
		}

		return result;
	}

	private static List<String> ppp( String systemProperty ) {
		return pp( ERXProperties.stringForKey( systemProperty ) );
	}

	public LocalDateTime startupTime() {
		long jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
		return LocalDateTime.ofInstant( Instant.ofEpochMilli( jvmStartTime ), ZoneId.systemDefault() );
	}

}