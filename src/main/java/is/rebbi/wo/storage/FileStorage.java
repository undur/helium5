package is.rebbi.wo.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;

import er.extensions.eof.ERXGenericRecord;
import is.rebbi.wo.util.SWSettings;

public class FileStorage extends Storage {

	private static String _documentPath;

	public FileStorage() {}

	public FileStorage( String documentPath ) {
		_documentPath = documentPath;
	}

	private static String documentPath() {
		if( _documentPath == null ) {
			_documentPath = SWSettings.documentPath();
		}

		return _documentPath;
	}

	/**
	 * @return The file on disk
	 */
	public File file( Object document ) {

		File file = null;
		String path = null;

		String primaryKey;

		if( document instanceof ERXGenericRecord ) {
			primaryKey = ((ERXGenericRecord)document).primaryKey();
		}
		else if( document instanceof CayenneDataObject ) {
			primaryKey = String.valueOf( Cayenne.longPKForObject( ((CayenneDataObject)document) ) );
		}
		else {
			throw new IllegalArgumentException( "FileStorage only supports ERXGenericRecord and CayenneDataObject. Class is: " + document.getClass() );
		}

		if( primaryKey != null ) {
			path = documentPath() + "/" + primaryKey;
			file = new File( path );
		}

		String uuid = uuid( document );

		if( uuid != null && (file == null || !file.exists()) ) {
			path = documentPath() + "/" + uuid;
			file = new File( path );
		}

		return file;
	}

	private static String uuid( Object object ) {
		try {
			Method uuidMethod = object.getClass().getMethod( "uuid", new Class<?>[] {} );
			return (String)uuidMethod.invoke( object, new Object[] {} );
		}
		catch( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			return null;
		}
	}

	@Override
	public void deleteData( Object document ) {
		File f = file( document );

		if( f != null && f.exists() ) {
			f.delete();
		}
	}

	@Override
	public boolean hasData( Object document ) {
		File f = file( document );
		return f != null && f.exists() && f.length() > 0;
	}

	@Override
	public long sizeOfData( Object document ) {
		File f = file( document );

		if( f.exists() ) {
			return f.length();
		}

		return 0;
	}

	@Override
	public InputStream in( Object document ) {
		try {
			File f = file( document );

			if( !f.exists() ) {
				f.createNewFile();
			}

			return new FileInputStream( f );
		}
		catch( IOException e ) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public OutputStream out( Object document ) {
		try {
			File f = file( document );

			if( !f.exists() ) {
				f.createNewFile();
			}

			return new FileOutputStream( f );
		}
		catch( IOException e ) {
			throw new RuntimeException( e );
		}
	}
}