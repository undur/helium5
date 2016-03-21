package is.rebbi.wo.components.fields;

import java.text.SimpleDateFormat;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.ajax.AjaxUtils;
import is.rebbi.wo.components.USBaseComponent;

/**
 * A field for entering a date without a time.
 */

public class USDateField extends USBaseComponent {

	public USDateField(WOContext context) {
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

		AjaxUtils.addStylesheetResourceInHead( context(), r, "USWebObjects", "smoothness/jquery-ui-1.8.22.custom.css" );
		AjaxUtils.addScriptResourceInHead( context(), r, "USWebObjects", "jquery-ui-1.8.22.custom.min.js" );
		AjaxUtils.addScriptResourceInHead( context(), r, "USWebObjects", "jquery.ui.datepicker-is.js" );
	}

	public SimpleDateFormat formatter() {
		return new SimpleDateFormat( "dd.MM.yyyy" );
	}
}