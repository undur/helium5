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
	 * Name of icon shown in the UI (as specified by font-awesome)
	 */
	public String iconName();

	/**
	 * A function that decides if the operation should be shown to the user.
	 */
	public BiFunction<DataObject, WOContext, Boolean> show();

	/**
	 * Defines a function that will be run when the button is clicked, passing the selectedObject if any.
	 */
	public default BiFunction<DataObject, WOContext, WOActionResults> execute() {
		return null;
	}

	/**
	 * A function that generates the URL for the current operation.
	 */
	public default BiFunction<DataObject, WOContext, String> urlFunction() {
		return null;
	}
}