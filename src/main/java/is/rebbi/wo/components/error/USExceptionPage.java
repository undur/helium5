package is.rebbi.wo.components.error;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.woextensions.WOExceptionParser;
import com.webobjects.woextensions.WOParsedErrorLine;

import er.ajax.AjaxUtils;
import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.Primary;

/**
 * A little nicer version of WOExceptionPage.
 *
 * TODO: Allow clicking to display source in browser for files where it's available.
 * TODO: Allow clicking in browser to open source file and go to line in Eclipse.
 * TODO: Allow reading of sources files that are not in bundles, but in JAR files.
 * TODO: Decompile sources if no source file available.
 */

public class USExceptionPage extends ERXComponent {

	private static final Logger logger = LoggerFactory.getLogger( USExceptionPage.class );

	private static final int NUMBER_OF_LINES_BEFORE_ERROR_LINE = 7;
	private static final int NUMBER_OF_LINES_AFTER_ERROR_LINE = 7;

	/**
	 * The exception we're reporting.
	 */
	private Throwable _exception;

	/**
	 * Line of source file currently being iterated over in the view.
	 */
	public String currentSourceLine;

	/**
	 * Current index of the source line iteration.
	 */
	public int currentSourceLineIndex;

	/**
	 * WO class that parses the stack trace for us.
	 */
	public WOExceptionParser exceptionParser;

	/**
	 * Line of the stack trace currently being iterated over.
	 */
	public WOParsedErrorLine currentErrorLine;

	public USExceptionPage( WOContext aContext ) {
		super( aContext );
	}

	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );
		AjaxUtils.addStylesheetResourceInHead( c, r, Primary.frameworkBundleName(), "helium/css/bootstrap.min.css" );
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/vendor/bootstrap.min.js" );
	}

	/**
	 * @return First line of the stack trace, essentially the causing line.
	 */
	public WOParsedErrorLine firstLineOfTrace() {
		NSArray<WOParsedErrorLine> stackTrace = exceptionParser.stackTrace();

		if( stackTrace.isEmpty() ) {
			return null;
		}

		return stackTrace.get( 0 );
	}

	/**
	 * @return true if source should be shown.
	 */
	public boolean showSource() {
		return ERXApplication.isDevelopmentModeSafe() && sourceFileContainingError() != null;
	}

	/**
	 * @return The source file where the exception originated (from the last line of the stack trace).
	 */
	private Path sourceFileContainingError() {
		String nameOfThrowingClass = firstLineOfTrace().packageClassPath();
		NSBundle bundle = bundleForClassName( nameOfThrowingClass );

		if( bundle == null ) {
			return null;
		}

		String path = bundle.bundlePath() + "/Sources/" + nameOfThrowingClass.replace( ".", "/" ) + ".java";
		return Paths.get( path );
	}

	/**
	 * @return The source lines to view in the browser.
	 */
	public List<String> lines() {
		List<String> lines;

		try {
			lines = Files.readAllLines( sourceFileContainingError() );
		}
		catch( IOException e ) {
			logger.error( "Attempt to read source code from '{}' failed", sourceFileContainingError(), e );
			return new ArrayList<>();
		}

		int indexOfFirstLineToShow = firstLineOfTrace().line() - NUMBER_OF_LINES_BEFORE_ERROR_LINE;
		int indexOfLastLineToShow = firstLineOfTrace().line() + NUMBER_OF_LINES_AFTER_ERROR_LINE;

		if( indexOfFirstLineToShow < 0 ) {
			indexOfFirstLineToShow = 0;
		}

		if( indexOfLastLineToShow > lines.size() ) {
			indexOfLastLineToShow = lines.size();
		}

		return lines.subList( indexOfFirstLineToShow, indexOfLastLineToShow );
	}

	/**
	 * @return Actual number of source file line being iterated over in the view.
	 */
	public int currentActualLineNumber() {
		return firstLineOfTrace().line() - NUMBER_OF_LINES_BEFORE_ERROR_LINE + currentSourceLineIndex + 1;
	}

	/**
	 * @return CSS class for the current line of the source file (to show odd/even lines and highlight the error line)
	 */
	public String sourceLineClass() {
		NSMutableArray<String> cssClasses = new NSMutableArray<>();
		cssClasses.add( "src-line" );

		if( currentSourceLineIndex % 2 == 0 ) {
			cssClasses.add( "even-line" );
		}
		else {
			cssClasses.add( "odd-line" );
		}

		if( isLineContainingError() ) {
			cssClasses.add( "error-line" );
		}

		return cssClasses.componentsJoinedByString( " " );
	}

	/**
	 * @return true if the current line being iterated over is the line containining the error.
	 */
	private boolean isLineContainingError() {
		return currentSourceLineIndex == NUMBER_OF_LINES_BEFORE_ERROR_LINE - 1;
	}

	public Throwable exception() {
		return _exception;
	}

	public void setException( Throwable value ) {
		exceptionParser = new WOExceptionParser( value );
		_exception = value;
	}

	/**
	 * @return bundle of the class currently being iterated over in the UI (if any)
	 */
	public NSBundle currentBundle() {
		return bundleForClassName( currentErrorLine.packageClassPath() );
	}

	/**
	 * Provided for convenience, to use this exception page when overriding Application.reportException(). Like so:
	 *
	 * @Override
	 * public WOResponse reportException( Throwable exception, WOContext context, NSDictionary extraInfo ) {
	 *    return USExceptionPage.reportException( exception, context, extraInfo );
	 * }
	 */
	public static WOResponse reportException( Throwable exception, WOContext context, NSDictionary extraInfo ) {
		USExceptionPage nextPage = ERXApplication.erxApplication().pageWithName( USExceptionPage.class, context );
		nextPage.setException( exception );
		return nextPage.generateResponse();
	}

	/**
	 * @return The bundle containing the (fully qualified) named class. Null if class is not found or not contained in a bundle.
	 */
	private static NSBundle bundleForClassName( String fullyQualifiedClassName ) {
		Class<?> clazz;

		try {
			clazz = Class.forName( fullyQualifiedClassName );
		}
		catch( ClassNotFoundException e ) {
			return null;
		}

		return NSBundle.bundleForClass( clazz );
	}

	/**
	 * @return The CSS class of the current row in the stack trace table.
	 */
	public String currentRowClass() {
		if( NSBundle.mainBundle().equals( currentBundle() ) ) {
			return "success";
		}

		return null;
	}
}