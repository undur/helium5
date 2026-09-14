package is.rebbi.wo.components.admin.look.tabler;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

import er.extensions.appserver.ERXResponseRewriter;
import er.extensions.appserver.ERXResponseRewriter.TagMissingBehavior;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.Primary;
import is.rebbi.wo.menu.USMenu;
import is.rebbi.wo.menu.USMenuItem;

public class USTablerLook extends ERXComponent {

	public Object selectedObject;
	public String searchString;

	public USMenuItem menuItem;
	public USMenuItem subItem;
	public USMenuItem subSubItem;

	public USTablerLook( WOContext context ) {
		super( context );
	}

	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );

		boolean includeJQuery = true;

		if( includeJQuery ) {
			final String scriptString = """
					<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
					<script type="text/javascript">
						$.noConflict();
					</script>
							""";

			ERXResponseRewriter.insertInResponseBeforeTag( r, c, scriptString, "<title>", TagMissingBehavior.SkipAndWarn );
		}

		ERXResponseRewriter.addScriptResourceInHead( r, context(), Primary.frameworkBundleName(), "bootstrap_prototype_conflict_fix.js" );

	}

	/** The app's start page (concept.startPageName), the framework's when none is set */
	public WOActionResults startPage() {
		return pageWithName( is.rebbi.wo.util.USSettings.startPageName() );
	}

	public String frameworkBundleName() {
		return Primary.frameworkBundleName();
	}

	public USMenu menu() {
		return Primary.heliumMenu();
	}

	public String siteName() {
		return "Helium 5";
	}

	/**
	 * FIXME: This should be configurable. Should be null for a non-fluid layout
	 */
	public String bodyClass() {
		return "layout-fluid";
		//		return null;
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

	// --------------- Menu stuff starts here --------------- //

	public WOActionResults menuItemClick() {
		return menuItem.action();
	}

	public String liClass() {
		if( menuItem.hasChildren() ) {
			return "nav-item dropdown";
		}

		return "nav-item";
	}

	public String rootLinkClass() {
		if( menuItem.hasChildren() ) {
			return "nav-link dropdown-toggle";
		}

		return "nav-link";
	}

	public String dataBsToggle() {
		if( menuItem.hasChildren() ) {
			return "dropdown";
		}

		return null;
	}

	public String dataBsAutoClose() {
		if( menuItem.hasChildren() ) {
			return "outside";
		}

		return null;
	}
}