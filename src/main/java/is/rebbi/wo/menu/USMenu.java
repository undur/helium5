package is.rebbi.wo.menu;

import java.util.ArrayList;
import java.util.List;

import is.rebbi.wo.Primary;

public class USMenu {

	/**
	 * Menu items in the root of the menu.
	 */
	private List<USMenuItem> _rootItems = new ArrayList<>();

	/**
	 * Default menu
	 */
	private static USMenu _defaultMenu;

	public List<USMenuItem> rootItems() {
		return _rootItems;
	}

	public static USMenu defaultMenu() {
		if( _defaultMenu == null ) {
			_defaultMenu = new USMenu();
		}

		return _defaultMenu;
	}

	public USMenuItem addChild( final USMenuItem item ) {
		rootItems().add( item );
		return item;
	}

	@Deprecated
	public void addDatabaseMenuItem() {
		addChild( Primary.databaseMenuItem() );
	}

	@Deprecated
	public void addSystemMenuItem() {
		addChild( Primary.systemMenuItem() );
	}
}