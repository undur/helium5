package is.rebbi.wo.util;

import java.util.List;

import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import is.rebbi.core.util.StringUtilities;

/**
 * Mime type utilities.
 */

public class FileTypes {

	private static NSArray<FileType> _types;
	private static NSArray<EOSortOrdering> SORT_ORDERINGS = new NSArray<>( new EOSortOrdering( "name", EOSortOrdering.CompareCaseInsensitiveAscending ) );

	public static final String APPLICATION_TYPE = "application";
	public static final String AUDIO_TYPE = "audio";
	public static final String IMAGE_TYPE = "image";
	public static final String TEXT_TYPE = "text";
	public static final String VIDEO_TYPE = "video";

	static {
		_types = new NSMutableArray<>();
		register( "PDF", APPLICATION_TYPE, "pdf", "pdf" );
		register( "Microsoft Word", APPLICATION_TYPE, "msword", "doc" );
		register( "Microsoft Powerpoint", APPLICATION_TYPE, "mspowerpoint", "ppt" );
		register( "Microsoft Word 2011", APPLICATION_TYPE, "vnd.openxmlformats-officedocument.wordprocessingml.document", "docx" );
		register( "Microsoft Powerpoint 2011", APPLICATION_TYPE, "vnd.openxmlformats-officedocument.presentationml.presentation", "pptx" );
		register( "Microsoft Excel", APPLICATION_TYPE, "excel", "xls" );
		register( "Microsoft Excel 2011", APPLICATION_TYPE, "vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx" );
		register( "xltx", APPLICATION_TYPE, "vnd.openxmlformats-officedocument.spreadsheetml.template", "xltx" );
		register( "potx", APPLICATION_TYPE, "application/vnd.openxmlformats-officedocument.presentationml.template", "potx" );
		register( "ppsx", APPLICATION_TYPE, "application/vnd.openxmlformats-officedocument.presentationml.slideshow", "ppsx" );
		register( "sldx", APPLICATION_TYPE, "application/vnd.openxmlformats-officedocument.presentationml.slide", "sldx" );
		register( "dotx", APPLICATION_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.template", "dotx" );
		register( "xlam", APPLICATION_TYPE, "application/vnd.ms-excel.addin.macroEnabled.12", "xlam" );
		register( "xlsb", APPLICATION_TYPE, "application/vnd.ms-excel.sheet.binary.macroEnabled.12", "xlsb" );
		register( "Adobe Illustrator", APPLICATION_TYPE, "postscript", "ai" );
		register( "Adobe Photoshop", APPLICATION_TYPE, "octet-stream", "psd" );
		register( "RTF-skjal", APPLICATION_TYPE, "rtf", "rtf" );
		register( "Þjöppuð gögn, tgz", APPLICATION_TYPE, "gnutar", "tgz" );
		register( "Þjöppuð gögn, zip", APPLICATION_TYPE, "zip", "zip" );
		register( "Þjöppuð gögn, rar", APPLICATION_TYPE, "x-rar-compressed", "rar" );
		register( "Flash", APPLICATION_TYPE, "x-shockwave-flash", "swf" );
		register( "Hljóð mpeg", AUDIO_TYPE, "mpeg", "mpga" );
		register( "Hljóð mp2", AUDIO_TYPE, "mpeg", "mp2" );
		register( "Hljóð mp3", AUDIO_TYPE, "mpeg", "mp3" );
		register( "Hljóð m4a", AUDIO_TYPE, "mp4", "m4a" );
		register( "Hljóð wav", AUDIO_TYPE, "wav", "wav" );
		register( "Hljóð aac", AUDIO_TYPE, "aac", "aac" );
		register( "Mynd gif", IMAGE_TYPE, "gif", "gif" );
		register( "Mynd jpeg", IMAGE_TYPE, "jpeg", "jpg;jpeg;jif" );
		register( "Mynd png", IMAGE_TYPE, "png", "png" );
		register( "Mynd postscript", IMAGE_TYPE, "postscript", "eps" );
		register( "Mynd tiff", IMAGE_TYPE, "tiff", "tif;tiff" );

		register( "Video avi", VIDEO_TYPE, "avi", "avi" );
		register( "Video flash", VIDEO_TYPE, "x-flv", "flv" );
		register( "Video mpeg", VIDEO_TYPE, "mpeg", "mpe;mpg;mpeg;mpv" );
		register( "Video m4v", VIDEO_TYPE, "mp4", "m4v;mp4" );
		register( "Video QuickTime", VIDEO_TYPE, "quicktime", "mov" );
		register( "Video Windows", VIDEO_TYPE, "x-ms-wmv", "wmv" );

		register( "HMTL-skjal", TEXT_TYPE, "html", "html" );
		register( "Textaskjal", TEXT_TYPE, "plain", "txt" );
		register( "XML-skjal", TEXT_TYPE, "xml", "xml" );
		_types = _types.immutableClone();
		_types = EOSortOrdering.sortedArrayUsingKeyOrderArray( _types, FileTypes.SORT_ORDERINGS );
	}

	/**
	 * Register a new type of file.
	 */
	private static void register( String name, String mimePre, String mimePost, String extensionsString ) {
		String mimeType = mimePre + "/" + mimePost;
		FileType t = new FileType( name, mimeType, extensionsString );
		NSMutableArray<FileType> a = _types.mutableClone();
		a.addObject( t );
		_types = a.immutableClone();
	}

	/**
	 * @return All registered types.
	 */
	public static List<FileType> types() {
		return _types;
	}

	/**
	 * @return A mimeType corresponding to the given extension
	 */
	public static String extensionForMimeType( String mimeType ) {

		if( mimeType == null ) {
			return null;
		}

		mimeType = mimeType.toLowerCase();

		for( FileType type : types() ) {
			if( mimeType.equals( type.mimeType() ) ) {
				return type.extension();
			}
		}

		return null;
	}

	/**
	 * @return the mimeType for the given extension.
	 */
	public static String mimeTypeForExtension( String extension ) {

		if( extension == null ) {
			return null;
		}

		for( FileType type : types() ) {
			if( type.hasExtension( extension ) ) {
				return type.mimeType();
			}
		}

		return null;
	}

	/**
	 * @return The mime type corresponding to the extension of the given filename.
	 */
	public static String mimeTypeFromFilename( String filename ) {
		String extension = extensionFromFilename( filename );
		return mimeTypeForExtension( extension );
	}

	/**
	 * @return The string after the last period in the string. Null, if the string is null or contains no period.
	 */
	public static String extensionFromFilename( String filename ) {
		if( StringUtilities.hasValue( filename ) ) {
			int positionOfLastDot = filename.lastIndexOf( "." );

			if( positionOfLastDot == 0 ) {
				return null;
			}

			if( positionOfLastDot > -1 ) {
				positionOfLastDot = positionOfLastDot + 1;

				if( positionOfLastDot < filename.length() ) {
					String result = filename.substring( positionOfLastDot, filename.length() );
					return result.toLowerCase();
				}
			}
		}

		return null;
	}

	/**
	 * @return The filename excluding the extension.
	 */
	public static String filenameByRemovingExtension( String oldFilename ) {
		String extension = extensionFromFilename( oldFilename );

		if( extension != null ) {
			int extensionLength = extension.length();
			int filenameLength = oldFilename.length() - extensionLength - 1;
			String filename = oldFilename.substring( 0, filenameLength );
			return filename;
		}

		return oldFilename;
	}

	/**
	 * @return FileType for extension.
	 */
	public static FileType fromExtension( String extension ) {

		for( FileType t : types() ) {
			if( t.hasExtension( extension ) ) {
				return t;
			}
		}

		return null;
	}
}