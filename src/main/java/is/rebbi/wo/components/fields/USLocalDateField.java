package is.rebbi.wo.components.fields;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.ajax.AjaxUtils;
import is.rebbi.wo.Primary;
import is.rebbi.wo.components.USBaseComponent;

/**
 * A field for entering a date without a time.
 */

public class USLocalDateField extends USBaseComponent {

	private static final DateTimeFormatter DATE_TIME_FORMATTER_WITHOUT_TIME = DateTimeFormatter.ofPattern( "d.M.yyyy" );

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
		AjaxUtils.addStylesheetResourceInHead( context(), r, Primary.frameworkBundleName(), "smoothness/jquery-ui-1.8.22.custom.css" );
		AjaxUtils.addScriptResourceInHead( context(), r, Primary.frameworkBundleName(), "jquery-ui-1.8.22.custom.min.js" );
		AjaxUtils.addScriptResourceInHead( context(), r, Primary.frameworkBundleName(), "jquery.ui.datepicker-is.js" );
	}

	public String stringValue() {

		if( hasBinding("stringValue") ) {
			return stringValueForBinding( "stringValue" );
		}

		final LocalDate value = (LocalDate)valueForBinding( "value" );

		if( value == null ) {
			return null;
		}

		return DATE_TIME_FORMATTER_WITHOUT_TIME.format( value );
	}

	public void setStringValue( String value ) {

		if( hasBinding("stringValue") ) {
			setValueForBinding( value, "stringValue" );
		}
		else {
			TemporalAccessor localDate;

			if( value != null ) {
				localDate = LocalDate.parse( value, DATE_TIME_FORMATTER_WITHOUT_TIME );
			}
			else {
				localDate = null;
			}

			setValueForBinding( localDate, "value" );
		}
	}
}