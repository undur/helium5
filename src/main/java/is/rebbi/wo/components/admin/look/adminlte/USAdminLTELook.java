package is.rebbi.wo.components.admin.look.adminlte;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.ajax.AjaxUtils;
import is.rebbi.wo.Primary;
import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.menu.USMenu;

public class USAdminLTELook extends USViewPage {

	public USAdminLTELook( WOContext context ) {
		super( context );
	}

	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );
		// ERXResponseRewriter.insertInResponseBeforeHead( r, c, "<script type=\"text/javascript\"> $.noConflict(); </script>", TagMissingBehavior.SkipAndWarn );
		AjaxUtils.addScriptResourceInHead( context(), r, "helium", "bootstrap_prototype_conflict_fix.js" );
	}

	public String frameworkBundleName() {
		return Primary.frameworkBundleName();
	}

	public USMenu menu() {
		return USMenu.defaultMenu();
	}
}