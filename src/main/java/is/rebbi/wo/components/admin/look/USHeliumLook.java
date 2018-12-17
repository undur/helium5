package is.rebbi.wo.components.admin.look;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.ObjectSelect;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.ajax.AjaxUtils;
import er.extensions.appserver.ERXResponseRewriter;
import er.extensions.appserver.ERXResponseRewriter.TagMissingBehavior;
import er.extensions.crypting.ERXCrypto;
import is.rebbi.wo.Primary;
import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.components.admin.USAdminSearchResultsPage;
import is.rebbi.wo.menu.USMenu;
import is.rebbi.wo.util.SWSettings;
import jambalaya.interfaces.UniqueIDStamped;

public class USHeliumLook extends USViewPage {

	public String searchString;

	public USHeliumLook( WOContext context ) {
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
		AjaxUtils.addScriptResourceInHead( c, r, Primary.frameworkBundleName(), "helium/js/pages/index.js" );
		// AjaxUtils.addScriptResourceInHead( c, r, "app", "jquery-textcomplete/jquery.textcomplete.min.js" );

		ERXResponseRewriter.addResourceInHead( r, c, Primary.frameworkBundleName(), "helium/img/favicon.ico", "<link rel=\"shortcut icon\" href=\"", "\">" );

		addAppleTouchIcon( r, c, "helium/img/icon57.png", "57x57" );
		addAppleTouchIcon( r, c, "helium/img/icon72.png", "72x72" );
		addAppleTouchIcon( r, c, "helium/img/icon76.png", "76x76" );
		addAppleTouchIcon( r, c, "helium/img/icon114.png", "114x114" );
		addAppleTouchIcon( r, c, "helium/img/icon120.png", "120x120" );
		addAppleTouchIcon( r, c, "helium/img/icon144.png", "144x144" );
		addAppleTouchIcon( r, c, "helium/img/icon152.png", "152x152" );

		ERXResponseRewriter.insertInResponseBeforeHead( r, c, "<script type=\"text/javascript\"> $.noConflict(); </script>", TagMissingBehavior.SkipAndWarn );
		AjaxUtils.addScriptResourceInHead( context(), r, "helium", "bootstrap_prototype_conflict_fix.js" );
	}

	public WOActionResults search() {
		USAdminSearchResultsPage nextPage = pageWithName( USAdminSearchResultsPage.class );
		List results = new ArrayList<>();

		for( ObjEntity objEntity : oc().getEntityResolver().getObjEntities() ) {
			Class<?> clazz = objEntity.getJavaClass();
			Expression e = null;

			if( objEntity.getAttribute( "uuid" ) != null && !objEntity.isAbstract() ) {
				e = ExpressionFactory.matchExp( "uuid", searchString );
			}

			if( UniqueIDStamped.class.isAssignableFrom( clazz ) ) {
				e = ExpressionFactory.matchExp( "uniqueID", searchString );
			}

			if( e != null ) {
				results.addAll( ObjectSelect.query( clazz ).where( e ).select( oc() ) );
			}
		}

		nextPage.list = results;
		return nextPage;
	}

	private void addAppleTouchIcon( WOResponse r, WOContext c, String filename, String sizes ) {
		ERXResponseRewriter.addResourceInHead( r, c, Primary.frameworkBundleName(), filename, "<link rel=\"icon\" type=\"image/png\" href=\"", "\" sizes=\"" + sizes + "\">" );
	}

	public USMenu menu() {
		return USMenu.defaultMenu();
	}

	public String avatarSRC() {
		return "http://www.gravatar.com/avatar/" + ERXCrypto.md5Encode( "hugi@karlmenn.is" ) + "?s=120";
	}

	public String siteName() {
		return SWSettings.name();
	}
}