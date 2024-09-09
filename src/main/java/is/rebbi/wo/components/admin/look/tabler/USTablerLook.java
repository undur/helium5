package is.rebbi.wo.components.admin.look.tabler;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

import er.extensions.appserver.ERXResponseRewriter;
import is.rebbi.wo.Primary;
import is.rebbi.wo.menu.USMenu;
import is.rebbi.wo.menu.USMenuItem;

public class USTablerLook extends WOComponent {

	public Object selectedObject;
	public String searchString;

	public USMenuItem menuItem;
	public USMenuItem subItem;

	public USTablerLook( WOContext context ) {
		super( context );
	}

	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );
		ERXResponseRewriter.addScriptResourceInHead( r, context(), Primary.frameworkBundleName(), "bootstrap_prototype_conflict_fix.js" );
	}

	public String frameworkBundleName() {
		return Primary.frameworkBundleName();
	}

	public USMenu menu() {
		return USMenu.defaultMenu();
	}

	public String siteName() {
		return "Helium 5";
		//		return SWSettings.name();
	}

	/**
	 * FIXME: This should be configurable. Should be null for a non-fluid layout
	 */
	public String bodyClass() {
		//		return "layout-fluid";
		return null;
	}

	public boolean showTopButtons() {
		return false;
	}

	public Object user() {
		return null;
	}

	public String avatarBackgroundStyle() {
		final String url = application().resourceManager().urlForResourceNamed( "images/avatar.png", "app", NSArray.emptyArray(), context().request() );
		return "background-image: url(%s)".formatted( url );
	}

	public WOActionResults menuItemClick() {
		return menuItem.action();
	}

	public String liClass() {
		if( !menuItem.children().isEmpty() ) {
			return "nav-item dropdown";
		}

		return "nav-item";
	}

	public String rootLinkClass() {
		if( !menuItem.children().isEmpty() ) {
			return "nav-link dropdown-toggle";
		}

		return "nav-link";
	}

	public String dataBsToggle() {
		if( menuItem.children().isEmpty() ) {
			return null;
		}

		return "dropdown";
	}

	public String dataBsAutoClose() {
		if( menuItem.children().isEmpty() ) {
			return null;
		}

		return "outside";
	}
}