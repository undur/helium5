package is.rebbi.wo.util;

/**
 * Objects can implement this interface to use the generic sorting methods of USSortableUtilities.
 */

public interface USSortable {

	public static final int UP = -1;
	public static final int DOWN = 1;

	public Integer sortNumber();
	public void setSortNumber( Integer aValue );
}