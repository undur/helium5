package is.rebbi.wo.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.webobjects.foundation.NSData;

/**
 * Classes that will provide data for SWDocument should inherit from this class;
 */

public abstract class Storage {

	public abstract InputStream in( Object document );

	public abstract OutputStream out( Object document );

	public abstract void deleteData( Object document );

	public abstract boolean hasData( Object document );

	public abstract long sizeOfData( Object document );

	public NSData fetchData( Object document ) {

		try( InputStream is = in( document ) ) {
			long length = sizeOfData( document );

			if( length > Integer.MAX_VALUE ) {
				throw new RuntimeException( "File is too large" );
			}

			byte[] bytes = new byte[(int)length];

			int offset = 0;
			int numRead = 0;

			while( offset < bytes.length && (numRead = is.read( bytes, offset, bytes.length - offset )) >= 0 ) {
				offset += numRead;
			}

			if( offset < bytes.length ) {
				throw new IOException( "Could not completely read file" );
			}

			is.close();
			return new NSData( bytes );
		}
		catch( IOException e ) {
			throw new RuntimeException( "Failed to read data", e );
		}
	}

	public void writeData( Object document, NSData data ) {
		byte[] bytes;

		if( data == null ) {
			bytes = new byte[0];
		}
		else {
			bytes = data.bytes();
		}

		try( OutputStream out = out( document ) ) {
			out.write( bytes );
		}
		catch( IOException e ) {
			throw new RuntimeException( "Failed to write data", e );
		}
	}
}