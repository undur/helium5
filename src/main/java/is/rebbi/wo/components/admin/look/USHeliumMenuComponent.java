package is.rebbi.wo.components.admin.look;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXSession;
import er.extensions.components.ERXNonSynchronizingComponent;
import is.rebbi.core.util.HierarchyUtilities;
import is.rebbi.wo.menu.USMenuItem;

public class USHeliumMenuComponent extends ERXNonSynchronizingComponent {

	public USMenuItem currentMenuItem;

	public USHeliumMenuComponent( WOContext context ) {
		super( context );
	}

	public List<USMenuItem> items() {
		return (List<USMenuItem>)valueForBinding( "items" );
	}

	public int previousLevel() {
		return intValueForBinding( "previousLevel", 0 );
	}

	public int level() {
		return previousLevel() + 1;
	}

	public String rootClass() {
		return stringValueForBinding( "rootClass" );
	}

	public String currentMenuItemClass() {
		StringBuilder b = new StringBuilder();
		b.append( currentMenuItem.iconClasses() );
		b.append( " sidebar-nav-icon" );
		return b.toString();
	}

	public boolean noItemClass() {
		return StringUtils.isEmpty( currentMenuItemClass() );
	}

	public String sidebarLinkClass() {
		StringBuilder b = new StringBuilder();

		if( level() == 1 ) {
			b.append( "sidebar-nav-menu" );
		}
		else {
			b.append( "sidebar-nav-submenu" );
		}

		return b.toString();
	}

	public String actionLinkClass() {
		boolean equals = currentMenuItem.equals( selectedMenuItem() );
		return equals ? "active" : null;
	}

	public WOActionResults action() {
		setSelectedMenuItem( currentMenuItem );
		return currentMenuItem.action();
	}

	private void setSelectedMenuItem( Object currentMenuItem2 ) {
		((ERXSession)session()).objectStore().takeValueForKey( currentMenuItem, "selectedMenuItem" );
	}

	private USMenuItem selectedMenuItem() {
		return (USMenuItem)((ERXSession)session()).objectStore().valueForKey( "selectedMenuItem" );
	}

	private boolean isOpen() {
		return HierarchyUtilities.isParentNodeOfNode( currentMenuItem, selectedMenuItem(), true );
	}

	public String currentLIClass() {
		return isOpen() ? "open active" : null;
	}
}