package is.rebbi.wo.logs;

import java.io.File;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;

/**
 * A file system browsing component
 */

public class RBFileBrowser extends ERXComponent {

	/**
	 * The sort orderings of the files.
	 */
	private static final NSArray<EOSortOrdering> FILE_SORT_ORDERINGS = new NSArray<>( new EOSortOrdering[] { new EOSortOrdering( "isDirectory", EOSortOrdering.CompareAscending ), new EOSortOrdering( "name", EOSortOrdering.CompareCaseInsensitiveAscending ) } );

	/**
	 * Currently selected directory.
	 */
	private File _selectedFile;

	/**
	 * File being iterated over in the list view.
	 */
	public File currentFile;

	/**
	 * Indicate if the user is allowed to browse through the file system.
	 */
	public boolean allowBrowsing;

	/**
	 * Indicates if we want to calculate directory sizes.
	 */
	public boolean calculateDirectorySizes = false;

	public RBFileBrowser( WOContext context ) {
		super( context );
	}

	public File selectedFile() {
		if( _selectedFile == null ) {
			_selectedFile = new File( "/var/log" );
		}

		return _selectedFile;
	}

	public void setSelectedFile( File value ) {
		_selectedFile = value;
	}

	/**
	 * @return Rough size of file in human readable format.
	 */
	public String displaySize() {

		long length = currentFile.length();

		if( currentFile.isDirectory() && calculateDirectorySizes ) {
			length = RBFileUtils.sizeOfDirectory( currentFile );
		}

		if( length < 1000 ) {
			return length + "";
		}

		if( length < 1000000 ) {
			return length / 1000 + " KB";
		}

		if( length < 1000000000 ) {
			return length / 1000000 + " MB";
		}

		return length / 1000000000 + " GB";
	}

	/**
	 * @return If we want to show the current size of
	 */
	public boolean showCurrentSize() {
		return !currentFile.isDirectory() || (currentFile.isDirectory() && calculateDirectorySizes);
	}

	/**
	 * @return The list of files in the current directory.
	 */
	public NSArray<File> files() {
		NSArray<File> files = new NSArray<>( selectedFile().listFiles() );
		return EOSortOrdering.sortedArrayUsingKeyOrderArray( files, FILE_SORT_ORDERINGS );
	}

	/**
	 * Select a directory from the list.
	 */
	public WOActionResults selectFile() {
		if( currentFile.isDirectory() ) {
			setSelectedFile( currentFile );
			return null;
		}
		else {
			RBLogViewer nextPage = pageWithName( RBLogViewer.class );
			nextPage.setFile( currentFile );
			nextPage.callingComponent = context().page();
			return nextPage;
		}
	}

	/**
	 * Select the parent directory.
	 */
	public WOActionResults upOneLevel() {
		setSelectedFile( selectedFile().getParentFile() );
		return null;
	}

	/**
	 * @return The name of the icon to show besides the current file.
	 */
	public String currentIconName() {
		return currentFile.isDirectory() ? "folder.gif" : "document.gif";
	}

	public String currentIconSRC() {
		return ERXApplication.erxApplication().resourceManager().urlForResourceNamed( currentIconName(), "helium", null, context().request() );
	}

	@Override
	public String path() {
		return selectedFile().getAbsolutePath();
	}

	public void setPath( String value ) {
		if( !value.equals( selectedFile().getAbsolutePath() ) ) {
			setSelectedFile( new File( value ) );
		}
	}

	/**
	 * @return The lastModified date of the current file.
	 */
	public NSTimestamp lastModified() {
		return new NSTimestamp( currentFile.lastModified() );
	}
}