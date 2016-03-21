package is.rebbi.wo.components;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

import er.ajax.AjaxUtils;

/**
 * For adding tooltips to HTML elements.
 *
 * Bindings:
 *  value   : The text to display in the tooltip.
 *  noStyle : Disables styling of the tooltip (you'll usually want to set this to "true" if adding tooltips to anything other than a string).
 */

public class USTooltip extends USBaseComponent {

	public USTooltip( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	@Override
	public NSArray<String> additionalCSSFiles() {
		return new NSArray<String>( "USTooltip.css" );
	}

	@Override
	public NSArray<String> additionalJavascriptFiles() {
		return new NSArray<String>( "BubbleTooltips.js" );
	}

	@Override
	public void appendToResponse( WOResponse response, WOContext context ) {
		super.appendToResponse( response, context );
		AjaxUtils.addScriptResourceInHead( context, response, "Ajax", "prototype.js" );
	}

	/**
	 * @return The CSS class of the tooltip element.
	 */
	public String cssClass() {
		return noStyle() ? null : "tooltipLink";
	}

	/**
	 * @return The string value of the tooltip.
	 */
	public String value() {
		return stringValueForBinding( "value" );
	}

	/**
	 * @return Binding, you,ll probably want to set this to true if adding tips to elements other than strings.
	 */
	public boolean noStyle() {
		return booleanValueForBinding( "noStyle", false );
	}
}