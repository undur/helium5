package is.rebbi.wo.util;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;

import is.rebbi.core.util.StringUtilities;

/**
 * Various utility methods.
 */

public class USUtilities {

	private static final Logger logger = LoggerFactory.getLogger( USUtilities.class );

	private USUtilities() {}

	/**
	 * Finds out the value of Object and attempts to coerce it's value to an Integer
	 */
	public static Integer integerFromObject( Object o ) {

		if( isNull( o ) ) {
			return null;
		}

		try {
			if( o instanceof Number ) {
				return ((Number)o).intValue();
			}

			if( o instanceof String ) {
				if( StringUtilities.isDigitsOnly( (String)o ) ) {
					return Integer.valueOf( ((String)o) );
				}
				else {
					Double d = Double.valueOf( (String)o );

					if( d != null ) {
						return d.intValue();
					}
				}
			}
		}
		catch( Exception e ) {
			logger.warn( "Could not convert to Integer from object: " + o, e );
		}

		return null;
	}

	/**
	 * @param object The object to check
	 * @return true if an object is null, or NSKeyValueCoding.NullValue.
	 */
	private static final boolean isNull( Object object ) {
		return (object == null) || (object instanceof NSKeyValueCoding.Null);
	}

	/**
	 * Reads data from the named resource and converts it to a string using UTF-8.
	 * If no framework name is specified, reads from the "app" bundle by default.
	 */
	public static String stringFromResource( String resourceName, String frameworkName ) {
		return stringFromResource( resourceName, frameworkName, null );
	}

	/**
	 * Reads data from the named resource and converts it to a string using UTF-8.
	 * If no framework name is specified, reads from the "app" bundle by default.
	 */
	private static String stringFromResource( String resourceName, String frameworkName, String language ) {
		return stringFromResource( resourceName, frameworkName, language, null );
	}

	/**
	 * Reads data from the named resource and converts it to a string using the given encoding.
	 * If no framework name is specified, reads from the "app" bundle by default.
	 */
	private static String stringFromResource( String resourceName, String frameworkName, String language, String encoding ) {

		if( frameworkName == null ) {
			frameworkName = "app";
		}

		NSArray<String> languages;

		if( language == null ) {
			languages = NSArray.emptyArray();
		}
		else {
			languages = new NSArray<>( language );
		}

		if( encoding == null ) {
			encoding = "UTF-8";
		}

		byte[] data = WOApplication.application().resourceManager().bytesForResourceNamed( resourceName, frameworkName, languages );

		if( data == null ) {
			return null;
		}

		if( data.length == 0 ) {
			return "";
		}

		String template = null;

		try {
			template = new String( data, encoding );
		}
		catch( UnsupportedEncodingException e ) {
			logger.debug( "Could not read string form resource", e );
		}

		return template;
	}
}