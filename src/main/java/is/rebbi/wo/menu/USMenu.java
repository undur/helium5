package is.rebbi.wo.menu;

import java.util.ArrayList;
import java.util.List;

public class USMenu {

	/**
	 * Menu items in the root of the menu.
	 */
	private List<USMenuItem> _rootItems = new ArrayList<>();

	public List<USMenuItem> rootItems() {
		return _rootItems;
	}

	public USMenuItem addChild( final USMenuItem item ) {
		rootItems().add( item );
		return item;
	}
}