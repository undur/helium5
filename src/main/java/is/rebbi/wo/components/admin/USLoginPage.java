package is.rebbi.wo.components.admin;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.ajax.AjaxUtils;
import er.extensions.appserver.ERXResponseRewriter;
import er.extensions.appserver.ERXResponseRewriter.TagMissingBehavior;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.Primary;
import is.rebbi.wo.util.SWSettings;

public class USLoginPage extends ERXComponent {

	public String username;
	public String password;

	public USLoginPage( WOContext context ) {
		super( context );
	}

	@Override
	public void appendToResponse( WOResponse r, WOContext c ) {
		super.appendToResponse( r, c );
		AjaxUtils.addStylesheetResourceInHead( c, r, Primary.frameworkBundleName(), "helium/css/bootstrap.min.css" );
		AjaxUtils.addStylesheetResourceInHead( c, r, Primary.frameworkBundleName(), "helium/css/plugins.css" );
		AjaxUtils.addStylesheetResourceInHead( c, r, Primary.frameworkBundleName(), "helium/css/main.css" );
		AjaxUtils.addStylesheetResourceInHead( c, r, Primary.frameworkBundleName(), "helium/css/themes/night.css" );
		AjaxUtils.addStylesheetResourceInHead( c, r, Primary.frameworkBundleName(), "helium/css/themes.css" );
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/vendor/jquery-1.11.1.min.js" );
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/vendor/bootstrap.min.js" );
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/vendor/modernizr-2.7.1-respond-1.4.2.min.js" );
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/plugins.js" );
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/app.js" );
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/pages/login.js" );

		ERXResponseRewriter.addResourceInHead( r, c, Primary.frameworkBundleName(), "helium/img/favicon.ico", "<link rel=\"shortcut icon\" href=\"", "\">" );

		addAppleTouchIcon( r, c, "helium/img/icon57.png", "57x57" );
		addAppleTouchIcon( r, c, "helium/img/icon72.png", "72x72" );
		addAppleTouchIcon( r, c, "helium/img/icon76.png", "76x76" );
		addAppleTouchIcon( r, c, "helium/img/icon114.png", "114x114" );
		addAppleTouchIcon( r, c, "helium/img/icon120.png", "120x120" );
		addAppleTouchIcon( r, c, "helium/img/icon144.png", "144x144" );
		addAppleTouchIcon( r, c, "helium/img/icon152.png", "152x152" );

		ERXResponseRewriter.insertInResponseBeforeHead( r, c, "<script type=\"text/javascript\"> $.noConflict(); </script>", TagMissingBehavior.SkipAndWarn );
	}

	private void addAppleTouchIcon( WOResponse r, WOContext c, String filename, String sizes ) {
		ERXResponseRewriter.addResourceInHead( r, c, Primary.frameworkBundleName(), filename, "<link rel=\"icon\" type=\"image/png\" href=\"", "\" sizes=\"" + sizes + "\">" );
	}

	public WOActionResults login() {

		if( SWSettings.adminUsername().equals( username ) && SWSettings.adminPassword().equals( password ) ) {
			return pageWithName( USStartPage.class );
		}

		return null;
	}
}