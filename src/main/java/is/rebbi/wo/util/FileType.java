package is.rebbi.wo.util;

import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSArray;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXWOContext;

public class FileType {

	private String _name;
	private String _mimeType;
	private String[] _extensions;

	public FileType( String name, String mimeType, String extensionsString ) {
		_name = name;
		_mimeType = mimeType;
		_extensions = extensionsString.split( ";" );
	}

	/**
	 * A Descriptive name of this documentType, for example "Adobe PDF"
	 */
	public String name() {
		return _name;
	}

	/**
	 * Sets the descriptive name of this documentType
	 */
	public void setName( String value ) {
		_name = value;
	}

	/**
	 * The mimeType for this type of document
	 */
	public String mimeType() {
		return _mimeType;
	}

	public String[] extensions() {
		return _extensions;
	}

	/**
	 * @return Primary extension for this type of document
	 */
	public String extension() {
		return extensions()[0];
	}

	/**
	 * @return True if the given type has the given extension.
	 */
	public boolean hasExtension( String value ) {

		if( value == null ) {
			return false;
		}

		value = value.toLowerCase();

		for( String extension : extensions() ) {
			if( extension.equals( value ) ) {
				return true;
			}
		}

		return false;
	}

	public String iconURL() {
		return iconURLForExtension( extension() );
	}

	/**
	 * @return The name of the icon file for this document.
	 */
	public static String iconURLForExtension( String extension ) {
		String iconURL = null;
		String filename = null;

		if( extension != null ) {
			filename = "ext/" + extension + ".png";
		}
		else {
			filename = "ext/html.png";
		}

		//		WORequest request = ERXApplication.erxApplication().createRequest( "GET", "/", "HTTP/1.1", Collections.emptyMap(), NSData.EmptyData, NSDictionary.emptyDictionary() );
		WORequest request = ERXWOContext.currentContext().request();
		iconURL = ERXApplication.erxApplication().resourceManager().urlForResourceNamed( filename, "SoloWeb", NSArray.<String> emptyArray(), request );

		if( iconURL.contains( "NOT_FOUND" ) ) {
			filename = "ext/html.png";
			iconURL = ERXApplication.erxApplication().resourceManager().urlForResourceNamed( filename, "SoloWeb", NSArray.<String> emptyArray(), request );
		}

		return iconURL;
	}
}