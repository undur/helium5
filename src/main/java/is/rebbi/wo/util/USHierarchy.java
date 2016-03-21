package is.rebbi.wo.util;

import com.webobjects.foundation.NSArray;

import is.rebbi.core.util.Hierarchy;

/**
 * USHierarchy can be implemented by any class to take advantage of the utility methods in USHierarcyUtilities.
 */

public interface USHierarchy<E extends USHierarchy<E>> extends Hierarchy<E> {

	/**
	 * This node's parent node
	 */
	@Override
	public E parent();

	/**
	 * This node's child nodes
	 */
	@Override
	public NSArray<E> children();
}