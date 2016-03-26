package is.rebbi.wo.components;

import java.util.List;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.core.util.ListUtilities;

/**
 * A hierarchical repetition.
 *
 * Bindings:
 * [item] 		: The item currently being iterated over
 * [list] 		: An NSArray of root elements to display
 * [sublist] 	: The list of subelements to display. A List contained in the item - "item.someList"
 * [index] 		: index of the repetition in each level of the hierarchy.
 * [isOrdered]	: indicates if we want to generate an ordered or unordered list (ol or ul)
 */

public class USNestedList extends ERXStatelessComponent {

	public USNestedList(WOContext context) {
		super( context );
	}

	/**
	 * Binding indicating if the list should be ordered.
	 */
	private boolean isOrdered() {
		return booleanValueForBinding( "isOrdered" );
	}

	/**
	 * Sublist binding
	 */
	private List<?> sublist() {
		return (List<?>)valueForBinding( "sublist" );
	}

	/**
	 * Ordered or unordered.
	 */
	public String listTagName() {
		return isOrdered() ? "ol" : "ul";
	}

	/**
	 * Determines if the current node has a sublist
	 */
	public boolean hasSublist() {
		return ListUtilities.hasObjects( sublist() );
	}
}