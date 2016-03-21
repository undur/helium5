package is.rebbi.wo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.foundation.NSData;

/**
 * SWDataUtilities contains various utility methods for handling NSData objects and data streams,
 * reading and writing data to and from disks or over a network connection etc.
 */

public class USDataUtilities {

	private static Logger logger = LoggerFactory.getLogger( USDataUtilities.class );

	private USDataUtilities() {}

	/**
	 * This method reads a file and encapsulates it's contents in an NSData object
	 *
	 * @param sourceFile The file to read from
	 */
	public static NSData readDataFromFile( File sourceFile ) {

		NSData d = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream( sourceFile );
			d = new NSData( fis, 2048 );
		}
		catch( IOException exception ) {
			logger.debug( "Exception occurred", exception );
		}
		finally {
			try {
				fis.close();
			}
			catch( Exception e ) {}
		}

		return d;
	}

	/**
	 * This method writes the contents of an NSData object to a file
	 *
	 * @param destination The file to write to
	 */

	public static void writeDataToFile( NSData data, File destination ) {
		FileOutputStream fos = null;

		try {
			if( data != null ) {
				fos = new FileOutputStream( destination );
				data.writeToStream( fos );
			}
		}
		catch( Exception exception ) {
			logger.error( "Error occurred", exception );
		}
		finally {
			try {
				fos.close();
			}
			catch( Exception e ) {}
		}
	}

	/**
	 * @param sourceURL The url to read from
	 */
	public static NSData readDataFromURL( String sourceURL ) {
		try {
			return new NSData( new URL( sourceURL ) );
		}
		catch( Exception e ) {
			logger.error( "An exception occurred while reading data from URL: " + sourceURL, e );
			return null;
		}
	}

	/**
	 * Consume a stream and close it.
	 */
	public static byte[] consumeStream( InputStream stream ) {

		if( stream == null ) {
			return new byte[0];
		}

		try {
			return IOUtils.toByteArray( stream );
		}
		catch( IOException e ) {
			throw new RuntimeException( "Failed to read byte array", e );
		}
	}
}