package is.rebbi.wo.util;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.mail.internet.InternetAddress;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;

import is.rebbi.core.util.StringUtilities;

/**
 * Various utility methods.
 */

public class USUtilities {

	private static final Logger logger = LoggerFactory.getLogger( USUtilities.class );

	private static final String TRUE_STRING = "true";
	private static final String FALSE_STRING = "false";

	/**
	 * No instances created, ever.
	 */
	private USUtilities() {}

	/**
	 * Finds out the value of Object and attempts to coerce it's value to a boolean.
	 *
	 * Returns true if:
	 * - it's a boolean with the value of true
	 * - it's string with value of "true" (case insensitive)
	 * - If it's an integer larger than zero.
	 */
	public static boolean booleanFromObject( Object o ) {

		if( isNull( o ) ) {
			return false;
		}

		if( o instanceof Boolean ) {
			return ((Boolean)o).booleanValue();
		}

		if( o instanceof String ) {
			String value = ((String)o).toLowerCase();

			if( value.equals( TRUE_STRING ) ) {
				return true;
			}

			if( value.equals( FALSE_STRING ) ) {
				return false;
			}
		}

		Long i = longFromObject( o );

		if( i == null ) {
			return false;
		}

		if( i.longValue() != 0 ) {
			return true;
		}

		return false;
	}

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
	 * Finds out the value of Object and attempts to coerce it's value to an Integer
	 */
	public static Long longFromObject( Object o ) {

		if( isNull( o ) ) {
			return null;
		}

		try {
			if( o instanceof Number ) {
				return ((Number)o).longValue();
			}

			if( o instanceof String ) {
				if( StringUtilities.isDigitsOnly( (String)o ) ) {
					return Long.valueOf( (String)o );
				}
			}
		}
		catch( Exception e ) {
			logger.warn( "Could not convert to Long from object: " + o, e );
		}

		return null;
	}

	/**
	 * Finds out the value of Object and attempts to coerce it's value to an Integer
	 */
	public static Double doubleFromObject( Object o ) {

		if( isNull( o ) ) {
			return null;
		}

		try {
			if( o instanceof Number ) {
				return ((Number)o).doubleValue();
			}
		}
		catch( Exception e ) {
			logger.warn( "Could not convert to double from object: " + o, e );
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
	 * Finds out the value of Object and attempts to coerce it's value to a String
	 */
	public static String stringFromObject( Object o ) {

		if( isNull( o ) ) {
			return null;
		}

		return o.toString();
	}

	/**
	 * Transforms a W3 Document to it's (xml) String representation.
	 */
	public static String convertDOMDocumentToString( Document doc ) throws TransformerException {
		DOMSource domSource = new DOMSource( doc );
		return convertDOMDocumentToString( domSource );
	}

	/**
	 * Transforms a W3 Node to it's (xml) String representation.
	 */
	public static String convertDOMDocumentToString( Node node ) throws TransformerException {
		DOMSource domSource = new DOMSource( node );
		return convertDOMDocumentToString( domSource );
	}

	/**
	 * Transforms a W3 DOMSource to it's (xml) String representation.
	 */
	public static String convertDOMDocumentToString( DOMSource domSource ) throws TransformerException {
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult( writer );
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform( domSource, result );
		return writer.toString();
	}

	/**
	 * Reads data from the named resource in the application bundle and converts it to a string using UTF-8.
	 */
	public static String stringFromResource( String resourceName ) {
		return stringFromResource( resourceName, null );
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
	public static String stringFromResource( String resourceName, String frameworkName, String language ) {
		return stringFromResource( resourceName, frameworkName, language, null );
	}

	/**
	 * Reads data from the named resource and converts it to a string using the given encoding.
	 * If no framework name is specified, reads from the "app" bundle by default.
	 */
	public static String stringFromResource( String resourceName, String frameworkName, String language, String encoding ) {

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

	/**
	 * @return If the given e-mail address is properly formatted.
	 */
	public static boolean validateEmailAddress( String emailAddress ) {
		boolean result = false;

		try {
			InternetAddress addr = new InternetAddress( emailAddress, true );
			result = true;
		}
		catch( Exception e ) {
			result = false;
		}

		if( !StringUtilities.validateEmailAddress( emailAddress ) ) {
			result = false;
		}

		return result;
	}
}