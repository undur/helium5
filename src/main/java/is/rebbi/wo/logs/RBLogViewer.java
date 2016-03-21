package is.rebbi.wo.logs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.components.ERXComponent;
import er.extensions.foundation.ERXArrayUtilities;

/**
 * A component for viewing log files.
 */

public class RBLogViewer extends ERXComponent {

	/**
	 * The component to return to.
	 */
	public WOComponent callingComponent;

	/**
	 * Path of the currently selected file.
	 */
	private File _file;

	/**
	 * Lines currently shown in the GUI.
	 */
	public NSArray<Line> lines;

	/**
	 * Current iteration in the repetition
	 */
	public int currentIndex;

	/**
	 * Line currently being iterated over in the GUI.
	 */
	public Line currentLine;

	/**
	 * GUI input variables
	 */
	public String encoding = "utf-8";
	public Integer startLine;
	public Integer endLine;
	public String filter;
	public boolean reverseLines = false;
	public boolean showLineNumbers = true;

	private int oldLineCount = 0;
	private int numberOfNewLines = 0;

	public RBLogViewer( WOContext context ) {
		super( context );
	}

	@Override
	protected boolean useDefaultComponentCSS() {
		return true;
	}

	public File file() {
		return _file;
	}

	public void setFile( File value ) {
		_file = value;

		int numberOfLines = numberOfLines();

		if( numberOfLines > 2000 ) {
			startLine = numberOfLines - 2000;
		}

		read();
	}

	/**
	 * Perform the actual reading of the files, and calculate how many new lines we've got.
	 */
	public WOActionResults read() {
		lines = linesFromFile( file(), encoding, filter, startLine, endLine, reverseLines );
		numberOfNewLines = lines.count() - oldLineCount;
		oldLineCount = lines.count();
		return null;
	}

	public String lineClass() {
		if( reverseLines ) {
			if( currentIndex < numberOfNewLines ) {
				return "lineNumber newLine";
			}
		}
		else {
			if( currentIndex + 1 > (lines.count() - numberOfNewLines) ) {
				return "lineNumber newLine";
			}
		}

		return "lineNumber";
	}

	/**
	 * @return The page that invoked this component.
	 */
	public WOActionResults returnToCallingComponent() {
		callingComponent.ensureAwakeInContext( context() );
		return callingComponent;
	}

	/**
	 * the number of lines in the given document.
	 */
	public int numberOfLines() {
		return countLines( file() );
	}

	/**
	 * Return the specified line numbers from the given file.
	 */
	private static NSArray<Line> linesFromFile( File file, String encoding, String filter, Integer firstLine, Integer lastLine, boolean reverse ) {

		if( firstLine == null ) {
			firstLine = 1;
		}

		if( lastLine == null ) {
			lastLine = Integer.MAX_VALUE;
		}

		NSMutableArray<Line> lines = new NSMutableArray<Line>();

		try {
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ), encoding ) );
			String line;

			int currentIndex = 1;

			while( (line = in.readLine()) != null && currentIndex <= lastLine ) {
				if( currentIndex >= firstLine && currentIndex <= lastLine ) {
					if( filter != null ) {
						if( line.matches( filter ) ) {
							lines.addObject( new Line( currentIndex, line ) );
						}
					}
					else {
						lines.addObject( new Line( currentIndex, line ) );
					}
				}
				currentIndex++;
			}

			in.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		if( reverse ) {
			return ERXArrayUtilities.reverse( lines );
		}

		return lines;
	}

	/**
	 * Count the number of lines in the given file.
	 */
	private static int countLines( File file ) {
		InputStream is = null;
		try {
			is = new BufferedInputStream( new FileInputStream( file ) );
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			while( (readChars = is.read( c )) != -1 ) {
				for( int i = 0; i < readChars; ++i ) {
					if( c[i] == '\n' ) {
						++count;
					}
				}
			}
			return count;
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	public static class Line {
		public int number;
		public String text;

		public Line( int newNumber, String newText ) {
			number = newNumber;
			text = newText;
		}
	}
}