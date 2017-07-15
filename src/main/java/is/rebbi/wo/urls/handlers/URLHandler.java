package is.rebbi.wo.urls.handlers;

import java.util.function.BiFunction;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

public interface URLHandler {

	/**
	 * URL prefix to handle
	 */
	public String prefix();

	/**
	 * Defines a function that will be run when the button is clicked, passing the selectedObject if any.
	 */
	public BiFunction<String, WOContext, WOActionResults> execute();
}