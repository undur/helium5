package is.rebbi.wo.menu;

import com.webobjects.appserver.WOActionResults;

public class USMenuItemContainer extends USMenuItem {

	@Override
	public WOActionResults action() {
		throw new RuntimeException( "Action should never be invoked on a container item" );
	}

	public static USMenuItemContainer create( final String name, final String iconClasses ) {
		USMenuItemContainer item = new USMenuItemContainer();
		item.setName( name );
		item.setIconClasses( iconClasses );
		return item;
	}
}