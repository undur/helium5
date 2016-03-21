package is.rebbi.wo.util;

/**
 * Objects can implement this class to attempt to present themselves in a human readable way.
 */

public interface HumanReadable {

	/**
	 * @return A human readable representation of this object.
	 */
	public String toStringHuman();
}