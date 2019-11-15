package is.rebbi.wo.components.admin.look.adminlte;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXSession;
import er.extensions.components.ERXNonSynchronizingComponent;
import is.rebbi.core.util.HierarchyUtilities;
import is.rebbi.wo.menu.USMenuItem;

public class USAdminLTE2MenuComponent extends ERXNonSynchronizingComponent {

	public USMenuItem currentMenuItem;

	public USAdminLTE2MenuComponent( WOContext context ) {
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
		return b.toString();
	}

	public boolean noItemClass() {
		return StringUtils.isEmpty( currentMenuItemClass() );
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
		return currentMenuItem.forceOpen || HierarchyUtilities.isParentNodeOfNode( currentMenuItem, selectedMenuItem(), true );
	}

	public String currentLIClass() {
		List<String> classes = new ArrayList<>();

		if( isOpen() ) {
			classes.add( "open" );
			classes.add( "active" );
		}

		if( !currentMenuItem.children().isEmpty() ) {
			classes.add( "treeview" );
		}

		return String.join( " ", classes );
	}

	public String dataWidget() {
		if( rootClass().equals( "sidebar-menu" ) ) {
			return "tree";
		}

		return null;
	}
}