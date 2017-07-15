package is.rebbi.wo.operations;

import java.util.function.BiFunction;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

public interface DataObjectOperation {

	/**
	 * Name of the operation (shown in the UI)
	 */
	public String name();

	/**
	 * Name of glyphicon to show.
	 */
	public String iconName();

	/**
	 * Defines a function that will be run when the button is clicked, passing
	 * the selectedObject if any.
	 */
	public BiFunction<DataObject, WOContext, WOActionResults> execute();

	/**
	 * A function that decides if the operation should be shown to the user.
	 */
	public BiFunction<DataObject, WOContext, Boolean> show();

	/**
	 * A function that generates the URL for the current operation.
	 */
	public default BiFunction<DataObject, WOContext, String> urlFunction() {
		return null;
	}
}