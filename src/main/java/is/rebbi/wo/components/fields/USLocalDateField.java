package is.rebbi.wo.components.fields;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXResponseRewriter;
import is.rebbi.wo.Primary;
import is.rebbi.wo.components.USBaseComponent;

/**
 * A field for entering a date without a time.
 */

public class USLocalDateField extends USBaseComponent {

	public USLocalDateField( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	/**
	 * Returns a response, based on if the user is logged in or not.
	 */
	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );
		//		ERXResponseRewriter.addStylesheetResourceInHead( r, context(), Primary.frameworkBundleName(), "smoothness/jquery-ui-1.8.22.custom.css" );
		//		ERXResponseRewriter.addScriptResourceInHead( r, context(), Primary.frameworkBundleName(), "jquery-ui-1.8.22.custom.min.js" );
		//		ERXResponseRewriter.addScriptResourceInHead( r, context(), Primary.frameworkBundleName(), "jquery.ui.datepicker-is.js" );
		ERXResponseRewriter.addScriptResourceInHead( r, context(), Primary.frameworkBundleName(), "tabler/vendor/litepicker.js" );
	}

	public String stringValue() {

		if( hasBinding( "stringValue" ) ) {
			return stringValueForBinding( "stringValue" );
		}

		final LocalDate value = (LocalDate)valueForBinding( "value" );

		if( value == null ) {
			return null;
		}

		return StringDateParser.format( value );
	}

	public void setStringValue( String value ) {

		if( hasBinding( "stringValue" ) ) {
			setValueForBinding( value, "stringValue" );
		}
		else {
			final LocalDate localDate = StringDateParser.parse( value );
			setValueForBinding( localDate, "value" );
		}
	}

	private static class StringDateParser {

		private static final DateTimeFormatter DATE_TIME_FORMATTER_WITHOUT_TIME_WITHOUT_PERIODS = DateTimeFormatter.ofPattern( "ddMMyyyy" );
		private static final DateTimeFormatter DATE_TIME_FORMATTER_WITHOUT_TIME = DateTimeFormatter.ofPattern( "d.M.yyyy" );

		/**
		 * Parses the given date in several forms. Short form dates are assigned the current year.
		 */
		public static LocalDate parse( String dateString ) {

			if( dateString == null ) {
				return null;
			}

			LocalDate parsed = null;

			if( dateString.contains( "/" ) ) {
				dateString = dateString.replace( "/", "." );
			}

			if( dateString.contains( "." ) ) {

				// FIXME: We can be better
				final String[] splitString = dateString.split( "\\." );

				// Date is on the form 03.12 or 3.12
				if( splitString.length == 2 ) {
					dateString = dateString + "." + LocalDate.now().getYear();
				}

				// Date is on the form 03.12.20
				if( splitString.length == 3 && splitString[2].length() == 2 ) {
					dateString = splitString[0] + "." + splitString[1] + "." + (Integer.parseInt( splitString[2] ) + 2000);
				}

				// Finally parse date using form 03.12.2020
				parsed = LocalDate.parse( dateString, DATE_TIME_FORMATTER_WITHOUT_TIME );
			}
			else {
				// Date is on the form 0312
				if( dateString.length() == 4 ) {
					dateString = dateString + LocalDate.now().getYear();
				}

				// Date is on the form 031220
				if( dateString.length() == 6 ) {
					dateString = dateString.substring( 0, 4 ) + (Integer.parseInt( dateString.substring( 4, 6 ) ) + 2000);
				}

				// Finally parse date using form 03122020
				parsed = LocalDate.parse( dateString, DATE_TIME_FORMATTER_WITHOUT_TIME_WITHOUT_PERIODS );
			}

			return parsed;
		}

		public static String format( LocalDate date ) {

			if( date == null ) {
				return "";
			}

			return DATE_TIME_FORMATTER_WITHOUT_TIME.format( date );
		}
	}
}