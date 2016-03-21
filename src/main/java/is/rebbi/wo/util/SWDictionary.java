package is.rebbi.wo.util;

import java.io.File;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;

import is.rebbi.core.util.StringUtilities;

/**
 * SWDictionary is a simple key-value store that allows synchronization with a file (in ASCII-plist format)
 */

public class SWDictionary<E, T> implements NSKeyValueCodingAdditions {

	/**
	 * Stores the values
	 */
	private NSMutableDictionary<E, T> _storage = new NSMutableDictionary<>();

	/**
	 * This file is always kept synchronized with the contents of the dictionary.
	 */
	private File _file;

	/**
	 * Initializes an empty SWDictionary
	 */
	public SWDictionary() {}

	/**
	 * Initializes an SWDictionary using the specified File. If the file does not exist,
	 * an empty SWDictionary is created and the file is then written to disk.
	 *
	 * @param file the File to write to
	 */
	public SWDictionary( File file ) {
		setFile( file );
		read();
	}

	/**
	 * Initializes an SWDictionary from a standard ascii-plist String using NSPropertyListSerialization.
	 *
	 * @param string A standard ascii-plist string
	 */
	public SWDictionary( String string ) {
		if( string != null ) {
			setStorage( (NSMutableDictionary)NSPropertyListSerialization.propertyListFromString( string ) );
		}
	}

	/**
	 * @return Internal contents of the dictionary.
	 */
	private NSMutableDictionary<E, T> storage() {
		return _storage;
	}

	/**
	 * Set the storage object.
	 */
	private void setStorage( NSMutableDictionary<E, T> value ) {
		if( value == null ) {
			value = new NSMutableDictionary<>();
		}

		_storage = value;
		write();
	}

	/**
	 * Reads the File specified for this dictionary object
	 */
	private void read() {
		if( hasFile() ) {
			String s = StringUtilities.readStringFromFileUsingEncoding( file(), "UTF-8" );
			NSMutableDictionary<E, T> d = (NSMutableDictionary)NSPropertyListSerialization.propertyListFromString( s );

			if( d != null ) {
				setStorage( d );
			}
		}
	}

	/**
	 * Writes the dictionary's contents to the dictionary's file
	 */
	public void write() {
		if( hasFile() ) {
			String s = NSPropertyListSerialization.stringFromPropertyList( storage() );

			if( s != null ) {
				StringUtilities.writeStringToFileUsingEncoding( s, file(), "UTF-8" );
			}
		}
	}

	/**
	 * @return A file kept in sync with the object's contents.
	 */
	private File file() {
		return _file;
	}

	/**
	 * Sets the file for this dictionary. This file is always kept in sync with the dictionary's contents.
	 *
	 * @param value The file to keep in sync with the dictionary
	 */
	private void setFile( File value ) {
		_file = value;
	}

	/**
	 * @return True if this has a file.
	 */
	public boolean hasFile() {
		return file() != null;
	}

	/**
	 * Standard implementation of this NSKeyValueCoding method
	 */
	@Override
	public Object valueForKey( String key ) {

		Object value = storage().valueForKey( key );

		if( value instanceof NSDictionary && !(value instanceof NSMutableDictionary) ) {
			value = ((NSDictionary)value).mutableClone();
		}

		return value;
	}

	@Override
	public void takeValueForKey( Object value, String key ) {
		storage().takeValueForKey( value, key );
		write();
	}

	@Override
	public Object valueForKeyPath( String keyPath ) {
		return storage().valueForKeyPath( keyPath );
	}

	@Override
	public void takeValueForKeyPath( Object value, String keyPath ) {
		storage().takeValueForKeyPath( value, keyPath );
		write();
	}

	/**
	 * Displays the contents of the SWDictionary as a String
	 */
	@Override
	public String toString() {
		return storage().toString();
	}
}